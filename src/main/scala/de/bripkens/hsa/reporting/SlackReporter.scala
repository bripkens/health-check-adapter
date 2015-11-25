package de.bripkens.hsa.reporting

import akka.actor.Actor
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.SlackReporterConfig

class SlackReporter(val mapper: ObjectMapper, val config: SlackReporterConfig) extends Actor {
  override def receive: Receive = ???
}
