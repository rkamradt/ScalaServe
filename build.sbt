name := "ScalaServe"

version := "0.1"

scalaVersion := "2.12.5"

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"            % "10.0.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-xml"        % "10.0.11",
  "com.typesafe.akka" %% "akka-stream"          % "2.5.11",

  "com.typesafe.akka" %% "akka-http-testkit"    % "10.0.11" % Test,
  "com.typesafe.akka" %% "akka-testkit"         % "2.5.11"     % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % "2.5.11"     % Test,
  "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test)

mainClass in Compile := Some("net.kamradtfamily.scalaserve.QuickstartServer")

dockerBaseImage := "openjdk:jre-alpine"

