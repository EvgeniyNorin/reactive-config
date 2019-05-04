name := "reactive-config"
version := "0.1"
scalaVersion := "2.12.8"
scalacOptions += "-Ypartial-unification"

val monixVersion = "3.0.0-RC2"
val circeVersion = "0.11.1"
val Http4sVersion = "0.20.0-RC1"

val monixDependencies = Seq(
  "io.monix" %% "monix-eval" % monixVersion,
  "io.monix" %% "monix-reactive" % monixVersion
)

val circeDependencies = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val http4sDependencies = Seq(
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe"  % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
)

val loggingDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

libraryDependencies ++=
  monixDependencies ++
    circeDependencies ++
    http4sDependencies ++
    loggingDependencies