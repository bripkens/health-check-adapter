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

package de.bripkens

import com.fasterxml.jackson.annotation.{ JsonProperty, JsonCreator }
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.bripkens.ha.ComponentStatus.ComponentStatus

package object ha {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  object ComponentStatus extends Enumeration {
    type ComponentStatus = Value
    val OKAY, UNHEALTHY, NOT_REACHABLE = Value
  }

  type HealthCheckResult = Map[String, HealthCheckEntry]
  case class HealthCheckEntry(healthy: Boolean, message: String = "")

  case class ComponentStatusUpdate(
    component: HealthCheckEndpoint,
    status: ComponentStatus,
    result: Option[HealthCheckResult] = None
  )
}
