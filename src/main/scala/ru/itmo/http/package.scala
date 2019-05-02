
package ru.itmo

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import ru.itmo.consul.KeyWatchType

package object http {

  implicit val keyWatchTypeDecoder: Decoder[KeyWatchType] = deriveDecoder[KeyWatchType]

}
