name := "healthcheck-adapter"

version := "1.0"

scalaVersion := "2.11.7"

val jacksonVersion = "2.6.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion

val akkaCoreVersion = "2.4.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaCoreVersion
libraryDependencies += "com.typesafe.akka" %% "akka-contrib" % akkaCoreVersion

val akkaExtensionsVersion = "2.0-M1"
libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % akkaExtensionsVersion
libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaExtensionsVersion

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"