lazy val healthCheckAdapter = project
  .copy(id = "health-check-adapter")
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

name := "health-check-adapter"

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

initialCommands := """|import de.bripkens.ha._
                      |""".stripMargin
