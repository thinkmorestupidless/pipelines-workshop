package pipelines.workshop.processor

import pipelines.akkastream.AkkaStreamlet
import pipelines.akkastream.util.scaladsl.MergeLogic
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.workshop.schema.CardPayment

class CardPaymentMerge extends AkkaStreamlet {

  val in0 = AvroInlet[CardPayment]("in-0")
  val in1 = AvroInlet[CardPayment]("in-1")
  val out = AvroOutlet[CardPayment]("out", _.customerId)

  final override val shape = StreamletShape.withInlets(in0, in1).withOutlets(out)

  final override def createLogic = new MergeLogic[CardPayment](Vector(in0, in1), out)
}
