package net.kamradtfamily.scalaserve

object Payloads {
  final case class AuthClientRequest(name: String)
  final case class AuthClientResponse(name: String, id: String, clientId: String, clientSecret: String)
  final case class AuthClientsResponse(clients: Seq[AuthClientResponse])
  implicit def User2AuthClientResponse(user: User) = AuthClientResponse(user.name, user.id, user.clientId, user.clientSecret)
}
