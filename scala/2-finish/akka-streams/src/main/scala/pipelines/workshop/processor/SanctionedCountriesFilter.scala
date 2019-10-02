package pipelines.workshop.processor

import akka.stream.scaladsl.RunnableGraph
import pipelines.akkastream.scaladsl.{ FlowWithPipelinesContext, RunnableGraphStreamletLogic }
import pipelines.akkastream.{ AkkaStreamlet, StreamletLogic }
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.streamlets.{ StreamletShape, StringConfigParameter }
import pipelines.workshop.schema.CardPayment

class SanctionedCountriesFilter extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("in")
  val outlet = AvroOutlet[CardPayment]("out", _.countryCode)

  val shape = StreamletShape(inlet, outlet)

  val FilteringResource = StringConfigParameter(
    key = "sanctioned-countries",
    description = "Payments from these countries require manual processing",
    Some("RU")
  )

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    println(streamletConfig.entrySet())

    val sanctionedCountriesConfig = if (streamletConfig.hasPath(FilteringResource.key)) streamletConfig.getString(FilteringResource.key) else FilteringResource.defaultValue.get
    val sanctionedCountries = sanctionedCountriesConfig.split(",")

    def flow =
      FlowWithPipelinesContext[CardPayment].filter { payment ⇒ sanctionedCountries.contains(payment.countryCode) }

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).via(flow).to(atLeastOnceSink(outlet))
  }
}
