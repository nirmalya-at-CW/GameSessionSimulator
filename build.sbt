
lazy val root = project
  .in(file("."))
  .settings(
    name := "GameSessionLoadDriver",
    scalaVersion := "2.11.11",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" ,
      "io.gatling"            % "gatling-test-framework"    % "2.2.2",
      "org.json4s" % "json4s-native_2.11" % "3.5.2",
      "org.json4s" % "json4s-ext_2.11" % "3.5.2",
      "de.heikoseeberger" %% "akka-http-json4s" % "1.16.0",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.8.9",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.9",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.9"
    )
  ).enablePlugins(GatlingPlugin)

publishArtifact in (Test, packageBin) := true

    