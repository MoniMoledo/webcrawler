import sbt._
import play.sbt.PlayImport._

object Dependencies {
  val playVersion = "2.5.0"
  val twitter4jVersion = "4.0.3"
  val mockitoAll = "org.mockito" % "mockito-all" % "1.10.19" % Test
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.6" % Test
  val easytest = "org.easytesting" % "fest-assert" % "1.4" % Test
  val testDeps = Seq(
    specs2 % Test,
    "org.specs2" %% "specs2-matcher-extra" % "3.6" % Test,
    "org.easytesting" % "fest-assert" % "1.4" % Test,
    "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % Test,
    scalatest,
    easytest,
    mockitoAll
  )

  val noahDependencies: Seq[ModuleID] = Seq(
    ws,
    "org.twitter4j" % "twitter4j-stream" % twitter4jVersion,
    "org.twitter4j" % "twitter4j-core" % twitter4jVersion,
    "com.twitter" % "hbc-core" % "2.2.0",
    ("com.typesafe.play" %% "play-json" % playVersion).exclude("commons-logging", "commons-logging"),
    "org.jsoup" % "jsoup" % "1.9.2"
  ) ++ testDeps

  val utilDependencies: Seq[ModuleID] = Seq(
    ("com.typesafe.play" %% "play-logback" % playVersion)
      .exclude("org.slf4j", "slf4j-simple")
      .exclude("ch.qos.logback", "logback-classic"),
    "org.kohsuke.args4j" % "args4j-maven-plugin" % "2.33"
  ) ++ testDeps

  val gnosisDependencies: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "2.2.6",
    ("com.typesafe.play" %% "play-json" % playVersion).exclude("commons-logging", "commons-logging"),
    "com.vividsolutions" % "jts" % "1.13",
    "org.wololo" % "jts2geojson" % "0.7.0"
  ) ++ testDeps

  val webcrawlerDependencies: Seq[ModuleID] =  Seq(
    "org.apache.httpcomponents" % "httpclient" % "4.3.4",
    "com.google.code.gson" % "gson" % "2.8.0",
    "org.apache.httpcomponents" % "httpcore" % "4.4.6"
  )
}
