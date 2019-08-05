package com.example.helloworld.swagger

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Route
import com.example.helloworld.HelloService
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info

object SwaggerDocService extends SwaggerHttpService {
  override val apiClasses = Set(classOf[HelloService])
  override val host = "localhost:8080"
  override val info = Info(version = "1.0")
  override def routes: Route = {
    path("swagger.json") {
      get {
        complete(HttpEntity(MediaTypes.`application/json`, generateSwaggerJson))
      }
    }
  }
  //override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
}
