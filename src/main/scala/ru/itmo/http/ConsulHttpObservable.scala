package ru.itmo.http

import cats.Monad
import cats.effect.concurrent.Deferred
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.subjects.PublishSubject
import monix.reactive.{Observable, Observer}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeServerBuilder
import ru.itmo.consul.KeyWatchType

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

class ConsulHttpObservable(httpPort: Int)
                          (implicit logger: Logger,
                           executionContext: ExecutionContext,
                           scheduler: Scheduler) {

  private val subject = PublishSubject[KeyWatchType]
  private val (observer, observable): (Observer[KeyWatchType], Observable[KeyWatchType]) = (subject, subject)

  private def route: HttpApp[Task] = HttpApp[Task] {
    case rawRequest @ POST -> Root / "update_config" =>
      for {
        parsedKV <- rawRequest.as[KeyWatchType]
        _ = logger.info(s"[Consul HTTP] KeyWatchType entity received $parsedKV")
        _ <- Task.fromFuture(observer.onNext(parsedKV))
        response <- Monad[Task].pure(Response[Task]())
      } yield response
  }


  private def builder = BlazeServerBuilder[Task]
    .bindHttp(httpPort)
    .withHttpApp(route)
    .withExecutionContext(executionContext)
    .resource

  def unsafeRun: Task[Observable[KeyWatchType]] = {
    val stopFlagDeferred: Task[Deferred[Task, Unit]] = Deferred[Task, Unit]
    for {
      stopFlag <- stopFlagDeferred
      _ <- builder.use { server =>
        logger.info(s"[Consul HTTP] Initialize HTTP Listener for $server")
        stopFlag.get
      }
    } yield observable.doOnSubscriptionCancel(stopFlag.complete(()))
  }
}
