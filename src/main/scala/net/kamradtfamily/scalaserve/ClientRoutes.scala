package net.kamradtfamily.scalaserve

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import net.kamradtfamily.scalaserve.UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout

trait ClientRoutes extends JsonSupport {
  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ClientRoutes])

  // other dependencies that UserRoutes use
  def userRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val clientRoutes: Route =
    pathPrefix("client") {
      concat(
        pathEnd {
          concat(
            get {
              val clients: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(clients)
            },
            post {
              entity(as[String]) { name =>
                val userCreated: Future[ActionPerformed] =
                  (userRegistryActor ? CreateUser(name)).mapTo[ActionPerformed]
                onSuccess(userCreated) { performed =>
                  log.info("Created client [{}]: {}", name, performed.description)
                  complete((StatusCodes.Created, userCreated))
                }
              }
            }
          )
        },
        path(Segment) { id =>
          concat(
            get {
              val maybeUser: Future[Option[User]] =
                (userRegistryActor ? GetUser(id)).mapTo[Option[User]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
            },
            delete {
              val userDeleted: Future[ActionPerformed] =
                (userRegistryActor ? DeleteUser(id)).mapTo[ActionPerformed]
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user [{}]: {}", id, performed.description)
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
}
