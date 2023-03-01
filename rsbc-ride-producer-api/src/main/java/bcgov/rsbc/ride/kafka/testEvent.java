
package bcgov.rsbc.ride.kafka;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * eTkTestEvent
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ticket_number",
    "count_number",
    "dispute_action_date",
    "dispute_type_code",
    "count_act_regulation",
    "compressed_section"
})
@Generated("jsonschema2pojo")
public class testEvent {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("count_number")
    private Integer countNumber;
    @JsonProperty("dispute_action_date")
    private String disputeActionDate;
    @JsonProperty("dispute_type_code")
    private String disputeTypeCode;
    @JsonProperty("count_act_regulation")
    private String countActRegulation;
    @JsonProperty("compressed_section")
    private String compressedSection;
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

    @JsonProperty("dispute_type_code")
    public String getDisputeTypeCode() {
        return disputeTypeCode;
    }

    @JsonProperty("dispute_type_code")
    public void setDisputeTypeCode(String disputeTypeCode) {
        this.disputeTypeCode = disputeTypeCode;
    }

    @JsonProperty("count_act_regulation")
    public String getCountActRegulation() {
        return countActRegulation;
    }

    @JsonProperty("count_act_regulation")
    public void setCountActRegulation(String countActRegulation) {
        this.countActRegulation = countActRegulation;
    }

    @JsonProperty("compressed_section")
    public String getCompressedSection() {
        return compressedSection;
    }

    @JsonProperty("compressed_section")
    public void setCompressedSection(String compressedSection) {
        this.compressedSection = compressedSection;
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
