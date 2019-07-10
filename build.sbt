name := "fun-with-macros"

version := "0.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.0",
  organization := "scala.example"
)
lazy val scalaReflect = Def.setting { "org.scala-lang" % "scala-reflect" % scalaVersion.value }

lazy val core = (project in file("core"))
  .dependsOn(macros)
  .settings(
    commonSettings,
  )

lazy val macros = (project in file("macros"))
  .settings(
    commonSettings,
    libraryDependencies += scalaReflect.value
  )

lazy val root = (project in file(".")).aggregate(core, macros)