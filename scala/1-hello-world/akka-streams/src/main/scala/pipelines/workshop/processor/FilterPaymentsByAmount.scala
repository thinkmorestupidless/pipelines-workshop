package pipelines.workshop.processor

import akka.stream.scaladsl.RunnableGraph
import pipelines.akkastream.scaladsl.{ FlowWithPipelinesContext, RunnableGraphStreamletLogic }
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.streamlets.{ DoubleConfigParameter, StreamletShape }
import pipelines.workshop.schema.CardPayment

object FilterPaymentsByAmount extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("in")
  val outlet = AvroOutlet[CardPayment]("out", _.customerId)

  val shape = StreamletShape(inlet, outlet)

  val FilteringResource = DoubleConfigParameter(
    key = "maxPayment",
    description = "Provide the resource to use for payment filtering"
  )

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    val maxPaymentValue = streamletConfig.getDouble(FilteringResource.key)

    def flow = FlowWithPipelinesContext[CardPayment].filter(payment ⇒ payment.amount > maxPaymentValue)

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).via(flow.named("maxPaymentFilter")).to(atLeastOnceSink(outlet))
  }
}
