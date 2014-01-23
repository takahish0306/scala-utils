import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Utils extends Build {

  val Organization = "scala.utils"
  val Name = "Utils"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.2"
 
  val baseSettings = Seq(
    version := Version,
    organization := Organization,
    scalaVersion := ScalaVersion,
    name := Name,
    resolvers += Classpaths.typesafeReleases,
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.8.1" % "test" withSources(),
      "org.scalatest" %% "scalatest" % "1.9.1" % "test",
      "org.scala-tools.testing" %% "specs" % "1.6.9" % "test" withSources()
    ),
    scalacOptions ++= Seq("-encoding", "utf8", "-unchecked", "-deprecation")
  )

  lazy val utils = Project(
    id = "utils",
    base = file("."),
    settings = Project.defaultSettings ++
      baseSettings ++
      assemblySettings
  ).settings(
    name := "utils",
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.4",
      "org.apache.httpcomponents" % "httpclient" % "4.3.2"
    )
  )

}
