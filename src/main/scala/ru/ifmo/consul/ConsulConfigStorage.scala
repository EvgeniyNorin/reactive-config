package ru.ifmo.consul

import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import ru.ifmo.config.ConfigStorage

class ConsulConfigStorage extends ConfigStorage[KeyValue] {
  override def modificationPublisher: Observable[KeyValue] = {
    val publishSubject = PublishSubject[KeyValue]()

    ???
  }
}
