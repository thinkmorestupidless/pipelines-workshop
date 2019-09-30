package pipelines.workshop.ingress

import java.time.{ LocalDateTime, ZoneId }
import java.util.UUID

import akka.NotUsed
import akka.stream.scaladsl.Source
import pipelines.akkastream.scaladsl.RunnableGraphStreamletLogic
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.AvroOutlet
import pipelines.workshop.schema.CardPayment

import scala.util.Random

case class Customer(id: UUID, devices: List[UUID])

object CardPaymentGeneratorIngress extends AkkaStreamlet {

  val outlet = AvroOutlet[CardPayment]("out")
  val shape = StreamletShape(outlet)

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    def generateUUIDs(n: Int): List[UUID] = {
      (1 to n).map { _ ⇒
        UUID.randomUUID()
      }.toList
    }

    def generateCustomers(n: Int): List[Customer] = {
      (1 to n).map { _ ⇒
        Customer(UUID.randomUUID(), generateUUIDs(Random.nextInt(3)))
      }.toList
    }

    def randomAmount(from: Double, to: Double) =
      from + (Random.nextDouble() * (to - from))

    def generateTimestamp(): Long =
      LocalDateTime.now()
        .atZone(ZoneId.of("GMT"))
        .toInstant
        .toEpochMilli

    val merchants = generateUUIDs(1000)
    val customers = generateCustomers(10000)

    override def runnableGraph() =
      Source.repeat(NotUsed)
        .map { _ ⇒ generatePayment() }
        .to(atMostOnceSink(outlet))

    def generatePayment() = {
      val customer = customers(Random.nextInt(customers.size))
      val device = customer.devices(Random.nextInt(customer.devices.size))
      val merchant = merchants(Random.nextInt(merchants.size))
      val amount = randomAmount(5, 500)

      CardPayment(generateTimestamp(), "GB", customer.id.toString, device.toString, merchant.toString, amount)
    }
  }
}
