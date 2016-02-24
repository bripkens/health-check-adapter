package de.bripkens.ha

import java.nio.file.{Files, NoSuchFileException, Path}

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty, JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import de.bripkens.ha.reporting.{ConsoleReporter, SlackReporter}

import scala.util.control.Exception._

object Configuration {

  def load(path: Path): Either[Throwable, Configuration] = {
    val yamlMapper = new ObjectMapper(new YAMLFactory())
    yamlMapper.registerModule(DefaultScalaModule)

    catching(classOf[NoSuchFileException], classOf[JsonMappingException]) either {
      val content = String.join("\n", Files.readAllLines(path))
      yamlMapper.readValue(content, classOf[Configuration])
    }
  }
}

@JsonCreator
case class Configuration(@JsonProperty("endpoints") endpoints: Set[HealthCheckEndpoint],
                         @JsonProperty("reporters") reporters: Map[String, AbstractReporterConfig],
                         @JsonProperty("akka") akkaConfig: Map[String, _ <: AnyRef])

@JsonCreator
case class HealthCheckEndpoint(@JsonProperty("url") url: String,
                               @JsonProperty("id") id: String,
                               @JsonProperty("name") name: String,
                               @JsonProperty("interval") interval: Int,
                               @JsonProperty("reporter") reporter: String)

@JsonCreator
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes(value = Array(
  new JsonSubTypes.Type(value = classOf[SlackReporterConfig], name = "slack"),
  new JsonSubTypes.Type(value = classOf[ConsoleReporterConfig], name = "console")
))
abstract class AbstractReporterConfig(@JsonProperty("type") reporterType: String) {
  val implementation: Class[_]
}

case class SlackReporterConfig(@JsonProperty("type") reporterType: String,
                               @JsonProperty("channel") channel: String,
                               @JsonProperty("webhookUrl") webhookUrl: String,
                               @JsonProperty("botName") botName: String,
                               @JsonProperty("botImage") botImage: String)
  extends AbstractReporterConfig(reporterType) {
  override val implementation = classOf[SlackReporter]
}

case class ConsoleReporterConfig(@JsonProperty("type") reporterType: String)
  extends AbstractReporterConfig(reporterType) {
  override val implementation = classOf[ConsoleReporter]
}