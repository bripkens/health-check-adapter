package de.bripkens.hsa

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo, JsonProperty, JsonCreator}

@JsonCreator
case class Configuration(@JsonProperty("endpoints") endpoints: Set[HealthCheckEndpoint],
                         @JsonProperty("reporters") reporters: Map[String, AbstractReporter])

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
  new JsonSubTypes.Type(value = classOf[SlackReporter], name = "slack")
))
abstract class AbstractReporter(@JsonProperty("type") reporterType: String)

case class SlackReporter(@JsonProperty("type") reporterType: String,
                         @JsonProperty("channel") channel: String,
                         @JsonProperty("token") token: String,
                         @JsonProperty("botName") botName: String,
                         @JsonProperty("botImage") botImage: String)
    extends AbstractReporter(reporterType) {
}