package de.bripkens.ha.reporting

import akka.actor.{Props, ActorLogging, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.ImplicitMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.ComponentStatus._
import de.bripkens.ha.{HealthCheckEndpoint, ComponentStatus, ComponentStatusUpdate, SlackReporterConfig}

import scala.collection.mutable

object SlackReporter {

  def props(mapper: ObjectMapper, config: SlackReporterConfig) = Props(new SlackReporter(mapper, config))

}

class SlackReporter(val mapper: ObjectMapper, val config: SlackReporterConfig) extends Actor
                                                                               with ActorLogging
                                                                               with ImplicitMaterializer {

  import context.dispatcher

  // make the pipeTo method available (requires the ExecutionContextExecutor)
  // on Future
  import akka.pattern.pipe

  val componentStatus = new mutable.HashMap[String, ComponentStatus]()

  private val http = Http(context.system)

  override def receive: Receive = {
    case ComponentStatusUpdate(component, ComponentStatus.OKAY, _) => {
      onStatusChange(component, ComponentStatus.OKAY, onOkay)
    }
    case ComponentStatusUpdate(component, ComponentStatus.UNHEALTHY, _) => {
      onStatusChange(component, ComponentStatus.UNHEALTHY, onUnhealthy)
    }
    case ComponentStatusUpdate(component, ComponentStatus.NOT_REACHABLE, _) => {
      onStatusChange(component, ComponentStatus.NOT_REACHABLE, onUnreachable)
    }
    case response: HttpResponse => {
      if (response.status != StatusCodes.OK) {
        log.warning(s"Retrieved status code ${response.status} from the Slack API")
      }
    }
  }

  def onStatusChange(component: HealthCheckEndpoint,
                     status: ComponentStatus,
                     onChange: (HealthCheckEndpoint) => Unit): Unit = {
    if (!componentStatus.get(component.id).contains(status)) {
      componentStatus.put(component.id, status)
      onChange(component)
    }
  }

  def onOkay(component: HealthCheckEndpoint): Unit = {
    sendToSlack(Map(
      "color" -> "good",
      "text" -> s"<${component.url}|${component.name}> is now okay.",
      "fallback" -> s"${component.name} is now okay (${component.url})."
    ))
  }

  def onUnreachable(component: HealthCheckEndpoint): Unit = {
    sendToSlack(Map(
      "color" -> "danger",
      "text" -> s"<${component.url}|${component.name}> is not reachable.",
      "fallback" -> s"${component.name} is not reachable (${component.url})."
    ))
  }

  def onUnhealthy(component: HealthCheckEndpoint): Unit = {
    // TODO add debug output, i.e. JSON from health endpoint
    sendToSlack(Map(
      "color" -> "danger",
      "text" -> s"<${component.url}|${component.name}> is not healthy.",
      "fallback" -> s"${component.name} is not healthy (${component.url})."
    ))
  }

  def sendToSlack(attachment: Map[String, Any]): Unit = {
    val payload = Map(
      "text" -> "Component health changed:",
      "username" -> config.botName,
      "icon_url" -> config.botImage,
      "channel" -> config.channel,
      "attachments" -> List(attachment)
    )
    val entity = HttpEntity(ContentType(MediaTypes.`application/json`),
      mapper.writeValueAsString(payload))
    http.singleRequest(HttpRequest(uri = config.webhookUrl, entity = entity)).pipeTo(self)
  }

}
