import Dependencies._

name := "Crawler"

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

lazy val crawler = project.in(file(".")).settings(
  libraryDependencies ++= Seq( "args4j" % "args4j" % "2.33",
    "org.apache.httpcomponents" % "httpclient" % "4.3.4",
    "com.google.code.gson" % "gson" % "2.8.0")
).aggregate(webhose).dependsOn(webhose % "compile->compile")

lazy val webhose = project.in(file("webhoseio-java-sdk-master")).
  settings( Seq(
    //publishMavenStyle := true,
    // Do not append Scala versions to the generated artifacts
    crossPaths := false,
    // This forbids including Scala related libraries into the dependency
    autoScalaLibrary := false
    )
  )
  .settings {
    libraryDependencies ++=
      Seq("com.google.code.gson" % "gson" % "2.8.0",
        "org.apache.httpcomponents" % "httpclient" % "4.5.2",
        "org.apache.httpcomponents" % "httpcore" % "4.4.6")
  }

lazy val util = (project in file("util")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= utilDependencies
  )

lazy val gnosis = (project in file("gnosis")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= gnosisDependencies
  ).dependsOn(util)

lazy val noah = (project in file("noah")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= noahDependencies
  ).dependsOn(gnosis, util)




