// Resolver for the pipelines-sbt plugin
//
// NOTE: Private repository!
//  Please add your Bintray credentials to your global SBT config.
//
// Refer to https://github.com/lightbend/pipelines-docs/blob/master/user-guide/getting-started.md#bintray-credentials
// for details on how to setup your Bintray credentials and access `sbt-pipelines`.
//
resolvers += Resolver.url("Pipelines Internal", url("https://dl.bintray.com/lightbend/pipelines-internal"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
resolvers += "Akka Snapshots" at "https://repo.akka.io/snapshots/"

//addSbtPlugin("com.lightbend.pipelines" % "sbt-pipelines" % "1.1.0")
//addSbtPlugin("com.lightbend.pipelines" % "sbt-pipelines" % "1.1.1-1237-48cb75ec")
addSbtPlugin("com.lightbend.pipelines" % "sbt-pipelines" % "1.1.1-1243-a9b71bef")

