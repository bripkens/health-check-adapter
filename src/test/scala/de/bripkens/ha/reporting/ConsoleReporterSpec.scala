package de.bripkens.ha.reporting

import akka.actor.Props
import akka.testkit.EventFilter
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.{ComponentStatus, ComponentStatusUpdate, ConsoleReporterConfig, HealthCheckEndpoint}

class ConsoleReporterSpec extends BaseAkkaSpec {

  val endpoint = new HealthCheckEndpoint("http://www.example.com", "12345", "test-endpoint", 2, "console")

  "A ConsoleReporter actor" should {

    "log health state of an endpoint" in {
      val reporter = system.actorOf(Props(new ConsoleReporter(new ObjectMapper, new ConsoleReporterConfig(""))))

      EventFilter.info(occurrences = 1, pattern = ".*test-endpoint is okay\\.").intercept {
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.OKAY)
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.OKAY)
      }

      EventFilter.info(occurrences = 1, pattern = ".*test-endpoint has some issues\\.").intercept {
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.UNHEALTHY)
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.UNHEALTHY)
      }

      EventFilter.info(occurrences = 1, pattern = ".*test-endpoint cannot be reached\\.").intercept {
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.NOT_REACHABLE)
        reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.NOT_REACHABLE)
      }

    }

    "log a warning when it receives an unknown message" in {
      val reporter = system.actorOf(Props(new ConsoleReporter(new ObjectMapper, new ConsoleReporterConfig(""))))

      EventFilter.warning(occurrences = 1).intercept {
        reporter ! "unknown message"
      }
    }
  }
}
