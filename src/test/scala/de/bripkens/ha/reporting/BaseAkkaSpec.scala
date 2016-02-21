package de.bripkens.ha.reporting

import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class BaseAkkaSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  implicit protected val system = ActorSystem("ha")

  override protected def afterAll() = {
    Await.ready(system.terminate(), Duration.Inf)
    super.afterAll()
  }
}
