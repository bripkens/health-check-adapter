package de.bripkens

import com.fasterxml.jackson.annotation.{JsonProperty, JsonCreator}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.bripkens.hsa.ComponentStatus.ComponentStatus

package object hsa {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  object ComponentStatus extends Enumeration {
    type ComponentStatus = Value
    val OKAY, UNHEALTHY, NOT_REACHABLE = Value
  }

  type HealthCheckResult = Map[String, HealthCheckEntry]
  case class HealthCheckEntry(healthy: Boolean, message: String = "")

  case class ComponentStatusUpdate(component: HealthCheckEndpoint,
                                   status: ComponentStatus,
                                   result: Option[HealthCheckResult] = None)
}
