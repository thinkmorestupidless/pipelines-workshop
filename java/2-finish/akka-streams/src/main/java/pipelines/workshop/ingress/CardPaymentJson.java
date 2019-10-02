package pipelines.workshop.ingress;
import pipelines.workshop.schema.java.*;

public class CardPaymentJson {
  private long timestamp;
  private String countryCode;
  private String customerId;
  private String deviceId;
  private String merchantId;
  private double amount;

  public CardPaymentJson() {
  }
  public CardPaymentJson(
    long timestamp,
    String countryCode,
    String customerId,
    String deviceId,
    String merchantId,
    double amount
  ) {
    this.timestamp = timestamp;
    this.countryCode = countryCode;
    this.customerId = customerId;
    this.deviceId = deviceId;
    this.merchantId = merchantId;
    this.amount = amount;
  }
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  public long getTimestamp() {
    return timestamp;
  }
  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }
  public String getCountryCode() {
    return countryCode;
  }
  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }
  public String getCustomerId() {
    return customerId;
  }
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }
  public String getDeviceId() {
    return deviceId;
  }
  public void setMerchantId(String merchantId) {
    this.merchantId = merchantId;
  }
  public String getMerchantId() {
    return merchantId;
  }
  public void setAmount(double amount) {
    this.amount = amount;
  }
  public double getAmount() {
    return amount;
  }
  public CardPayment toCardPayment() {
    return new CardPayment(
      timestamp,
      countryCode,
      customerId,
      deviceId,
      merchantId,
      amount       
    );
  }

  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof CardPaymentJson)) return false;
    CardPaymentJson that = (CardPaymentJson) object;

    return getTimestamp() == that.getTimestamp() &&
            Double.compare(that.getAmount(), getAmount()) == 0 &&
            java.util.Objects.equals(getCountryCode(), that.getCountryCode()) &&
            java.util.Objects.equals(getCustomerId(), that.getCustomerId()) &&
            java.util.Objects.equals(getDeviceId(), that.getDeviceId()) &&
            java.util.Objects.equals(getMerchantId(), that.getMerchantId());
  }

  public int hashCode() {
    return java.util.Objects.hash(super.hashCode(), getTimestamp(), getCountryCode(), getCustomerId(), getDeviceId(), getMerchantId(), getAmount());
  }
  public String toString() {
    return "CardPaymentJson(" +
            getTimestamp() + "," + 
            getCountryCode().toString() + "," + 
            getCustomerId().toString() + "," + 
            getDeviceId().toString() + "," + 
            getMerchantId().toString() + "," + 
            getAmount() +
           ")";
  }
}
