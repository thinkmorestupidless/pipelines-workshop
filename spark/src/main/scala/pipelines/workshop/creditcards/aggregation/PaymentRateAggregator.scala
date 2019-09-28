package pipelines.workshop.creditcards.aggregation

import pipelines.spark.sql.SQLImplicits._

import pipelines.spark.{ SparkStreamlet, SparkStreamletLogic, StreamletQueryExecution }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.workshop.creditcards.schema.{ CardPayment, PaymentAggregation }

object PaymentRateAggregator /*extends SparkStreamlet*/ {

  //  val inlet = AvroInlet[CardPayment]("in")
  //  val outlet = AvroOutlet[PaymentAggregation]("out")
  //
  //  val shape = StreamletShape(inlet, outlet)
  //
  //  override protected def createLogic(): SparkStreamletLogic = new SparkStreamletLogic() {
  //    override def buildStreamingQueries: StreamletQueryExecution = {
  //      val dataset = readStream(inlet)
  ////      val query = dataset.with
  //      Set()
  //    }
  //  }
}
