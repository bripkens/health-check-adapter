import sbt._

object Version {
  final val Scala          = "2.11.8"
  final val ScalaTest      = "3.0.0-RC2"
  final val JacksonVersion = "2.6.3"
  final val AkkaVersion    = "2.4.2"
}

object Library {

  object Jackson {
    val core            = "com.fasterxml.jackson.core"        %  "jackson-core"             % Version.JacksonVersion
    val databind        = "com.fasterxml.jackson.core"        %  "jackson-databind"         % Version.JacksonVersion
    val dataformatYaml  = "com.fasterxml.jackson.dataformat"  %  "jackson-dataformat-yaml"  % Version.JacksonVersion
    val moduleScala     = "com.fasterxml.jackson.module"      %% "jackson-module-scala"     % Version.JacksonVersion
  }

  object Akka {
    val actor   = "com.typesafe.akka" %% "akka-actor"   % Version.AkkaVersion
    val contrib = "com.typesafe.akka" %% "akka-contrib" % Version.AkkaVersion
    val testkit = "com.typesafe.akka" %% "akka-testkit" % Version.AkkaVersion
  }

  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
}
