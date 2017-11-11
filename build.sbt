name := "kafka-streams-tryout"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies += "org.apache.kafka" % "kafka-streams" % "1.0.0"
libraryDependencies += "org.apache.kafka" % "kafka_2.12" % "1.0.0"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.12" % "2.4.18"
libraryDependencies += "com.typesafe.akka" % "akka-http_2.12" % "10.0.10"
libraryDependencies += "com.typesafe.akka" % "akka-slf4j_2.12" % "2.4.18"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.17"
