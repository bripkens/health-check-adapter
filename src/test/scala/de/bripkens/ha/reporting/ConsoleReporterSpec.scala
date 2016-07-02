/*
 * Copyright 2016 Ben Ripkens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bripkens.ha.reporting

import akka.actor.Props
import akka.testkit.EventFilter
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.{ ComponentStatus, ComponentStatusUpdate, ConsoleReporterConfig, HealthCheckEndpoint }

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
