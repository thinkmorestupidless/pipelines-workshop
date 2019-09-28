import sbt._

object Versions {

  val akkaHTTPJSONVersion   = "10.1.8"
  val scalatestVersion       = "3.0.5"
}

import Versions._

object Dependencies {

  val akkaSprayJson   = "com.typesafe.akka"              %% "akka-http-spray-json"      % akkaHTTPJSONVersion
  val scalaTest       = "org.scalatest"                  %% "scalatest"                 % scalatestVersion    % "test"
}
