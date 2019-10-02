package pipelines.workshop.processor

import akka.stream.scaladsl.RunnableGraph
import pipelines.akkastream.scaladsl.{ FlowWithPipelinesContext, RunnableGraphStreamletLogic }
import pipelines.akkastream.util.scaladsl.SplitterLogic
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.streamlets.{ DoubleConfigParameter, StreamletShape }
import pipelines.workshop.schema.CardPayment

object FilterPaymentsByAmount extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("in")

  val outlet = AvroOutlet[CardPayment]("out", _.customerId)

  val shape = StreamletShape(inlet, outlet)

  //  val leftOutlet = AvroOutlet[CardPayment]("left", _.customerId)
  //  val rightOutlet = AvroOutlet[CardPayment]("right", _.customerId)
  //
  //  val shape = StreamletShape.withInlets(inlet).withOutlets(leftOutlet, rightOutlet)

  val FilteringResource = DoubleConfigParameter(
    key = "maxPayment",
    description = "Provide the resource to use for payment filtering"
  )

  //  override protected def createLogic(): StreamletLogic = new SplitterLogic[CardPayment, CardPayment, CardPayment](inlet, leftOutlet, rightOutlet) {
  //    override def flow: FlowWithPipelinesContext[CardPayment, Either[CardPayment, CardPayment]] = {
  //      flowWithPipelinesContext().map { payment ⇒ payment }
  //    }
  //  }

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    val maxPaymentValue = streamletConfig.getDouble(FilteringResource.key)

    def flow = FlowWithPipelinesContext[CardPayment].filter(payment ⇒ payment.amount > maxPaymentValue)

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).via(flow.named("maxPaymentFilter")).to(atLeastOnceSink(outlet))
  }
}
