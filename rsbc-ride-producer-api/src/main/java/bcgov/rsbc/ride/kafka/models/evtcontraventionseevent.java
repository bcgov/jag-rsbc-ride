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
        "act_code",
        "section_text",
        "section_desc",
        "fine_amount",
        "wording_number"
})


public class evtcontraventionseevent {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("count_number")
    private Integer countNumber;
    @JsonProperty("act_code")
    private String actCode;
    @JsonProperty("section_text")
    private String sectionText;
    @JsonProperty("section_desc")
    private String sectionDesc;
    @JsonProperty("fine_amount")
    private String fineAmount;
    @JsonProperty("wording_number")
    private Integer wordingNumber;
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

    @JsonProperty("act_code")
    public String getActCode() {
        return actCode;
    }

    @JsonProperty("act_code")
    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    @JsonProperty("section_text")
    public String getSectionText() {
        return sectionText;
    }

    @JsonProperty("section_text")
    public void setSectionText(String sectionText) {
        this.sectionText = sectionText;
    }

    @JsonProperty("section_desc")
    public String getSectionDesc() {
        return sectionDesc;
    }

    @JsonProperty("section_desc")
    public void setSectionDesc(String sectionDesc) {
        this.sectionDesc = sectionDesc;
    }

    @JsonProperty("fine_amount")
    public String getFineAmount() {
        return fineAmount;
    }

    @JsonProperty("fine_amount")
    public void setFineAmount(String fineAmount) {
        this.fineAmount = fineAmount;
    }

    @JsonProperty("wording_number")
    public Integer getWordingNumber() {
        return wordingNumber;
    }

    @JsonProperty("wording_number")
    public void setWordingNumber(Integer wordingNumber) {
        this.wordingNumber = wordingNumber;
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
        sb.append(evtcontraventionseevent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null)?"<null>":this.ticketNumber));
        sb.append(',');
        sb.append("countNumber");
        sb.append('=');
        sb.append(((this.countNumber == null)?"<null>":this.countNumber));
        sb.append(',');
        sb.append("actCode");
        sb.append('=');
        sb.append(((this.actCode == null)?"<null>":this.actCode));
        sb.append(',');
        sb.append("sectionText");
        sb.append('=');
        sb.append(((this.sectionText == null)?"<null>":this.sectionText));
        sb.append(',');
        sb.append("sectionDesc");
        sb.append('=');
        sb.append(((this.sectionDesc == null)?"<null>":this.sectionDesc));
        sb.append(',');
        sb.append("fineAmount");
        sb.append('=');
        sb.append(((this.fineAmount == null)?"<null>":this.fineAmount));
        sb.append(',');
        sb.append("wordingNumber");
        sb.append('=');
        sb.append(((this.wordingNumber == null)?"<null>":this.wordingNumber));
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