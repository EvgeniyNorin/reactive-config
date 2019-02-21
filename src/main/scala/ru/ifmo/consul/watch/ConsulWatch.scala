package ru.ifmo.consul.watch

import io.finch.Endpoint

trait ConsulWatch[In, Out] {

  def endpoint: Endpoint[Out]

}
