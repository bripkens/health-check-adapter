name := "health-check-adapter"

version := "1.0"

scalaVersion := "2.11.7"

val akkaExtensionsVersion = "2.0.3"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaExtensionsVersion

libraryDependencies ++= Vector(
  Library.Jackson.core,
  Library.Jackson.databind,
  Library.Jackson.dataformatYaml,
  Library.Jackson.moduleScala,

  Library.Akka.actor,
  Library.Akka.contrib,
  Library.Akka.testkit % "test",

  Library.scalaTest % "test"
)

assemblyJarName in assembly := "health-check-adapter.jar"
mainClass in assembly := Some("de.bripkens.ha.App")