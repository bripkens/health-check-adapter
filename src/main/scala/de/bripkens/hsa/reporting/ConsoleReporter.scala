package de.bripkens.hsa.reporting

import akka.actor.{ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.ComponentStatus.ComponentStatus
import de.bripkens.hsa.{ComponentStatus, ComponentStatusUpdate, ConsoleReporterConfig}

import scala.collection.mutable

class ConsoleReporter(val mapper: ObjectMapper, val config: ConsoleReporterConfig) extends Actor
                                                                                   with ActorLogging {

  val componentStatus = new mutable.HashMap[String, ComponentStatus]()

  override def receive: Receive = {
    case ComponentStatusUpdate(component, ComponentStatus.OKAY, _) => {
      if (componentStatus.get(component.id) != ComponentStatus.OKAY) {
        log.info(s"${component.name} is okay.")
        componentStatus.put(component.id, ComponentStatus.OKAY)
      }
    }
    case ComponentStatusUpdate(component, ComponentStatus.UNHEALTHY, _) => {
      log.info(s"${component.name} has some issues.")
    }
    case ComponentStatusUpdate(component, ComponentStatus.NOT_REACHABLE, _) => {
      log.info(s"${component.name} cannot be reached.")
    }
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
