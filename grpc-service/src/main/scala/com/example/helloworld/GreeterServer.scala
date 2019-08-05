package com.example.helloworld

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.grpc.scaladsl.ServiceHandler
import akka.http.scaladsl.UseHttp2.Always
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, Http2, HttpConnectionContext}
import akka.stream.{ActorMaterializer, Materializer}
import grpc.reflection.v1alpha._
import io.grpc.protobuf.services.ProtoReflectionService

import scala.concurrent.{ExecutionContext, Future}

object GreeterServer {

  def main(args: Array[String]): Unit = {
    val system: ActorSystem = ActorSystem("GreeterServer")
    new GreeterServer(system).run()
  }
}

class GreeterServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem = system
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = sys.dispatcher

    val service =
      GreeterServiceHandler.partial(new GreeterServiceImpl(mat, system.log))

    val reflection = Reflection.serve(
      Seq(HelloworldProto.javaDescriptor, ReflectionProto.javaDescriptor),
      Seq(GreeterService.name, ServerReflection.name))

    val bound = Http2().bindAndHandleAsync(
      ServiceHandler.concatOrNotFound(service, reflection),
      interface = "0.0.0.0",
      port = 8080,
      HttpConnectionContext(http2 = Always)
    )

    bound.foreach { binding =>
      sys.log.info("gRPC server bound to: {}", binding.localAddress)
    }

    bound
  }
}
