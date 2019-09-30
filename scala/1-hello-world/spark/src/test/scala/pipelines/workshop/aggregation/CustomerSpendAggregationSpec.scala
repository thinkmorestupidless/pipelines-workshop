package pipelines.workshop.aggregation

import java.util.UUID

import scala.collection.immutable.Seq
import scala.concurrent.duration._
import org.apache.spark.sql.streaming.OutputMode
import pipelines.streamlets.StreamletShape
import pipelines.streamlets.avro._
import pipelines.spark.avro._
import pipelines.spark.testkit._
import pipelines.spark.sql.SQLImplicits._
import pipelines.workshop.schema.{ CardPayment, CustomerSpendAgg }

class CustomerSpendAggregationSpec extends SparkScalaTestSupport {

  "CustomerSpendAggregation" should {

    val testkit = SparkStreamletTestkit(session)

    "rollup data for a customer" in {
      val aggregator = new CustomerSpendAggregation

      val in: SparkInletTap[CardPayment] = testkit.inletAsTap[CardPayment](aggregator.inlet)
      val out: SparkOutletTap[CustomerSpendAgg] = testkit.outletAsTap[CustomerSpendAgg](aggregator.outlet)

      val customerId = UUID.randomUUID().toString
      val payment1 = CardPayment(0.toLong, "GB", customerId, UUID.randomUUID().toString, UUID.randomUUID().toString, 100)
      val payment2 = CardPayment(0.toLong, "GB", customerId, UUID.randomUUID().toString, UUID.randomUUID().toString, 5.99)

      in.addData(Seq(payment1, payment2))

      testkit.run(aggregator, Seq(in), Seq(out), 2.seconds)

      val results = out.asCollection(session)

      println(results.size + " results")
      results.foreach(r ⇒ println("result => " + r))
    }
  }
}
