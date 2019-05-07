package ru.itmo.core

import cats.effect.ConcurrentEffect
import cats.implicits._
import monix.execution.Ack.Continue
import monix.execution.Scheduler
import monix.reactive.Observable
import ru.itmo.core.Reloadable._

import scala.language.higherKinds

class DummyReloadable[F[_], T](observable: Observable[T],
                               initialState: T)
                              (implicit scheduler: Scheduler,
                               F: ConcurrentEffect[F]) extends Reloadable[F, T]((observable, initialState)) {
  @volatile private var internalState: T = initialState

  observable
    .subscribe { newState =>
      internalState = newState
      Continue
    }

  override def get: F[T] = F.delay(internalState)

  override def map[E](f: T => E): Reloadable[F, E] =
    point(observable.map(f), f(internalState))

  override def transformTo(state: ((Observable[T], T)) => Reloadable[F, T]): Reloadable[F, T] =
    state(observable, internalState)

  override def modify[E](f: ((Observable[T], T)) => (Observable[E], E)): Reloadable[F, E] =
    point(f(observable, internalState))

  override def combine[C, G](other: Reloadable[F, C])(f: (T, C) => G): F[Reloadable[F, G]] = {
    other.get.map { otherState =>
      val (otherObservable, _) = other.initState
      val combinedObservable: Observable[G] =
        observable.combineLatest(otherObservable).map { case (t, c) => f(t, c) }
      point((combinedObservable, f(internalState, otherState)))
    }
  }
}
