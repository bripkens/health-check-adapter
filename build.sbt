name := "health-check-adapter"

version := "1.0"

scalaVersion := "2.11.7"

val akkaCoreVersion = "2.4.2"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaCoreVersion
libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % akkaCoreVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaCoreVersion % "test"

val akkaExtensionsVersion = "2.0.3"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaExtensionsVersion

libraryDependencies ++= Vector(
  Library.jacksonCore,
  Library.jacksonDatabind,
  Library.jacksonDataformantYaml,
  Library.jacksonModuleScala,

  Library.scalaTest % "test"
)

assemblyJarName in assembly := "health-check-adapter.jar"
mainClass in assembly := Some("de.bripkens.ha.App")