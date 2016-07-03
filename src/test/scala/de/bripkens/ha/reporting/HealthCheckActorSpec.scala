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
import akka.actor.Status.Failure
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.testkit.TestProbe
import de.bripkens.ha._

class HealthCheckActorSpec extends BaseAkkaSpec {

  "A HealthCheckActor" should {
    val config = Configuration(Set.empty, Map.empty, Map.empty)
    val endpoint = HealthCheckEndpoint("127.0.0.1", "localhost", "localhost", 2000, "TestProbe")

    "should send reporter OKAY status when endpoint returns status OK" in {
      val reporter = TestProbe()

      val healthCheckActor = system.actorOf(Props(new HealthCheckActor(mapper, config, endpoint, reporter.ref)))

      healthCheckActor ! HttpResponse(StatusCodes.OK)
      reporter.expectMsg(ComponentStatusUpdate(endpoint, ComponentStatus.OKAY))
    }

    "should send reporter UNHEALTHY status when endpoint does not return status OK" in {
      val reporter = TestProbe()

      val healthCheckActor = system.actorOf(Props(new HealthCheckActor(mapper, config, endpoint, reporter.ref)))

      healthCheckActor ! HttpResponse(StatusCodes.GatewayTimeout)
      reporter.expectMsg(ComponentStatusUpdate(endpoint, ComponentStatus.UNHEALTHY))
    }

    "should send reporter NOT_REACHABLE status when request to endpoint fails" in {
      val reporter = TestProbe()

      val healthCheckActor = system.actorOf(Props(new HealthCheckActor(mapper, config, endpoint, reporter.ref)))

      healthCheckActor ! Failure
      reporter.expectMsg(ComponentStatusUpdate(endpoint, ComponentStatus.NOT_REACHABLE))
    }
  }
}
