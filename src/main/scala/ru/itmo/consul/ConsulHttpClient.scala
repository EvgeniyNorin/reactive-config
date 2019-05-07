package ru.itmo.consul

import cats.effect.Resource
import com.typesafe.scalalogging.Logger
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.client._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{EntityDecoder, Uri}

import scala.concurrent.ExecutionContext

class ConsulHttpClient(host: String, port: Int)
                      (implicit executionContext: ExecutionContext,
                       scheduler: Scheduler,
                       logger: Logger) {

  private val httpClientResource: Resource[Task, Client[Task]] =
    BlazeClientBuilder[Task](executionContext).resource

  private def executeGetRequest[T](uri: Uri)(implicit entityDecoder: EntityDecoder[Task, T]): Task[T] =
    httpClientResource.use(client => client.expect(uri)(entityDecoder))

  private val consulAgentUri: Uri = {
    Uri.fromString(s"$host:$port").fold[Uri](
      parseFailure => {
        logger.error("Error during URI verification")
        throw parseFailure
      },
      uri => uri
    )
  }

  def getConfig[T](implicit entityDecoder: EntityDecoder[Task, T]): Task[T] =
    executeGetRequest(consulAgentUri)(entityDecoder)
}
