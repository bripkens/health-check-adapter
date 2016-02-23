package de.bripkens.ha

import java.nio.file.{Paths, NoSuchFileException}

import akka.actor.{ActorSystem, Props}
import com.fasterxml.jackson.databind.JsonMappingException

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

  implicit val system = ActorSystem("ha")

  // the AppActor gets us started from here on out
  system.actorOf(Props(classOf[AppActor], mapper, configuration), "app")

  def loadConfig(rawPath: String): Configuration = {
    Configuration.load(Paths.get(rawPath)) match {
      case Left(_: NoSuchFileException) => reportCriticalInitialisationError(
        s"Config file $rawPath does not exist."
      )
      null
      case Left(e: JsonMappingException) => reportCriticalInitialisationError(
        s"Config file $rawPath could not be parsed. Error: ${e.getMessage}"
      )
      null
      case Right(conf) => conf
    }
  }

  def reportCriticalInitialisationError(msg: String): Unit = {
    Console.err.println(msg)
    System.exit(1)
  }
}
