import sbt._
import sbt.Keys._
import scalariform.formatter.preferences._
import Dependencies._

lazy val thisVersion = "1.0.0"
version in ThisBuild := thisVersion
fork := true

val user = sys.props.getOrElse("user.name", "unknown-user")

lazy val `pipelines-workshop-java` = (project in file("."))
  .aggregate(`schemae-java`, `akka-streams-java`, `spark-java`, `pipeline-java`)

lazy val `schemae-java` = (project in file("./schemae"))
  .enablePlugins(PipelinesLibraryPlugin)
  .settings(
    commonSettings,
    schemaCodeGenerator := SchemaCodeGenerator.Java,
    libraryDependencies ++= Seq(junit, junitInterface)
  )

lazy val `akka-streams-java` = (project in file("./akka-streams"))
  .enablePlugins(PipelinesAkkaStreamsLibraryPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(junit, junitInterface, scalaTest)
  )
  .dependsOn(`schemae-java`)

lazy val `spark-java` = (project in file("./spark"))
  .enablePlugins(PipelinesSparkLibraryPlugin)
  .settings(
    commonSettings,
    Test / parallelExecution := false,
    Test / fork := true,
    libraryDependencies ++= Seq(scalaTest)
  )
  .dependsOn(`schemae-java`)

lazy val `pipeline-java` = (project in file("./pipeline"))
  .enablePlugins(PipelinesApplicationPlugin)
  .enablePlugins(PipelinesAkkaStreamsLibraryPlugin)
  .settings(
    name := s"pipelines-workshop-$user",
    version := thisVersion,
    runLocalConfigFile := Some("./pipeline/src/main/blueprint/local-blueprint.conf"),
    libraryDependencies ++= Seq(akkaHttpSprayJson, scalaTest),
    avroSpecificSourceDirectories in Compile ++=
      Seq(new java.io.File("schemae/src/main/avro"))
  )
  .settings(commonSettings)
  .dependsOn(`akka-streams-java`, `spark-java`)

lazy val `data-generator` = (project in file("./data-generator"))
  .settings(
    mainClass := Some("com.lightbend.fraud.gen.Main"),
    libraryDependencies ++= Seq(akkaStreams, playJson, slf4jSimple)
  )

lazy val commonScalacOptions = Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint:_",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  )

lazy val scalacTestCompileOptions = commonScalacOptions ++ Seq(
  //"-Xfatal-warnings",
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  //"-Ywarn-unused:params",              // Warn if a value parameter is unused. (But there's no way to suppress warning when legitimate!!)
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
)
// Ywarn-value-discard is particularly hard to use in many tests,
// because they error-out intentionally in ways that are expected, so it's
// usually okay to discard values, where that's rarely true in regular code.
lazy val scalacSrcCompileOptions = scalacTestCompileOptions ++ Seq(
  "-Ywarn-value-discard")

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  scalacOptions in Compile := scalacSrcCompileOptions,
  scalacOptions in Test := scalacTestCompileOptions,
  scalacOptions in (Compile, console) := commonScalacOptions,
  scalacOptions in (Test, console) := commonScalacOptions,

  scalariformPreferences := scalariformPreferences.value
    .setPreference(AlignParameters, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 90)
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(IndentLocalDefs, true)
    .setPreference(IndentPackageBlocks, true)
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
    .setPreference(NewlineAtEndOfFile, true)
    .setPreference(AllowParamGroupsOnNewlines, true)
    .setPreference(SpacesWithinPatternBinders, false) // otherwise case head +: tail@_ fails to compile!

)
