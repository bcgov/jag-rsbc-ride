package bcgov.rsbc.ride.kafka.models;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ticket_number",
        "count_number",
        "payment_card_type",
        "payment_ticket_type_code",
        "payment_amount",
        "transaction_id"
})

public class evtpaymenteevent {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("count_number")
    private Integer countNumber;
    @JsonProperty("payment_card_type")
    private String paymentCardType;
    @JsonProperty("payment_ticket_type_code")
    private String paymentTicketTypeCode;
    @JsonProperty("payment_amount")
    private Integer paymentAmount;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("ticket_number")
    public String getTicketNumber() {
        return ticketNumber;
    }

    @JsonProperty("ticket_number")
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @JsonProperty("count_number")
    public Integer getCountNumber() {
        return countNumber;
    }

    @JsonProperty("count_number")
    public void setCountNumber(Integer countNumber) {
        this.countNumber = countNumber;
    }

    @JsonProperty("payment_card_type")
    public String getPaymentCardType() {
        return paymentCardType;
    }

    @JsonProperty("payment_card_type")
    public void setPaymentCardType(String paymentCardType) {
        this.paymentCardType = paymentCardType;
    }

    @JsonProperty("payment_ticket_type_code")
    public String getPaymentTicketTypeCode() {
        return paymentTicketTypeCode;
    }

    @JsonProperty("payment_ticket_type_code")
    public void setPaymentTicketTypeCode(String paymentTicketTypeCode) {
        this.paymentTicketTypeCode = paymentTicketTypeCode;
    }

    @JsonProperty("payment_amount")
    public Integer getPaymentAmount() {
        return paymentAmount;
    }

    @JsonProperty("payment_amount")
    public void setPaymentAmount(Integer paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @JsonProperty("transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty("transaction_id")
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(evtpaymenteevent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null)?"<null>":this.ticketNumber));
        sb.append(',');
        sb.append("countNumber");
        sb.append('=');
        sb.append(((this.countNumber == null)?"<null>":this.countNumber));
        sb.append(',');
        sb.append("paymentCardType");
        sb.append('=');
        sb.append(((this.paymentCardType == null)?"<null>":this.paymentCardType));
        sb.append(',');
        sb.append("paymentTicketTypeCode");
        sb.append('=');
        sb.append(((this.paymentTicketTypeCode == null)?"<null>":this.paymentTicketTypeCode));
        sb.append(',');
        sb.append("paymentAmount");
        sb.append('=');
        sb.append(((this.paymentAmount == null)?"<null>":this.paymentAmount));
        sb.append(',');
        sb.append("transactionId");
        sb.append('=');
        sb.append(((this.transactionId == null)?"<null>":this.transactionId));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}