package de.bripkens.hsa.reporting

import akka.actor.{ActorLogging, Actor}
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.SlackReporterConfig

class SlackReporter(val mapper: ObjectMapper, val config: SlackReporterConfig) extends Actor
                                                                               with ActorLogging {
  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
