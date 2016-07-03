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

import java.util.concurrent.TimeUnit

import akka.actor.Status.Failure
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, StatusCodes }
import akka.pattern.after
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import com.fasterxml.jackson.databind.ObjectMapper

import scala.concurrent.duration._
import scala.concurrent.{ Future, TimeoutException }

object HealthCheckActor {

  def props(
    mapper: ObjectMapper,
    config: Configuration,
    endpoint: HealthCheckEndpoint,
    reporter: ActorRef
  ) = Props(new HealthCheckActor(mapper, config, endpoint, reporter))
}

class HealthCheckActor(
  val mapper: ObjectMapper,
  val config: Configuration,
  val endpoint: HealthCheckEndpoint,
  val reporter: ActorRef
) extends Actor
    with ActorLogging {

  import context.dispatcher

  // make the pipeTo method available (requires the ExecutionContextExecutor)
  // on Future
  import akka.pattern.pipe

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  private val http = Http(context.system)

  log.info(s"Scheduling health checks for ${endpoint.name} (${endpoint.url}) every ${endpoint.interval} milliseconds")
  context.system.scheduler.schedule(
    Duration.Zero,
    Duration.create(endpoint.interval, TimeUnit.MILLISECONDS),
    self,
    "check"
  )

  override def receive: Receive = {
    case "check" => {
      // okay, is this truly the way to do timeouts with Akka? Feels hackish…
      Future.firstCompletedOf(
        http.singleRequest(HttpRequest(uri = endpoint.url)) ::
          after(2.second, context.system.scheduler)(Future.failed(new TimeoutException)) ::
          Nil
      ).pipeTo(self)
    }
    case HttpResponse(StatusCodes.OK, _, _, _) => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.OKAY)
    }
    case response: HttpResponse => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.UNHEALTHY)
    }
    // TODO how can we differentiate between this and other errors?
    case failure: Failure => {
      reporter ! ComponentStatusUpdate(endpoint, ComponentStatus.NOT_REACHABLE)
    }
  }
}
