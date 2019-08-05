name := "akka-grpc-kubernetes"
scalaVersion := "2.12.8"


version in ThisBuild := "0.2.0-SNAPSHOT"

lazy val akkaVersion = "2.5.23"
lazy val discoveryVersion = "1.0.1"
lazy val akkaHttpVersion = "10.1.9"
lazy val alpnVersion = "2.0.9"
val jacksonVersion = "2.9.9"
val swaggerVersion = "2.0.8"
val grpcVersion = "1.22.1"

lazy val root = (project in file("."))
  .aggregate(httpToGrpc, grpcService)

// Http front end that calls out to a gRPC back end
lazy val httpToGrpc = (project in file("http-to-grpc"))
  .enablePlugins(AkkaGrpcPlugin, DockerPlugin, JavaAppPackaging, JavaAgent)
  .settings(
    libraryDependencies ++= Seq(
      "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.0.3",
      "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.0.4",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.9",
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


