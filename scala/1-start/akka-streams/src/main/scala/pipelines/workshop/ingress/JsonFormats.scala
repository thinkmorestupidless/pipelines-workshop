package pipelines.workshop.ingress

import pipelines.workshop.schema.CardPayment
import spray.json._

case object JsonFormats extends DefaultJsonProtocol {

  implicit object TxRecordFormat extends RootJsonFormat[CardPayment] {
    def write(tx: CardPayment) = JsObject(
      "timestamp" -> JsNumber(tx.timestamp),
      "countryCode" -> JsString(tx.countryCode),
      "customerId" -> JsString(tx.customerId),
      "deviceId" -> JsString(tx.deviceId),
      "merchantId" -> JsString(tx.merchantId),
      "amount" -> JsNumber(tx.amount.toString)
    )

    def read(json: JsValue) = {
      json.asJsObject.getFields("timestamp", "countryCode", "customerId", "deviceId", "merchantId", "amount") match {
        case Seq(JsNumber(timestamp), JsString(countryCode), JsString(customerId), JsString(deviceId), JsString(merchantId), JsNumber(amount)) ⇒
          CardPayment(timestamp.toLong, countryCode, customerId, deviceId, merchantId, amount.toDouble)

        case other ⇒ deserializationError(s"Expected CardPayment but got $other")
      }
    }
  }
}
