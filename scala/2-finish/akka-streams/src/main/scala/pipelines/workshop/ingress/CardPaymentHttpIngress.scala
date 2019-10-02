package pipelines.workshop.ingress

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import pipelines.akkastream.util.scaladsl.HttpServerLogic
import pipelines.akkastream.{ AkkaServerStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.AvroOutlet
import pipelines.workshop.ingress.JsonFormats._
import pipelines.workshop.schema.CardPayment

object CardPaymentHttpIngress extends AkkaServerStreamlet {

  val out = AvroOutlet[CardPayment]("out", payment ⇒ payment.customerId)
  val shape = StreamletShape.withOutlets(out)

  override protected def createLogic(): StreamletLogic =
    HttpServerLogic.default(this, out)
}
