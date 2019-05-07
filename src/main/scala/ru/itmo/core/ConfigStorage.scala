package ru.itmo.core

import monix.reactive.Observable

import scala.language.higherKinds

trait ConfigStorage[F[_], KV, C] {
  /**
    * @return Observable of changes from any backend
    */
  def getObservable: F[Observable[KV]]

  /**
    * Can be used for initial fetching
    * @return all configuration file from specified backend
    */
  def getConfig: F[C]

}
