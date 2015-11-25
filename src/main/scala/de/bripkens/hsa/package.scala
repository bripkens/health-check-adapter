package de.bripkens

import com.fasterxml.jackson.annotation.{JsonProperty, JsonCreator}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

package object hsa {

  val mapper = new ObjectMapper(new YAMLFactory())
  mapper.registerModule(DefaultScalaModule)

  type HealthCheckResult = Map[String, HealthCheckEntry]
  case class HealthCheckEntry(healthy: Boolean, message: String = "")

}
