package de.bripkens.hsa.reporting

import akka.actor.{ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.ConsoleReporterConfig

class ConsoleReporter(val mapper: ObjectMapper, val config: ConsoleReporterConfig) extends Actor
                                                                                   with ActorLogging {
  override def receive: Receive = {
    case Okay(component) => log.info(s"${component.name} is okay.")
    case SomethingIsWrong(component) => log.info(s"${component.name} has some issues.")
    case CannotReach(component) => log.info(s"${component.name} cannot be reached.")
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
