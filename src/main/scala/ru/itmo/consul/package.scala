package ru.itmo

import ru.itmo.utils.Base64.decodeBase64

package object consul {

  type ConsulKey = String

  final case class KeyValue(key: String, value: String)

  final case class KeyWatchTypeEncoded(Key: String,
                                       CreateIndex: Long,
                                       ModifyIndex: Long,
                                       LockIndex: Long,
                                       Flags: Long,
                                       Value: String,
                                       Session: String)

  def toKeyValue(encoded: KeyWatchTypeEncoded) = KeyValue(encoded.Key, decodeBase64(encoded.Value))


}
