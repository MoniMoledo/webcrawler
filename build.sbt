name := "WebCrawler"

version := "1.0"

scalaVersion := "2.12.2"

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







