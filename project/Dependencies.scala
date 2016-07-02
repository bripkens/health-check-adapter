import sbt._

object Version {
  final val Scala          = "2.11.8"
  final val ScalaTest      = "3.0.0-RC2"
  final val JacksonVersion = "2.6.3"
}

object Library {
  val jacksonCore = "com.fasterxml.jackson.core" % "jackson-core" % Version.JacksonVersion
  val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % Version.JacksonVersion
  val jacksonDataformantYaml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % Version.JacksonVersion
  val jacksonModuleScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % Version.JacksonVersion

  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
