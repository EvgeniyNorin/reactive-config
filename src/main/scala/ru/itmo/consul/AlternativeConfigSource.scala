package ru.itmo.consul

class AlternativeConfigSource[F[_]] {
  def getConfig[T](): F[T] = ???
}
