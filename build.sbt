import Dependencies._

name := "Crawler"

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

lazy val crawler = (project in file(".")).aggregate(util, gnosis, noah, webcrawler)

lazy val webcrawler = (project in file("webcrawler")).
  settings(
    libraryDependencies ++= Seq( "args4j" % "args4j" % "2.33",
      "org.apache.httpcomponents" % "httpclient" % "4.3.4",
      "com.google.code.gson" % "gson" % "2.8.0",
      "org.apache.httpcomponents" % "httpcore" % "4.4.6"
    )
  )

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






