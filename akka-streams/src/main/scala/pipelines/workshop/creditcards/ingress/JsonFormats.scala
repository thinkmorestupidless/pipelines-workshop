package pipelines.workshop.creditcards.ingress

import pipelines.workshop.creditcards.schema.CardPayment
import spray.json._

case object JsonFormats extends DefaultJsonProtocol {

  implicit object TxRecordFormat extends RootJsonFormat[CardPayment] {
    def write(tx: CardPayment) = JsObject(
      "time" -> JsNumber(tx.time.toDouble),
      "v1" -> JsNumber(tx.v1.toDouble),
      "v2" -> JsNumber(tx.v2.toDouble),
      "v3" -> JsNumber(tx.v3.toDouble),
      "v4" -> JsNumber(tx.v4.toDouble),
      "v5" -> JsNumber(tx.v5.toDouble),
      "v6" -> JsNumber(tx.v6.toDouble),
      "v7" -> JsNumber(tx.v7.toDouble),
      "v9" -> JsNumber(tx.v9.toDouble),
      "v10" -> JsNumber(tx.v10.toDouble),
      "v11" -> JsNumber(tx.v11.toDouble),
      "v12" -> JsNumber(tx.v12.toDouble),
      "v14" -> JsNumber(tx.v14.toDouble),
      "v16" -> JsNumber(tx.v16.toDouble),
      "v17" -> JsNumber(tx.v17.toDouble),
      "v18" -> JsNumber(tx.v18.toDouble),
      "v19" -> JsNumber(tx.v19.toDouble),
      "v21" -> JsNumber(tx.v21.toDouble),
      "amount" -> JsNumber(tx.amount.toDouble)
    )

    def read(json: JsValue) = {
      json.asJsObject.getFields(
        "time",
        "v1",
        "v2",
        "v3",
        "v4",
        "v5",
        "v6",
        "v7",
        "v9",
        "v10",
        "v11",
        "v12",
        "v14",
        "v16",
        "v17",
        "v18",
        "v19",
        "v21",
        "amount"
      ) match {
          case Seq(
            JsNumber(time),
            JsNumber(v1),
            JsNumber(v2),
            JsNumber(v3),
            JsNumber(v4),
            JsNumber(v5),
            JsNumber(v6),
            JsNumber(v7),
            JsNumber(v9),
            JsNumber(v10),
            JsNumber(v11),
            JsNumber(v12),
            JsNumber(v14),
            JsNumber(v16),
            JsNumber(v17),
            JsNumber(v18),
            JsNumber(v19),
            JsNumber(v21),
            JsNumber(amount)) ⇒
            CardPayment(time.toFloat, v1.toFloat, v2.toFloat, v3.toFloat, v4.toFloat, v5.toFloat, v6.toFloat, v7.toFloat, v9.toFloat, v10.toFloat, v11.toFloat, v12.toFloat, v14.toFloat, v16.toFloat, v17.toFloat, v18.toFloat, v19.toFloat, v21.toFloat, amount.toFloat)

          case other ⇒ deserializationError(s"Expected CardPayment but got $other")
        }
    }
  }
}
