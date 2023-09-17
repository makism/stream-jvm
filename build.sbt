ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "FlinkScala"
  )

libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.16.0"
libraryDependencies += "org.apache.flink" % "flink-streaming-scala_2.12" % "1.16.0"
libraryDependencies += "org.apache.flink" % "flink-clients" % "1.16.0"
libraryDependencies += "org.apache.flink" % "flink-connector-kafka" % "1.16.0"
libraryDependencies += "org.apache.flink" % "flink-connector-datagen" % "1.17.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.1"
libraryDependencies += "org.json4s" % "json4s-native_2.12" % "4.0.4"