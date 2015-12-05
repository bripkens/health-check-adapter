package de.bripkens.hsa.reporting

import akka.actor.{ActorLogging, Actor}
import akka.http.scaladsl.Http
import akka.stream.scaladsl.ImplicitMaterializer
import com.fasterxml.jackson.databind.ObjectMapper
import de.bripkens.hsa.SlackReporterConfig

class SlackReporter(val mapper: ObjectMapper, val config: SlackReporterConfig) extends Actor
                                                                               with ActorLogging
                                                                               with ImplicitMaterializer {
  import context.dispatcher

  // make the pipeTo method available (requires the ExecutionContextExecutor)
  // on Future
  import akka.pattern.pipe

  private val http = Http(context.system)

  override def receive: Receive = {
    case unsupported => log.error(s"Unsupported message received: $unsupported")
  }
}
