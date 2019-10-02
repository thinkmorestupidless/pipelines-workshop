package pipelines.workshop.data

import java.nio.file.Paths
import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.scaladsl.{ FileIO, Sink, Source }
import akka.stream.{ ActorMaterializer, Materializer }
import akka.util.{ ByteString, Timeout }
import com.typesafe.config.ConfigFactory
import org.slf4j.{ Logger, LoggerFactory }
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

case class CardPayment(timestamp: Long, countryCode: String, customerId: UUID, deviceId: UUID, merchantId: UUID, amount: BigDecimal)

object CardPayment {
  implicit val format: Format[CardPayment] = Json.format
}

object Main {

  val log: Logger = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("customer-generator")
  implicit val materializer = ActorMaterializer()

  val r = scala.util.Random

  val config = ConfigFactory.load()
  val conf = config.getConfig("data-generator")
  val customerCount = conf.getInt("customer-count")
  val outputPathStr = conf.getString("output-file")
  val recordsPerCustomer = conf.getInt("records-per-customer")

  def main(args: Array[String]): Unit = {
    log.info("starting customer generator")

    generate()
  }

  def generate()(implicit system: ActorSystem, mat: Materializer) = {
    for {
      merchants <- generateMerchants()
    } yield {
      val outputPath = Paths.get(outputPathStr)
      val outputFile = outputPath.toFile()

      if (outputFile.exists()) {
        outputFile.delete()
      }

      implicit val timeout = Timeout(10 seconds)

      val merchantsList = merchants.toList
      val customers = system.actorOf(Customers.props, "customers")

      Source.repeat(NotUsed)
        .take(customerCount)
        .mapAsync(1) { _ =>
          (customers ? Customers.GeneratePayments(UUID.randomUUID(), recordsPerCustomer, merchantsList))
            .mapTo[List[CardPayment]]
        }.flatMapConcat {
          case payments =>
            Source(payments)
        }
        .map(Json.toJson(_))
        .map(Json.stringify(_))
        .map { str => ByteString(s"$str\n") }
        .runWith(FileIO.toPath(outputPath))
        .onComplete {
          // Careful! This _could_ be an IO Failure from FileIO.toPath()
          case Success(_) => log.info(s"All Done! File written to $outputPath")
          case Failure(e) => log.warn(s"Failed to write JSON file -> $e")
        }
    }
  }

  def generateMerchants()(implicit system: ActorSystem, mat: Materializer): Future[Seq[UUID]] = {
    val conf = system.settings.config.getConfig("merchant-generator")
    val merchantCount = conf.getInt("merchant-count")
    Source.repeat(NotUsed).take(merchantCount).map { _ => UUID.randomUUID() }.runWith(Sink.seq)
  }
}

case class Customer(id: UUID, devices: List[Device])

case class Device(id: UUID, trusted: Boolean)
