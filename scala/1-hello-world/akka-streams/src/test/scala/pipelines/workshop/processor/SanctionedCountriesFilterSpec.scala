package pipelines.workshop.processor

import java.util.UUID

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.testkit._
import org.scalatest._
import pipelines.akkastream.testkit.scaladsl.{AkkaStreamletTestKit, Completed}
import pipelines.workshop.schema.CardPayment

class SanctionedCountriesFilterSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  private implicit val system = ActorSystem("AkkaStreamletSpec")
  private implicit val mat = ActorMaterializer()

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  def nonSanctionedPayment = CardPayment(0.toLong, "GB", UUID.randomUUID().toString, UUID.randomUUID().toString, UUID.randomUUID().toString, 0)
  val sanctionedPayment = CardPayment(0.toLong, "RU", UUID.randomUUID().toString, UUID.randomUUID().toString, UUID.randomUUID().toString, 0)

  "A SanctionedCountriesFilter" should {

    val testkit = AkkaStreamletTestKit(system, mat)

    "Only allow payments for sanctioned countries to pass through" in {

      val data = Vector(nonSanctionedPayment, sanctionedPayment, nonSanctionedPayment)
      val expectedData = Vector(sanctionedPayment)
      val source = Source(data)
      val proc = new SanctionedCountriesFilter

      val in = testkit.inletFromSource(proc.inlet, source)
      val out = testkit.outletAsTap(proc.outlet)

      testkit.run(proc, in, out, () ⇒ {
        out.probe.receiveN(1) mustBe expectedData.map(d ⇒ proc.outlet.partitioner(d) -> d)
      })

      out.probe.expectMsg(Completed)
    }
  }
}
