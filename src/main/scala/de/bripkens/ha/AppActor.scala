package de.bripkens.ha

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.reporting.{ConsoleReporter, SlackReporter}

object AppActor {

  final val Name = "app"

  def props(mapper: ObjectMapper, config: Configuration) = Props(new AppActor(mapper, config))
}

class AppActor(val mapper: ObjectMapper, val config: Configuration) extends Actor
                                                                    with ActorLogging {

  val reporters = config.reporters.map(config2Actor)

  def config2Actor(entry: (String, AbstractReporterConfig)): (String, ActorRef) = {
    val props = entry._2 match {
      case config@ConsoleReporterConfig(_)           => ConsoleReporter.props(mapper, config)
      case config@SlackReporterConfig(_, _, _, _, _) => SlackReporter.props(mapper, config)
    }

    val actor = context.actorOf(props, s"reporter-${entry._1}")
    (entry._1, actor)
  }

  config.endpoints.foreach { endpoint =>
    if (!reporters.contains(endpoint.reporter)) {
      log.error(s"Reporter ${endpoint.reporter} does not exist.")
      System.exit(1)
    }
    val reporter = reporters(endpoint.reporter)
    context.actorOf(
      HealthCheckActor.props(mapper, config, endpoint, reporter),
      s"healthCheck-${endpoint.id}"
    )
  }

  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
