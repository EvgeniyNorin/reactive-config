package ru.itmo.utils

object Base64 {
  def decodeBase64(encoded: String) = new String(
    java.util.Base64.getDecoder.decode(encoded.getBytes)
  )
}
