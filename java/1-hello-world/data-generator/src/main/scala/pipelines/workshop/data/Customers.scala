package pipelines.workshop.data

import java.util.UUID

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import pipelines.workshop.data.Customers.GeneratePayments

class Customers extends Actor with ActorLogging {
  import Customers._
  import context.become

  override def receive: Receive = behaviour(Map.empty)

  def behaviour(senders: Map[UUID, ActorRef]): Receive = {
    case msg: GeneratePayments =>
      val replyTo = sender()
      val customer = context.actorOf(CustomerActor.props, msg.id.toString)
      become(behaviour(senders + (msg.id -> replyTo)))
      customer ! msg

    case GeneratedPayments(id, payments) =>
      val replyTo = senders(id)
      replyTo ! payments
  }
}

object Customers {

  case class GeneratePayments(id: UUID, count: Int, merchants: List[UUID])
  case class GeneratedPayments(id: UUID, payments: List[CardPayment])

  def props = Props[Customers]
}
