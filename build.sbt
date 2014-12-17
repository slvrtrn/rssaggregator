name := "rssaggregator"

resolvers ++= Seq(
  "Twitter" at "http://maven.twttr.com",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
  )

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra" % "1.5.4",
  "com.novus" %% "salat" % "1.9.9",
  "org.scaldi" %% "scaldi" % "0.3.2",
  "net.debasishg" %% "redisreact" % "0.7",
  "com.typesafe" % "config" % "1.0.2",
  "com.github.t3hnar" %% "scala-bcrypt" % "2.4",
  "de.neuland-bfi" % "jade4j" % "0.4.2",
  "com.wix" %% "accord-core" % "0.4",
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test"
  )

scalaVersion := "2.10.4"

org.scalastyle.sbt.ScalastylePlugin.Settings

Revolver.settings

Revolver.enableDebugging(port = 5005, suspend = false)

mainClass in Revolver.reStart := Some("com.github.slvrthrn.App")