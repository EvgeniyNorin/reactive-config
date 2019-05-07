package ru.itmo.consul

import cats.effect.{Clock, ConcurrentEffect}
import cats.implicits._
import com.typesafe.scalalogging.Logger
import monix.catnap.CircuitBreaker
import monix.execution.exceptions.ExecutionRejectedException

import scala.concurrent.duration._

class FallbackProvider[F[_] : Clock](implicit logger: Logger,
                                     F: ConcurrentEffect[F]) {

  private val circuitBreaker: F[CircuitBreaker[F]] = CircuitBreaker[F].of(
    maxFailures = 3,
    resetTimeout = 200.millis,
    exponentialBackoffFactor = 1,
    maxResetTimeout = 300.millis
  )

  def withFallBack[T](mainTask: F[T], alternativeTask: F[T]): F[T] =
    circuitBreaker.flatMap { cb =>
      cb.protect(mainTask).recoverWith {
        case error: ExecutionRejectedException =>
          logger.error("Fail Fast with error", error)
          F.raiseError(error)
        case error =>
          logger.error("Exception in FallbackProvider", error)
          alternativeTask
      }
    }
}
