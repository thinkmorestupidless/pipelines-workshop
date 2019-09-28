package pipelines.workshop.creditcards.ingress

import pipelines.workshop.creditcards.schema.CardPayment
import spray.json._

case object JsonFormats extends DefaultJsonProtocol {

  implicit object TxRecordFormat extends RootJsonFormat[CardPayment] {
    def write(tx: CardPayment) = JsObject(
      "timestamp" -> JsNumber(tx.timestamp),
      "customerId" -> JsString(tx.customerId),
      "deviceId" -> JsString(tx.deviceId),
      "merchantId" -> JsString(tx.merchantId),
      "amount" -> JsNumber(tx.amount.toString)
    )

    def read(json: JsValue) = {
      json.asJsObject.getFields(
        "timestamp",
        "customerId",
        "deviceId",
        "merchantId",
        "amount",
      ) match {
          case Seq(
          JsNumber(timestamp),
          JsString(customerId),
          JsString(deviceId),
          JsString(merchantId),
          JsNumber(amount)) ⇒
            CardPayment(timestamp.toLong, customerId, deviceId, merchantId, amount.toDouble)

          case other ⇒ deserializationError(s"Expected CardPayment but got $other")
        }
    }
  }
}
