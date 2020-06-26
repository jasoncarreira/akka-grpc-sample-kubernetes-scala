name := "akka-grpc-kubernetes"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.3.0-SNAPSHOT"

val akkaVersion = "2.6.6"
val discoveryVersion = "1.0.8"
val akkaHttpVersion = "10.1.12"
val alpnVersion = "2.0.9"
val jacksonVersion = "2.11.1"
val swaggerVersion = "2.1.2"
val grpcVersion = "1.30.2"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// Http front end that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.1.1",
      "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.1.2",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
      "io.swagger.core.v3" % "swagger-core" % swaggerVersion,
      "io.swagger.core.v3" % "swagger-annotations" % swaggerVersion,
      "io.swagger.core.v3" % "swagger-models" % swaggerVersion,
      "io.swagger.core.v3" % "swagger-jaxrs2" % swaggerVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-protobuf" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,

      "com.typesafe.akka" %% "akka-parsing" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

      "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % discoveryVersion,
    ),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % alpnVersion % "runtime",
    dockerExposedPorts := Seq(8080)
  )

lazy val grpcService = (project in file("grpc-service"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-stub" % grpcVersion,
      "io.grpc" % "grpc-protobuf" % grpcVersion,
      "io.grpc" % "grpc-services" % grpcVersion
    ),
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % alpnVersion % "runtime",
    dockerExposedPorts := Seq(8080)
  )


