package de.bripkens.hsa

import java.nio.file.{NoSuchFileException, Paths, Files}
import akka.actor.{Props, ActorSystem}
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object App extends scala.App {

  if (args.length != 1) {
    reportCriticalInitialisationError(
      "Please specify exactly one parameter: The path to the config file."
    )
  }

  val configPath = args(0)
  Console.out.println(s"Starting with config file $configPath")

  val configuration = loadConfig(configPath)
  Console.out.println("Config successfully loaded. Initializing actor system.")

  implicit val system = ActorSystem("release-notifier")

  // the AppActor gets us started from here on out
  system.actorOf(Props(classOf[AppActor], mapper, configuration), "app")

  def loadConfig(rawPath: String): Configuration = {
    try {
      val path = Paths.get(rawPath)
      val content = String.join("\n", Files.readAllLines(path))
      mapper.readValue(content, classOf[Configuration])
    } catch {
      case e: NoSuchFileException => reportCriticalInitialisationError(
        s"Config file $rawPath does not exist."
      )
      null
      case e: JsonMappingException => reportCriticalInitialisationError(
        s"Config file $rawPath could not be parsed. Error: ${e.getMessage}"
      )
      null
    }
  }

  def reportCriticalInitialisationError(msg: String): Unit = {
    Console.err.println(msg)
    System.exit(1)
  }
}
