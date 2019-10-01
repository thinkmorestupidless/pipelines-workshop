import sbt._

object Versions {

  val akkaStreamsVersion    = "2.5.25"
  val akkaHTTPJSONVersion   = "10.1.8"
  val playJsonVersion       = "2.7.3"
  val slf4jSimpleVersion    = "1.7.28"
  val scalatestVersion      = "3.0.7"
  val junitVersion          = "4.11"
  val junitInterfaceVersion = "0.11"
}

import Versions._

object Dependencies {

  val akkaStreams         = "com.typesafe.akka"             %% "akka-stream"              % akkaStreamsVersion
  val akkaHttpSprayJson   = "com.typesafe.akka"             %% "akka-http-spray-json"     % akkaHTTPJSONVersion
  val playJson            = "com.typesafe.play"             %% "play-json"                % playJsonVersion
  val slf4jSimple         = "org.slf4j"                     %  "slf4j-simple"             % slf4jSimpleVersion
  val scalaTest           = "org.scalatest"                 %% "scalatest"                % scalatestVersion      % Test
  val junit               = "junit"                         %  "junit"                    % junitVersion          % Test
  val junitInterface      = "com.novocode"                  %  "junit-interface"          % junitInterfaceVersion % Test exclude("junit", "junit-dep")
}
