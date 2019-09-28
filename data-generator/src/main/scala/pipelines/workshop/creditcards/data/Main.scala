package pipelines.workshop.creditcards.data

import java.nio.file.Paths
import java.time.{ LocalDateTime, ZoneId }
import java.time.temporal.ChronoUnit
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{ FileIO, Sink, Source }
import akka.stream.{ ActorMaterializer, Materializer }
import akka.util.ByteString
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.math.BigDecimal.RoundingMode
import scala.util.{ Failure, Random, Success }

case class CardPayment(timestamp: Long, customerId: UUID, deviceId: UUID, merchantId: UUID, amount: BigDecimal)

object CardPayment {
  implicit val format: Format[CardPayment] = Json.format
}

object Main {

  val log: Logger = LoggerFactory.getLogger(getClass)

  val r = scala.util.Random

  def main(args: Array[String]): Unit = {
    log.info("starting customer generator")

    implicit val system = ActorSystem("customer-generator")
    implicit val materializer = ActorMaterializer()

    execute()
  }

  def execute()(implicit system: ActorSystem, mat: Materializer) =
    for {
      customers <- generateCustomers()
      merchants <- generateMerchants()
    } yield {
      log.info(s"successfully generated ${customers.size} customers and ${merchants.size}")
      updateCsv(customers.toList, merchants.toList)
    }

  def updateCsv(customers: List[Customer], merchants: List[UUID])(implicit system: ActorSystem, mat: Materializer) = {
    val conf = system.settings.config.getConfig("data-generator")
    val recordCount = conf.getLong("record-count")
    val outputPathStr = conf.getString("output-file")
    val fromDate = LocalDateTime.parse(conf.getString("from-date"))
    val toDate = LocalDateTime.parse(conf.getString("to-date"))
    val amountRange = conf.getDoubleList("amount-range")
    val fromAmount = amountRange.get(0)
    val toAmount = amountRange.get(1)

    // Delete the target file, if it exists, before we start
    val outputPath = Paths.get(outputPathStr)
    val outputFile = outputPath.toFile()

    if (outputFile.exists()) {
      outputFile.delete()
    }

    Source.repeat(NotUsed)
      .take(recordCount)
      .map { _ => generateCardPayment(customers, merchants, fromDate, toDate, fromAmount, toAmount) }
      .map(Json.toJson(_))
      .map(Json.stringify(_))
      .map { str => ByteString(s"$str\n") }
      .runWith(FileIO.toPath(outputPath))
      .onComplete {
        // Careful! This _could_ be an IO Failure from FileIO.toPath()
        case Success(_) => log.info("All Done!")
        case Failure(e) => log.warn(s"Failed to write CSV file -> $e")
      }
  }

  def generateCardPayment(customers: List[Customer], merchants: List[UUID], fromDate: LocalDateTime, toDate: LocalDateTime, fromAmount: Double, toAmount: Double): CardPayment = {
    val timestamp = generateTimestamp(fromDate, toDate)
    val (customer, device) = generateCustomerAndDevice(customers)
    val merchant = generateMerchant(merchants)
    val amount = generateAmount(fromAmount, toAmount)

    CardPayment(timestamp, customer, device, merchant, amount)
  }

  def generateTimestamp(from: LocalDateTime, to: LocalDateTime): Long = {
    val diff = ChronoUnit.DAYS.between(from, to)
    val ldt = from.plusDays(r.nextInt(diff.toInt))
    val zdt = ldt.atZone(ZoneId.of("GMT"))

    zdt.toInstant.toEpochMilli
  }

  def generateCustomerAndDevice(customers: List[Customer]): (UUID, UUID) = {
    val customer = customers(r.nextInt(customers.size))
    val device = customer.devices(r.nextInt(customer.devices.size))

    (customer.id, device.id)
  }

  def generateCustomers()(implicit system: ActorSystem, mat: Materializer): Future[Seq[Customer]] = {
    val conf = system.settings.config.getConfig("customer-generator")
    val customerCount = conf.getLong("customer-count")
    val devicesPerCustomer = conf.getInt("devices-per-customer")

    Source.repeat(NotUsed)
      .take(customerCount)
      .mapAsync(10) {
        _ => generateCustomer(devicesPerCustomer)
      }.runWith(Sink.seq)
  }

  def generateCustomer(devicesPerCustomer: Int): Future[Customer] =
    Future {
      val devices: List[Device] = (1 to devicesPerCustomer).foldLeft(List[Device]()) { (d, i) => d ++ List(generateDevice()) }
      Customer(UUID.randomUUID(), devices)
    }

  def generateDevice(): Device = {
    val uuid = UUID.randomUUID()
    val trusted = Random.nextBoolean()

    Device(uuid, trusted)
  }

  def generateMerchants()(implicit system: ActorSystem, mat: Materializer): Future[Seq[UUID]] = {
    val conf = system.settings.config.getConfig("merchant-generator")
    val merchantCount = conf.getInt("merchant-count")
    Source.repeat(NotUsed).take(merchantCount).map { _ => UUID.randomUUID() }.runWith(Sink.seq)
  }

  def generateMerchant(merchants: List[UUID]): UUID =
    merchants(r.nextInt(merchants.size))

  def generateAmount(from: Double, to: Double): BigDecimal = {
    val amount = from + (r.nextDouble() * (to - from))
    BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)
  }
}

case class Customer(id: UUID, devices: List[Device])

case class Device(id: UUID, trusted: Boolean)
