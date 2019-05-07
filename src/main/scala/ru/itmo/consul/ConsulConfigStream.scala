package ru.itmo.consul

import io.circe.Decoder
import io.circe.parser.{parse, _}
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import ru.itmo.core.{ConfigStream, Reloadable}

class ConsulConfigStream(configStorage: ConsulConfigStorage,
                         consulHttpClient: ConsulHttpClient)
                        (implicit scheduler: Scheduler) extends ConfigStream[Task, ConsulKey] {

  override def subscribe[V](key: ConsulKey)
                           (implicit decoder: Decoder[V]): Task[Observable[V]] = {
    configStorage
      .getObservable
      .map { observable =>
        observable
          .filter(_.key == key)
          .mapEvalF(kv => Task.fromEither(decode[V](kv.value)))
      }
  }

  override def getReloadable[V](key: ConsulKey)
                            (implicit decoder: Decoder[V]): Task[Reloadable[Task, V]] = {
    for {
      initial <- getConfig[V](key)
      observable <- subscribe[V](key)
    } yield Reloadable[Task, V](observable, initial)
  }

  override def getConfig[V](key: ConsulKey)
                           (implicit decoder: Decoder[V]): Task[V] = {
    configStorage.getConfig.flatMap { jsonString =>
      val parsingResult = for {
        jsonObject <- parse(jsonString)
        result <- jsonObject.hcursor.downField(key).as[V]
      } yield result
      Task.fromEither(parsingResult)
    }
  }

}
