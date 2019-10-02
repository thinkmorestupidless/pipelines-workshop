credentials += Credentials("Bintray", "dl.bintray.com", "728a4264-4c86-49de-83c7-241b4af37dd8@lightbend", "d5faa635ac793e87fd0fea31a1c9428c5e9c64f2")

resolvers += Resolver.url("Pipelines Internal", url("https://dl.bintray.com/lightbend/pipelines-internal"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("lightbend-commercial", url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
resolvers += "Akka Snapshots" at "https://repo.akka.io/snapshots/"

//addSbtPlugin("com.lightbend.pipelines" % "sbt-pipelines" % "1.1.0")
addSbtPlugin("com.lightbend.pipelines" % "sbt-pipelines" % "1.1.1-1237-48cb75ec")

