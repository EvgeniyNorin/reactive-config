package ru.itmo.consul

import cats.effect.concurrent.Deferred
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.subjects.PublishSubject
import monix.reactive.{Observable, Observer}
import ru.itmo.core.ConfigStorage

import scala.concurrent.ExecutionContext

class ConsulConfigStorage(httpClient: ConsulHttpClient,
                          alternativeConfigSource: AlternativeConfigSource[Task],
                          httpPort: Int)
                         (implicit logger: Logger,
                          executionContext: ExecutionContext,
                          fallbackProvider: FallbackProvider[Task],
                          scheduler: Scheduler) extends ConfigStorage[Task, KeyValue, String] {

  private val subject = PublishSubject[KeyValue]
  private val (observer, observable): (Observer[KeyValue], Observable[KeyValue]) = (subject, subject)

  private val pointOfInitialization: Task[Deferred[Task, Unit]] = {
    val consulUpdateRoutes = new ConsulHandlers(observer)
    val server = new HttpServerBuilder[Task](
      route = consulUpdateRoutes.buildRoutes(observer),
      httpPort = httpPort
    )
    server.build.memoizeOnSuccess.map { st =>
      sys.addShutdownHook(st.complete(()).runSyncUnsafe())
      st
    }
  }

  override def getObservable: Task[Observable[KeyValue]] =
    pointOfInitialization.flatMap(_ => Task.now(observable))


  override def getConfig: Task[String] =
    fallbackProvider.withFallBack(
      mainTask = httpClient.getConfig[String],
      alternativeTask = alternativeConfigSource.getConfig()
    )
}
