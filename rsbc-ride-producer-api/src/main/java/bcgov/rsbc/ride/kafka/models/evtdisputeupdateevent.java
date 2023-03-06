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
        "dispute_action_date",
        "dispute_action_code"
})
//@Generated("jsonschema2pojo")
public class evtdisputeupdateevent {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("count_number")
    private Integer countNumber;
    @JsonProperty("dispute_action_date")
    private String disputeActionDate;
    @JsonProperty("dispute_action_code")
    private String disputeActionCode;
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

    @JsonProperty("dispute_action_date")
    public String getDisputeActionDate() {
        return disputeActionDate;
    }

    @JsonProperty("dispute_action_date")
    public void setDisputeActionDate(String disputeActionDate) {
        this.disputeActionDate = disputeActionDate;
    }

    @JsonProperty("dispute_action_code")
    public String getDisputeActionCode() {
        return disputeActionCode;
    }

    @JsonProperty("dispute_action_code")
    public void setDisputeActionCode(String disputeActionCode) {
        this.disputeActionCode = disputeActionCode;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}