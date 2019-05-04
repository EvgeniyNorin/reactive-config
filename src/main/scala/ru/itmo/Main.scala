package ru.itmo
import monix.eval.Task
import ru.itmo.http.ConsulHttpObservable


object Main extends Components {

  def main(args: Array[String]): Unit = {
    val consulHttpObservable = new ConsulHttpObservable(8080)
    val observable = consulHttpObservable.unsafeRun.runSyncUnsafe()
    observable.doOnNextF {
      kv => Task.eval(logger.info(s"Processed next item: $kv"))
    }
  }
}
