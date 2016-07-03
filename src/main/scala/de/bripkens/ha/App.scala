/*
 * Copyright 2016 Ben Ripkens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bripkens.ha

import java.nio.file.{ NoSuchFileException, Paths }

import akka.actor.ActorSystem
import com.fasterxml.jackson.databind.JsonMappingException

import scala.util.{ Failure, Success }

object App {

  def main(args: Array[String]) {
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
  }

  private def startActorSystem(configuration: Configuration) = {
    Console.out.println("Config successfully loaded. Initializing actor system.")

    implicit val system = ActorSystem("ha")

    // the AppActor gets us started from here on out
    system.actorOf(AppActor.props(mapper, configuration), AppActor.Name)
  }

  private def reportCriticalInitialisationError(msg: String): Unit = {
    Console.err.println(msg)
    System.exit(1)
  }
}
