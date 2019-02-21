package ru.ifmo.config

import monix.reactive.Observable

trait ConfigStorage[T] {

  def modificationPublisher: Observable[T]

}
