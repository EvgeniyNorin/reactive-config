package ru.itmo

package object consul {

  final case class KeyValue(key: String, value: String)

  final case class KeyWatchType(Key: String,
                                CreateIndex: Long,
                                ModifyIndex: Long,
                                LockIndex: Long,
                                Flags: Long,
                                Value: String,
                                Session: String)

  type KeyPrefixWatchType = List[KeyWatchType]

}
