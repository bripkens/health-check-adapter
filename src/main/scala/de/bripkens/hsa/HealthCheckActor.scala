package de.bripkens.hsa

import java.util.concurrent.TimeUnit

import akka.actor.Status.Failure
import akka.actor.{ActorRef, ActorLogging, Actor}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, HttpResponse, HttpRequest}
import akka.stream.scaladsl.ImplicitMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.reporting.{Okay, SomethingIsWrong, CannotReach}

import scala.concurrent.duration.Duration

class HealthCheckActor(val mapper: ObjectMapper,
                       val config: Configuration,
                       val endpoint: HealthCheckEndpoint,
                       val reporter: ActorRef) extends Actor
                                               with ActorLogging
                                               with ImplicitMaterializer {

  import context.dispatcher

  // make the pipeTo method available (requires the ExecutionContextExecutor)
  // on Future
  import akka.pattern.pipe

  private val http = Http(context.system)

  log.info(s"Scheduling health checks for ${endpoint.name} (${endpoint.url}) every ${endpoint.interval} milliseconds")
  context.system.scheduler.schedule(
    Duration.Zero,
    Duration.create(endpoint.interval, TimeUnit.MILLISECONDS),
    self,
    "check"
  )

  override def receive: Receive = {
    // TODO set short timeout
    case "check" => http.singleRequest(HttpRequest(uri = endpoint.url)).pipeTo(self)
    case HttpResponse(StatusCodes.OK, _, _, _) => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.OKAY)
    }
    case response: HttpResponse => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.UNHEALTHY)
    }
    // TODO handle request timeouts
    // TODO how can we differentiate between this and other errors?
    case failure: Failure => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.NOT_REACHABLE)
    }
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
