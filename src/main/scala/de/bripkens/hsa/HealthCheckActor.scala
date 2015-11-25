package de.bripkens.hsa

import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper

import scala.concurrent.duration.Duration

class HealthCheckActor(val mapper: ObjectMapper,
                       val config: Configuration,
                       val endpoint: HealthCheckEndpoint) extends Actor
                                                          with ActorLogging {

  import context.dispatcher

  log.info(s"Scheduling health checks for ${endpoint.url} every ${endpoint.interval} milliseconds")
  context.system.scheduler.schedule(
    Duration.Zero,
    Duration.create(endpoint.interval, TimeUnit.MILLISECONDS),
    self,
    "check"
  )

  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
