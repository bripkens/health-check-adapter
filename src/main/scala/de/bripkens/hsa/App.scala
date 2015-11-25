package de.bripkens.hsa

import java.nio.file.{NoSuchFileException, Paths, Files}
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
  println(configuration)

  def loadConfig(rawPath: String): Configuration = {
    try {
      val path = Paths.get(rawPath)

      val mapper = new ObjectMapper(new YAMLFactory())
      mapper.registerModule(DefaultScalaModule)

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
