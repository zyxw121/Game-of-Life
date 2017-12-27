import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "uk.co.danielmroz",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Life",
    libraryDependencies += scalaTest  % Test,
    libraryDependencies += scalaMock % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "1.0.1"
  )
