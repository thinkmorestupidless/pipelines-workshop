package pipelines.workshop.egress

import akka.stream.scaladsl.RunnableGraph
import pipelines.akkastream.scaladsl.{ FlowWithPipelinesContext, RunnableGraphStreamletLogic }
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.AvroInlet
import pipelines.workshop.schema.CardPayment

object CardPaymentLogger extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("in")
  val shape = StreamletShape.withInlets(inlet)

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    val flow = FlowWithPipelinesContext[CardPayment].map { cp ⇒
      system.log.info("Hello, Card Payment! " + cp.toString)
      cp
    }

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).via(flow).to(atLeastOnceSink)
  }
}
