package com.example.helloworld

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.{Content, Schema}
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.{Operation, Parameter}
import javax.ws.rs.{GET, POST, Path}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object HelloService {
  def create(implicit system: ActorSystem,
             mat: Materializer,
             ec: ExecutionContext,
             log: LoggingAdapter): HelloService = {
    val settings = GrpcClientSettings.fromConfig("helloworld.GreeterService")
    val client = GreeterServiceClient(settings)
    new HelloService(client, log)
  }
}

@Path("/hello")
class HelloService(client: GreeterServiceClient, log: LoggingAdapter)
    extends Directives {
  val route: Route = getHello

  @GET
  @Operation(
    summary = "Return Hello greeting",
    description = "Return Hello greeting for named user",
    parameters = Array(
      new Parameter(name = "name",
                    in = ParameterIn.PATH,
                    description = "user name")),
    responses = Array(
      new ApiResponse(
        responseCode = "200",
        description = "Hello response",
        content = Array(
          new Content(schema = new Schema(implementation = classOf[String])))),
      new ApiResponse(responseCode = "500",
                      description = "Internal server error")
    )
  )
  def getHello: Route =
    path("hello" / Segment) { name =>
      get {
        log.info("hello request")
        onComplete(client.sayHello(HelloRequest(name))) {
          case Success(reply) => complete(reply.message)
          case Failure(t) =>
            log.error(t, "Request failed")
            complete(StatusCodes.InternalServerError, t.getMessage)
        }
      }
    }
}
