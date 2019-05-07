package ru.itmo.consul

import cats.Monad
import com.typesafe.scalalogging.Logger
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observer
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, _}
import ru.itmo.consul.ConsulHandlers._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

class ConsulHandlers(observer: Observer[KeyValue])
                    (implicit logger: Logger,
                     executionContext: ExecutionContext,
                     scheduler: Scheduler) {

  def buildRoutes(observer: Observer[KeyValue]): HttpApp[Task] = HttpApp[Task] {
    buildKeyUpdateRoute.orElse(buildKeyPrefixUpdateRoute)
  }

  private val buildKeyUpdateRoute: PartialFunction[Request[Task], Task[Response[Task]]] = {
    case rawRequest@POST -> Root / "update_key" =>
      for {
        parsedKV <- rawRequest.as[KeyWatchTypeEncoded]
        _ = logger.info(s"[Consul HTTP] KeyWatchType entity received $parsedKV")
        _ <- Task.fromFuture(observer.onNext(toKeyValue(parsedKV)))
        response <- Monad[Task].pure(Response[Task]())
      } yield response
  }

  private val buildKeyPrefixUpdateRoute: PartialFunction[Request[Task], Task[Response[Task]]] =
    PartialFunction.empty
}

object ConsulHandlers {
  implicit val keyWatchTypeDecoder: Decoder[KeyWatchTypeEncoded] =
    deriveDecoder[KeyWatchTypeEncoded]

  implicit val keyWatchTypeEntityDecoder: EntityDecoder[Task, KeyWatchTypeEncoded] =
    jsonOf[Task, KeyWatchTypeEncoded]
}