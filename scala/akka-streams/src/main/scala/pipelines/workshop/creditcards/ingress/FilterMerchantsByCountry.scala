package pipelines.workshop.creditcards.ingress

object FilterMerchantsByCountry /*extends AkkaStreamlet {

  val inlet = AvroInlet[CardPayment]("in")
  val safeOutlet = AvroOutlet[CardPayment]("safe")
  val unsafeOutlet = AvroOutlet[CardPayment]("unsafe")

  val shape = StreamletShape.withInlets(inlet).withOutlets(safeOutlet, unsafeOutlet)

  override protected def createLogic(): StreamletLogic = new RunnableGraphStreamletLogic() {

    val countryFilter = GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      // logic to dispatch odd and even numbers to different outlets
      val partition = b.add(Partition[CardPayment](2, payment => 0))

      partition ~> atLeastOnceSink(safeOutlet)
      partition ~> atLeastOnceSink(unsafeOutlet)

      FlowShape(partition.in, )
    }

    def filterFlow =
      Flow.fromGraph(GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._
        val input = b.add(countryFilter)
      })

    override def runnableGraph(): RunnableGraph[_] =
      atLeastOnceSource(inlet).to(atLeastOnceSink(safeOutlet))
  }
}*/
