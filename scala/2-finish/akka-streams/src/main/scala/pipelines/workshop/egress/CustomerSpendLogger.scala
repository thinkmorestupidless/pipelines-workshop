package pipelines.workshop.egress

import akka.stream.scaladsl.RunnableGraph
import pipelines.akkastream.scaladsl.{ FlowWithPipelinesContext, RunnableGraphStreamletLogic }
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.AvroInlet
import pipelines.workshop.schema.CustomerSpendAgg

object CustomerSpendLogger extends AkkaStreamlet {

  val inlet = AvroInlet[CustomerSpendAgg]("in")
  val shape = StreamletShape.withInlets(inlet)

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    val flow = FlowWithPipelinesContext[CustomerSpendAgg].map { agg ⇒
      system.log.info(s"$agg")
      agg
    }

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).via(flow).to(atLeastOnceSink)
  }
}
