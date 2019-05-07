package ru.itmo.core

import io.circe.Decoder
import monix.reactive.Observable

import scala.language.higherKinds

trait ConfigStream[F[_], K] {

  /**
    * Subscribe on updates from [[ConfigStorage]]
    * @return Observable of specific configuration object
    */

  def subscribe[V](key: K)(implicit decoder: Decoder[V]): F[Observable[V]]

  /**
    *  @return Wrapped in F[_] value of specific configuration object from [[ConfigStorage]]
    */
  def getConfig[V](key: K)(implicit decoder: Decoder[V]): F[V]

  /**
    * @return Reloadable for config associated with the key
    */

  def getReloadable[V](key: K)(implicit decoder: Decoder[V]): F[Reloadable[F, V]]

}
