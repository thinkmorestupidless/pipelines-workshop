package pipelines.workshop.processor

import pipelines.akkastream.scaladsl.FlowWithPipelinesContext
import pipelines.akkastream.util.scaladsl.SplitterLogic
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.workshop.schema.CardPayment

class CardPaymentSplit extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("int")
  val outLeft = AvroOutlet[CardPayment]("out-0")
  val outRight = AvroOutlet[CardPayment]("out-1")

  val shape = StreamletShape.withInlets(inlet).withOutlets(outLeft, outRight)

  override protected def createLogic(): SplitterLogic[CardPayment, CardPayment, CardPayment] = {
    new SplitterLogic[CardPayment, CardPayment, CardPayment](inlet, outLeft, outRight) {
      override def flow: FlowWithPipelinesContext[CardPayment, Either[CardPayment, CardPayment]] = {
        flowWithPipelinesContext().map { payment ⇒ Left(payment) }
      }
    }
  }
}
