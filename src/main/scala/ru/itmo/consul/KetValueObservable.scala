package ru.itmo.consul

import cats.effect.Sync
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{MulticastStrategy, Observable, Observer, OverflowStrategy}
import ru.itmo.http.ConsulHttpObservable

class KetValueObservable(consulHttpObservable: ConsulHttpObservable)
                        (implicit scheduler: Scheduler) {

  private val (observer, observable): (Observer.Sync[KeyWatchType], Observable[KeyWatchType]) =
    Observable.multicast[KeyWatchType](MulticastStrategy.Publish, OverflowStrategy.DropOld(1))

  private def initUnsafe() = {
//    consulHttpObservable.unsafeRun(observer).runSyncUnsafe()
  }

}
