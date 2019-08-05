package com.example.helloworld

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, get, onComplete, path}
import akka.stream.{ActorMaterializer, Materializer}
import com.example.helloworld.swagger.SwaggerDocService
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Rest {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("HttpToGrpc")
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher
    implicit val log: LoggingAdapter = system.log

    val helloService = HelloService.create

    val routes = helloService.route ~ SwaggerDocService.routes

    val bindingFuture = Http().bindAndHandle(routes, "0.0.0.0", 8080)
    bindingFuture.onComplete {
      case Success(sb) =>
        log.info("Bound: {}", sb)
      case Failure(t) =>
        log.error(t, "Failed to bind. Shutting down")
        system.terminate()
    }

  }
}
