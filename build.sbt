name := "visual-stream"
organization in ThisBuild := "com.yuiwai"
version in ThisBuild := "2.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.6"
scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
)

enablePlugins(ScalaJSPlugin)

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  "com.lihaoyi" %%% "utest" % "0.6.5" % "test"
)
testFrameworks += new TestFramework("utest.runner.Framework")
scalaJSUseMainModuleInitializer := true
