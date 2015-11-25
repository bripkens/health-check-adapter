package de.bripkens.hsa

package object reporting {
  case class Okay(component: HealthCheckEndpoint)

  case class SomethingIsWrong(component: HealthCheckEndpoint)

  case class CannotReach(component: HealthCheckEndpoint)
}
