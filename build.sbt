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
  "com.typesafe" % "config" % "1.0.2"
  )

scalaVersion := "2.10.4"
