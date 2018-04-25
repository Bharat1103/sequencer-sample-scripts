import sbt._

object Libs {
  val version = "0.1.0-SNAPSHOT"
  val `sequencer-framework` = "org.tmt" %% "sequencer-framework" % version
}

object Ammonite {
  val Version = "1.1.0"

  val `ammonite` = "com.lihaoyi" % "ammonite" % Version cross CrossVersion.full
  val `ammonite-sshd` = "com.lihaoyi" % "ammonite-sshd" % Version cross CrossVersion.full
}

object Akka {
  val Version = "2.5.11"

  val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % Version
  val `akka-typed` = "com.typesafe.akka" %% "akka-actor-typed" % Version
  val `akka-typed-testkit` = "com.typesafe.akka" %% "akka-testkit-typed" % Version
}
