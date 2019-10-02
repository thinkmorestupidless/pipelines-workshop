package pipelines.workshop.aggregation

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.TimestampType
import pipelines.spark.sql.SQLImplicits._
import pipelines.spark.{ SparkStreamlet, SparkStreamletLogic, StreamletQueryExecution }
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro.{ AvroInlet, AvroOutlet }
import pipelines.workshop.schema.java.CardPayment
import pipelines.workshop.schema.scala.CustomerSpendSummary

class CustomerSpendAggregation extends SparkStreamlet {

  val inlet = AvroInlet[CardPayment]("in")
  val outlet = AvroOutlet[CustomerSpendSummary]("out", _.customerId)

  val shape = StreamletShape(inlet, outlet)

  override protected def createLogic(): SparkStreamletLogic = new SparkStreamletLogic() {
    override def buildStreamingQueries: StreamletQueryExecution = {
      val dataset = readStream(inlet)
      val outStream = process(dataset)
      writeStream(outStream, outlet, OutputMode.Update()).toQueryExecution
    }

    private def process(inDataset: Dataset[CardPayment]): Dataset[CustomerSpendSummary] = {
      val query = inDataset
        .withColumn("ts", $"timestamp".cast(TimestampType))
        .withWatermark("ts", "10 minutes")
        .groupBy(window($"ts", "60 minutes", "60 minutes"), $"customerId")

      query.agg(
        min($"amount") as "amountMin",
        max($"amount") as "amountMax",
        avg($"amount") as "amountAvg",
        variance($"amount") as "amountVar",
        stddev($"amount") as "amountStDev"
      ).withColumn("year", year($"window.start"))
        .withColumn("month", month($"window.start"))
        //        .withColumn("day", dayofmonth($"window.start"))
        //        .withColumn("hour", hour($"window.start"))
        .as[CustomerSpendSummary]
    }
  }
}
