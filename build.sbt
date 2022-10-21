scalaVersion := "3.2.0"

name    := "zio-kafka-demo"
version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"         % "2.0.2",
  "dev.zio" %% "zio-streams" % "2.0.2",
  "dev.zio" %% "zio-kafka"   % "2.0.1"
).map(_.withJavadoc()).map(_.withSources())

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.4"
