package de.bripkens.hsa

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonProperty, JsonCreator}
import de.bripkens.hsa.reporting.{ConsoleReporter, SlackReporter}

@JsonCreator
case class Configuration(@JsonProperty("endpoints") endpoints: Set[HealthCheckEndpoint],
                         @JsonProperty("reporters") reporters: Map[String, AbstractReporterConfig])

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
                               @JsonProperty("token") token: String,
                               @JsonProperty("botName") botName: String,
                               @JsonProperty("botImage") botImage: String)
    extends AbstractReporterConfig(reporterType) {
  override val implementation = classOf[SlackReporter]
}

case class ConsoleReporterConfig(@JsonProperty("type") reporterType: String)
    extends AbstractReporterConfig(reporterType) {
  override val implementation = classOf[ConsoleReporter]
}