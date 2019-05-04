
package ru.itmo

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import monix.eval.Task
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import ru.itmo.consul.KeyWatchType

package object http {

  implicit val keyWatchTypeDecoder: Decoder[KeyWatchType] = deriveDecoder[KeyWatchType]

  implicit val keyWatchTypeEntityDecoder: EntityDecoder[Task, KeyWatchType] = jsonOf[Task, KeyWatchType]

}
