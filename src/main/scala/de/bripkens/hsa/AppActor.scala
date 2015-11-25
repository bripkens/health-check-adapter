package de.bripkens.hsa

import akka.actor.{Props, ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper

class AppActor(val mapper: ObjectMapper, val config: Configuration) extends Actor
                                                                    with ActorLogging {

  val reporters = config.reporters.map { case (name, config) =>
    val actor = context.actorOf(
      Props(config.implementation, mapper, config),
      s"reporter-$name"
    )
    (name, actor)
  }

  config.endpoints.foreach { endpoint =>
    if (!reporters.contains(endpoint.reporter)) {
      log.error(s"Reporter ${endpoint.reporter} does not exist.")
      System.exit(1)
    }
    val reporter = reporters(endpoint.reporter)
    context.actorOf(
      Props(classOf[HealthCheckActor], mapper, config, endpoint, reporter),
      s"healthCheck-${endpoint.id}"
    )
  }

  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
