name := "reactive-config"

version := "0.1"

scalaVersion := "2.12.8"

val monixVersion = "3.0.0-8084549"

val monixDependencies = Seq(
  "io.monix" %% "monix-eval" % monixVersion,
  "io.monix" %% "monix-reactive" % monixVersion
)

val circeDependencies = Seq(
  "com.github.finagle" %% "finch-circe" % "0.27.0",
  "com.github.finagle" %% "finch-core" % "0.27.0",
  "io.circe" %% "circe-generic" % "0.11.1"
)

libraryDependencies ++=
  monixDependencies ++
    circeDependencies