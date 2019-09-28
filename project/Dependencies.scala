import sbt._

object Versions {

  val akkaHTTPJSONVersion   = "10.1.8"
  val scalatestVersion      = "3.0.5"
  val playJsonVersion       = "2.7.3"
}

import Versions._

object Dependencies {

  val akkaSprayJson   = "com.typesafe.akka"              %% "akka-http-spray-json"      % akkaHTTPJSONVersion
  val playJson        = "com.typesafe.play"              %% "play-json"                 % playJsonVersion
  val scalaTest       = "org.scalatest"                  %% "scalatest"                 % scalatestVersion      % "test"
}
