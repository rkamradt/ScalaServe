package net.kamradtfamily.scalaserve

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.kamradtfamily.scalaserve.UserRegistryActor.ActionPerformed
import net.kamradtfamily.scalaserve.Payloads._
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val authClientRequestJsonFormat = jsonFormat1(AuthClientRequest)
  implicit val authClientResponseJsonFormat = jsonFormat4(AuthClientResponse)
  implicit val authClientsResponseJsonFormat = jsonFormat1(AuthClientsResponse)

  implicit val user = jsonFormat4(User)
  implicit val users = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
