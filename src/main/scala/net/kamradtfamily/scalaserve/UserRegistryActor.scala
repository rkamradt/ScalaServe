package net.kamradtfamily.scalaserve

import akka.actor.{ Actor, ActorLogging, Props }

final case class User(name: String, id: String, clientId: String, clientSecret: String)
final case class Users(users: Seq[User])

object UserRegistryActor {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(name: String)
  final case class GetUser(id: String)
  final case class DeleteUser(id: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {
  import UserRegistryActor._

  var users = Set.empty[User]

  def receive: Receive = {
    case GetUsers =>
      sender() ! Users(users.toSeq)
    case CreateUser(name) =>
      val user: User = User(name, "", "", "")
      users += user
      sender() ! ActionPerformed(s"User $user created.")
    case GetUser(id) =>
      sender() ! users.find(_.id == id)
    case DeleteUser(id) =>
      users.find(_.id == id) foreach { user => users -= user }
      sender() ! ActionPerformed(s"User $id deleted.")
  }
}
