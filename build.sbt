name := "healthcheck-statuspage-adapter"

version := "1.0"

scalaVersion := "2.11.7"

val jacksonVersion = "2.6.3"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion