name := "visual-stream"

version := "1.0"

scalaVersion := "2.12.3"

enablePlugins(ScalaJSPlugin)

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"

scalaJSUseMainModuleInitializer := true
