package de.bripkens.ha

import java.nio.file.{Files, Path}

import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.util.Try

object Configuration {

  def load(path: Path): Try[Configuration] = {
    val yamlMapper = new ObjectMapper(new YAMLFactory())
    yamlMapper.registerModule(DefaultScalaModule)

    Try({
      val content = String.join("\n", Files.readAllLines(path))
      yamlMapper.readValue(content, classOf[Configuration])
    })
  }
}

case class Configuration(@JsonProperty("endpoints") endpoints: Set[HealthCheckEndpoint],
                         @JsonProperty("reporters") reporters: Map[String, ReporterConfig],
                         @JsonProperty("akka") akkaConfig: Map[String, _ <: AnyRef])

case class HealthCheckEndpoint(@JsonProperty("url") url: String,
                               @JsonProperty("id") id: String,
                               @JsonProperty("name") name: String,
                               @JsonProperty("interval") interval: Int,
                               @JsonProperty("reporter") reporter: String)

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes(value = Array(
  new JsonSubTypes.Type(value = classOf[SlackReporterConfig], name = "slack"),
  new JsonSubTypes.Type(value = classOf[ConsoleReporterConfig], name = "console")
))
sealed trait ReporterConfig

case class SlackReporterConfig(@JsonProperty("type") reporterType: String,
                               @JsonProperty("channel") channel: String,
                               @JsonProperty("webhookUrl") webhookUrl: String,
                               @JsonProperty("botName") botName: String,
                               @JsonProperty("botImage") botImage: String)
  extends ReporterConfig

case class ConsoleReporterConfig(@JsonProperty("type") reporterType: String)
  extends ReporterConfig
