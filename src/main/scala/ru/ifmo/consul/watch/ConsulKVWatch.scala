package ru.ifmo.consul.watch

import ru.ifmo.consul.{KeyWatchType, KeyValue}
import io.finch._, io.finch.syntax._
import io.finch._
import io.finch.syntax._
import io.circe.generic.auto._


class ConsulKVWatch {

  def endpoint: Endpoint[KeyWatchType] = {
    get("keyvalue" :: "bar") { case keyWatchType: KeyWatchType =>
      Ok("")
    }
  }

}
