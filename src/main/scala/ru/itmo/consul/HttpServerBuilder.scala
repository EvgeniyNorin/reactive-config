package ru.itmo.consul

import cats.effect.concurrent.Deferred
import cats.effect.{ConcurrentEffect, Resource, Timer}
import cats.implicits._
import com.typesafe.scalalogging.Logger
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

class HttpServerBuilder[F[_] : ConcurrentEffect : Timer](route: HttpApp[F],
                                                         httpPort: Int)
                                                        (implicit executionContext: ExecutionContext,
                                                         logger: Logger) {

  private def mkServer: Resource[F, Server[F]] = BlazeServerBuilder[F]
    .bindHttp(httpPort)
    .withHttpApp(route)
    .withExecutionContext(executionContext)
    .resource

  def build: F[Deferred[F, Unit]] = {
    val stopFlagDeferred: F[Deferred[F, Unit]] = Deferred[F, Unit]
    for {
      stopFlag <- stopFlagDeferred
      _ <- mkServer.use { server =>
        logger.info(s"[Consul HTTP] Initialize HTTP Listener for $server")
        stopFlag.get
      }
    } yield stopFlag
  }
}
