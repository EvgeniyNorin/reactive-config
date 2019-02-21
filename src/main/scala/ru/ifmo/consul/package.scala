package ru.ifmo

package object consul {

  final case class KeyValue(key: String, value: String)

  sealed trait WatchType

  final case class KeyWatchType(Key: String,
                                CreateIndex: Long,
                                ModifyIndex: Long,
                                LockIndex: Long,
                                Flags: Long,
                                Value: String,
                                Session: String) extends WatchType

}
