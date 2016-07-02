import sbt._

object Version {
  final val Scala          = "2.11.8"
  final val ScalaTest      = "3.0.0-RC2"
  final val JacksonVersion = "2.6.3"
}

object Library {

  object Jackson {
    val core            = "com.fasterxml.jackson.core"        %  "jackson-core"             % Version.JacksonVersion
    val databind        = "com.fasterxml.jackson.core"        %  "jackson-databind"         % Version.JacksonVersion
    val dataformatYaml  = "com.fasterxml.jackson.dataformat"  %  "jackson-dataformat-yaml"  % Version.JacksonVersion
    val moduleScala     = "com.fasterxml.jackson.module"      %% "jackson-module-scala"     % Version.JacksonVersion
  }

  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
