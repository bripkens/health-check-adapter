package de.bripkens.ha

import java.nio.file.{NoSuchFileException, Paths}

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.JsonMappingException

import scala.util.{Failure, Success}

object App extends scala.App {

  if (args.length != 1) {
    reportCriticalInitialisationError(
      "Please specify exactly one parameter: The path to the config file."
    )
  }

  val configPath = args(0)
  Console.out.println(s"Starting with config file $configPath")

  Configuration.load(Paths.get(configPath)) match {
    case Success(configuration) => startActorSystem(configuration)
    case Failure(e: NoSuchFileException) => reportCriticalInitialisationError(
      s"Config file $configPath does not exist."
    )
    case Failure(e: JsonMappingException) => reportCriticalInitialisationError(
      s"Config file $configPath could not be parsed. Error: ${e.getMessage}"
    )
  }

  def startActorSystem(configuration: Configuration) = {
    Console.out.println("Config successfully loaded. Initializing actor system.")

    implicit val system = ActorSystem("ha")

    // the AppActor gets us started from here on out
    system.actorOf(AppActor.props(mapper, configuration), AppActor.Name)
  }

  def reportCriticalInitialisationError(msg: String): Unit = {
    Console.err.println(msg)
    System.exit(1)
  }
}
