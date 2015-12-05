package de.bripkens.hsa.reporting

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.actor.{ActorLogging, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.ImplicitMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.ComponentStatus._
import de.bripkens.hsa.{HealthCheckEndpoint, ComponentStatus, ComponentStatusUpdate, SlackReporterConfig}

import scala.collection.mutable

class SlackReporter(val mapper: ObjectMapper, val config: SlackReporterConfig) extends Actor
                                                                               with ActorLogging
                                                                               with ImplicitMaterializer {

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
    case unsupported => log.error(s"Unsupported message received: $unsupported")
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
    // TODO handle potential request errors and log to console
    http.singleRequest(HttpRequest(uri = config.webhookUrl, entity = entity))
  }

}
