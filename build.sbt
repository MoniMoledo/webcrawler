import Dependencies._

name := "Crawler"

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

lazy val crawler = (project in file(".")).aggregate(commons, gnosis, noah, webcrawler)

lazy val commons = (project in file("commons")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= utilDependencies
  )
lazy val webcrawler = (project in file("webcrawler")).
  settings(
    libraryDependencies ++= webcrawlerDependencies
  ).settings(Commons.settings: _*)
  .dependsOn(commons)

lazy val gnosis = (project in file("gnosis")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= gnosisDependencies
  ).dependsOn(commons)

lazy val noah = (project in file("noah")).
  settings(Commons.settings: _*).
  settings(
    libraryDependencies ++= noahDependencies
  ).dependsOn(gnosis, commons)