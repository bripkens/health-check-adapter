name := "health-check-adapter"

version := "1.0"

scalaVersion := "2.11.7"

val jacksonVersion = "2.6.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion

val akkaCoreVersion = "2.4.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaCoreVersion
libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % akkaCoreVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaCoreVersion % "test"

val akkaExtensionsVersion = "2.0-M1"
libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaExtensionsVersion

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

assemblyJarName in assembly := "health-check-adapter.jar"
mainClass in assembly := Some("de.bripkens.ha.App")