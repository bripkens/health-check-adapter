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

import akka.actor.{ Props, ActorLogging, Actor }
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.ha.ComponentStatus.ComponentStatus
import de.bripkens.ha.{ ComponentStatus, ComponentStatusUpdate, ConsoleReporterConfig }

import scala.collection.mutable

object ConsoleReporter {

  def props(mapper: ObjectMapper, config: ConsoleReporterConfig) = Props(new ConsoleReporter(mapper, config))

}

class ConsoleReporter(val mapper: ObjectMapper, val config: ConsoleReporterConfig) extends Actor
    with ActorLogging {

  val componentStatus = new mutable.HashMap[String, ComponentStatus]()

  override def receive: Receive = {
    case ComponentStatusUpdate(component, ComponentStatus.OKAY, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.OKAY)) {
        log.info(s"${component.name} is okay.")
        componentStatus.put(component.id, ComponentStatus.OKAY)
      }
    }
    case ComponentStatusUpdate(component, ComponentStatus.UNHEALTHY, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.UNHEALTHY)) {
        log.info(s"${component.name} has some issues.")
        componentStatus.put(component.id, ComponentStatus.UNHEALTHY)
      }
    }
    case ComponentStatusUpdate(component, ComponentStatus.NOT_REACHABLE, _) => {
      if (!componentStatus.get(component.id).contains(ComponentStatus.NOT_REACHABLE)) {
        log.info(s"${component.name} cannot be reached.")
        componentStatus.put(component.id, ComponentStatus.NOT_REACHABLE)
      }
    }
  }

}
