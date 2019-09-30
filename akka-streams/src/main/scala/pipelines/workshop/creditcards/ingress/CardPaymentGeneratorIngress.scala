package pipelines.workshop.creditcards.ingress

import akka.NotUsed
import akka.stream.scaladsl.Source
import pipelines.akkastream.scaladsl.RunnableGraphStreamletLogic
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.AvroOutlet
import pipelines.workshop.creditcards.schema.CardPayment

object CardPaymentGeneratorIngress extends AkkaStreamlet {

  val outlet = AvroOutlet[CardPayment]("out")
  val shape = StreamletShape(outlet)

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    //    def flow(): Flow[CardPayment] =
    //      Flow[CardPayment].map { _ ⇒
    //        CardPayment(1.toLong, "", "", "", 1.0)
    //      }

    override def runnableGraph() =
      Source.repeat(NotUsed)
        .map { _ ⇒ CardPayment(1.toLong, "", "", "", 1.0) }
        .to(atLeastOnceSink(outlet))
  }
}
