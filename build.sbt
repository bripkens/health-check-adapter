name := "health-check-adapter"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Vector(
  Library.Jackson.core,
  Library.Jackson.databind,
  Library.Jackson.dataformatYaml,
  Library.Jackson.moduleScala,

  Library.Akka.actor,
  Library.Akka.contrib,
  Library.Akka.stream,
  Library.Akka.httpCore,
  Library.Akka.http,
  Library.Akka.testkit % "test",

  Library.scalaTest % "test"
)

assemblyJarName in assembly := "health-check-adapter.jar"
mainClass in assembly := Some("de.bripkens.ha.App")