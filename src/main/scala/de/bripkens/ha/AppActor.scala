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

package de.bripkens.ha

import akka.actor.{ Actor, ActorLogging, Props }
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.reporting.{ ConsoleReporter, SlackReporter }

object AppActor {

  final val Name = "app"

  def props(mapper: ObjectMapper, config: Configuration) = Props(new AppActor(mapper, config))
}

class AppActor(val mapper: ObjectMapper, val config: Configuration) extends Actor
    with ActorLogging {

  val reporters = config.reporters.map {
    case (name, reporterConfig) =>
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
