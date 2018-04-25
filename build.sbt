
name := "sequencer-sample-scripts"

version := "0.1"

isSnapshot := true

organization := "org.tmt"

scalaVersion := "2.12.4"
libraryDependencies ++= Seq(Ammonite.`ammonite`, Libs.`sequencer-framework`)
enablePlugins(JavaAppPackaging)

publishTo := {
  val base = "http://localhost:8080/maven/"
  if (version.value.endsWith("SNAPSHOT")) Some("snapshots" at base + "snapshots")
  else                                    Some("releases"  at base + "releases")
}