package pipelines.workshop.data

import java.time.temporal.ChronoUnit
import java.time.{ LocalDateTime, ZoneId }
import java.util.UUID

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

class CustomerActor extends Actor with ActorLogging {
  import Customers._
  import CustomerActor._
  import context.become

  val conf = context.system.settings.config.getConfig("data-generator")

  val devicesPerCustomer = conf.getInt("devices-per-customer")
  val balanceRange = conf.getDoubleList("balance-range")
  val fromBalance = balanceRange.get(0)
  val toBalance = balanceRange.get(1)
  val fromDate = LocalDateTime.parse(conf.getString("from-date"))
  val toDate = LocalDateTime.parse(conf.getString("to-date"))
  val amountRange = conf.getDoubleList("amount-range")
  val fromAmount = amountRange.get(0)
  val toAmount = amountRange.get(1)
  val countryCodes = conf.getStringList("country-codes")

  val devices = generateDeviceIds(devicesPerCustomer)

  override def receive: Receive = uninitialised

  def initialised(customerId: UUID, balance: BigDecimal, count: Int, merchants: List[UUID], payments: List[CardPayment], replyTo: ActorRef): Receive = {
    case GeneratePayment =>
      val timestamp = generateTimestamp()
      val countryCode = countryCodes.get(Random.nextInt(countryCodes.size))
      val deviceId = devices(Random.nextInt(devices.size))
      val merchantId = merchants(Random.nextInt(merchants.size))
      val amount = generateAmount()
      val newPayments = payments :+ CardPayment(timestamp, countryCode, customerId, deviceId, merchantId, amount)

      if (newPayments.size == count) {
        become(uninitialised)
        replyTo ! GeneratedPayments(customerId, payments)
      } else {
        become(initialised(customerId, balance, count, merchants, newPayments, replyTo))
        self ! GeneratePayment
      }
  }

  def uninitialised: Receive = {

    case GeneratePayments(id, count, merchants) =>
      log.info(s"generating $count payments for user $id")
      become(initialised(id, generateBalance(), count, merchants, List.empty, sender()))
      self ! GeneratePayment
  }

  def generateDeviceIds(count: Int) = {
    (1 to count).map { _ =>
      UUID.randomUUID()
    }.toList
  }

  def generateBalance(): BigDecimal = {
    val amount = fromBalance + (Random.nextDouble() * (toBalance - fromBalance))
    BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)
  }

  def generateTimestamp(): Long = {
    val diff = ChronoUnit.DAYS.between(fromDate, toDate)
    val ldt = fromDate.plusDays(Random.nextInt(diff.toInt))
    val zdt = ldt.atZone(ZoneId.of("GMT"))

    zdt.toInstant.toEpochMilli
  }

  def generateAmount(): BigDecimal = {
    val amount = fromAmount + (Random.nextDouble() * (toAmount - fromAmount))
    BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)
  }
}

object CustomerActor {

  case object GeneratePayment

  def props = Props[CustomerActor]
}
