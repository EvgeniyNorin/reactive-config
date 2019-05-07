package ru.itmo.core

import cats.effect.ConcurrentEffect
import monix.execution.Scheduler
import monix.reactive.Observable
import ru.itmo.core.Reloadable.Source

import scala.language.higherKinds

/**
  * Stateful object which manage the configurable object
  */

abstract class Reloadable[F[_], T](val initState: Source[T]) {

  def get: F[T]

  def map[E](f: T => E): Reloadable[F, E]

  def transformTo(state: Source[T] => Reloadable[F, T]): Reloadable[F, T]

  def modify[E](f: Source[T] => Source[E]): Reloadable[F, E]

  def combine[C, G](other: Reloadable[F, C])(f: (T, C) => G): F[Reloadable[F, G]]

}

object Reloadable {

  type Source[T] = (Observable[T], T)


  def point[F[_] : ConcurrentEffect, E](initial: Source[E])
                                    (implicit scheduler: Scheduler): Reloadable[F, E] = {
    new DummyReloadable[F, E](initial._1, initial._2)
  }

  def apply[F[_], T](observable: Observable[T],
                     initialState: T)
                    (implicit scheduler: Scheduler,
                     F: ConcurrentEffect[F]): Reloadable[F, T] = new DummyReloadable(observable, initialState)

}