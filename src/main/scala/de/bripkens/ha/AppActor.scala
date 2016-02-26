package de.bripkens.ha

import akka.actor.{Actor, ActorLogging, Props}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.reporting.{ConsoleReporter, SlackReporter}

object AppActor {

  final val Name = "app"

  def props(mapper: ObjectMapper, config: Configuration) = Props(new AppActor(mapper, config))
}

class AppActor(val mapper: ObjectMapper, val config: Configuration) extends Actor
                                                                    with ActorLogging {

  val reporters = config.reporters.map{ case (name, reporterConfig) =>
    val props = reporterConfig match {
      case config: ConsoleReporterConfig => ConsoleReporter.props(mapper, config)
      case config: SlackReporterConfig   => SlackReporter.props(mapper, config)
    }

    val actor = context.actorOf(props, s"reporter-$name")
    (name, actor)
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

  override def receive = Actor.emptyBehavior

}
