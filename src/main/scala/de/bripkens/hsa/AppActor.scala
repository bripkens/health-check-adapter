package de.bripkens.hsa

import akka.actor.{Props, ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper

class AppActor(val mapper: ObjectMapper, val config: Configuration) extends Actor
                                                                    with ActorLogging {

  config.endpoints.foreach { endpoint =>
    context.actorOf(
      Props(classOf[HealthCheckActor], mapper, config, endpoint),
      "healthCheck"
    )
  }

  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
