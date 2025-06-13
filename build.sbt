ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "BriScala",
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.13.2" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    )
  )
