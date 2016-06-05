package de.bripkens.ha

import java.nio.file.{NoSuchFileException, Paths}

import com.fasterxml.jackson.databind.JsonMappingException
import org.scalatest.{Matchers, WordSpec, TryValues}

class ConfigurationSpec extends WordSpec with Matchers with TryValues {

  "Configuration" when {

    "loading a valid config file" should {

      val path = Paths.get(getClass.getResource("test-config.yml").toURI)
      val loaded = Configuration.load(path)
      loaded should be a 'success
      val configuration = loaded.success.value

      "have two endpoints" in {
        configuration.endpoints should have size 2
      }

      "have a shopping endpoint" in {
        val expected = HealthCheckEndpoint("http://127.0.0.1:8081/healthcheck", "shopping", "Shopping System", 3000, "mySlackReporter")
        configuration.endpoints should contain(expected)
      }

      "have a recommendation endpoint" in {
        val expected = HealthCheckEndpoint("http://127.0.0.1:8181/healthcheck", "recommendation", "Recommendation System", 10000, "myConsoleReporter")
        configuration.endpoints should contain(expected)
      }

      "have two reporters" in {
        configuration.reporters should have size 2
      }

      "have a slack reporter" in {
        val expected = SlackReporterConfig(null, "test", "https://hooks.slack.com/services/12345", "Health Check", "http://lorempixel.com/64/64/")

        configuration.reporters should contain key "mySlackReporter"
        configuration.reporters("mySlackReporter") should be(expected)
      }

      "have a console reporter" in {
        configuration.reporters should contain key "myConsoleReporter"
        configuration.reporters("myConsoleReporter") should be(ConsoleReporterConfig(null))
      }
    }

    "loading a non existent config file" should {

      "return NoSuchFileExcpetion" in {
        val path = Paths.get("/does/not/exist")

        val loaded = Configuration.load(path)
        loaded should be a 'failure
        loaded.failure.exception shouldBe a[NoSuchFileException]
      }
    }

    "loading a broken config file" should {

      "return JsonMappingException" in {
        val path = Paths.get(getClass.getResource("broken-config.yml").toURI)

        val loaded = Configuration.load(path)
        loaded should be a 'failure
        loaded.failure.exception shouldBe a[JsonMappingException]
      }
    }
  }
}
