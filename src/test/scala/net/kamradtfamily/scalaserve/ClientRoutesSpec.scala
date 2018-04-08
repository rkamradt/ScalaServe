package net.kamradtfamily.scalaserve

//#user-routes-spec
//#test-top
import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import net.kamradtfamily.scalaserve.Payloads._

class ClientRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with ClientRoutes {

  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes, 
  // but we could "mock" it by implementing it in-place or by using a TestProbe() 
  override val userRegistryActor: ActorRef =
    system.actorOf(UserRegistryActor.props, "userRegistry")

  lazy val routes = clientRoutes

  "UserRoutes" should {
    "return no users if no present (GET /client)" in {
      // note that there's no need for the host part in the uri:
      val request = HttpRequest(uri = "/client")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"users":[]}""")
      }
    }
    "be able to add users (POST /client)" in {
      val user = AuthClientRequest("Kapi")
      val userEntity = Marshal(user).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      // using the RequestBuilding DSL:
      val request = Post("/client").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and we know what message we're expecting back:
        entityAs[String] should ===("""{"description":"User User({\"name\":\"Kapi\"},,,) created."}""")
      }
    }

    "be able to remove users (DELETE /client)" in {
      // user the RequestBuilding DSL provided by ScalatestRouteSpec:
      val request = Delete(uri = "/client/Kapi")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        // we expect the response to be json:
        contentType should ===(ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should ===("""{"description":"User Kapi deleted."}""")
      }
    }
  }
}
