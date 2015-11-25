package de.bripkens.hsa

import com.fasterxml.jackson.annotation.{JsonProperty, JsonCreator}

@JsonCreator
case class Configuration(
  @JsonProperty("endpoints")
  endpoints: List[HealthCheckEndpoint]
)

@JsonCreator
case class HealthCheckEndpoint(
  @JsonProperty("url")
  url: String
)
