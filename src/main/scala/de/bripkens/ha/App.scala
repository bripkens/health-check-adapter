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

  Configuration.load(Paths.get(configPath)) match {
    case Left(e: NoSuchFileException) => reportCriticalInitialisationError(
      s"Config file $configPath does not exist."
    )
    case Left(e: JsonMappingException) => reportCriticalInitialisationError(
      s"Config file $configPath could not be parsed. Error: ${e.getMessage}"
    )
    case Right(configuration) => startActorSystem(configuration)
  }

  def startActorSystem(configuration: Configuration) = {
    Console.out.println("Config successfully loaded. Initializing actor system.")

    implicit val system = ActorSystem("ha")

    // the AppActor gets us started from here on out
    system.actorOf(Props(classOf[AppActor], mapper, configuration), "app")
  }

  def reportCriticalInitialisationError(msg: String): Unit = {
    Console.err.println(msg)
    System.exit(1)
  }
}
