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
        "submit_date",
        "sent_time",
        "drivers_licence_province_code",
        "person_gender_code",
        "person_residence_city_name",
        "person_residence_province_code",
        "young_person_yn",
        "offender_type_code",
        "violation_date",
        "violation_time",
        "violation_highway_desc",
        "violation_city_code",
        "violation_city_name",
        "vehicle_province_code",
        "vehicle_nsj_puj_cd",
        "vehicle_make_code",
        "vehicle_type_code",
        "vehicle_year",
        "accident_yn",
        "dispute_address_text",
        "court_location_code",
        "mre_agency_text",
        "enforcement_jurisdiction_code",
        "certificate_of_service_date",
        "certificate_of_service_number",
        "e_violation_form_number",
        "enforcement_jurisdiction_name",
        "mre_minor_version_text",
        "count_quantity",
        "enforcement_officer_number",
        "enforcement_officer_name",
        "sent_date",
        "enforcement_org_unit_cd",
        "enforcement_org_unit_cd_txt"
})


public class evtissuanceeevent {

    @JsonProperty("ticket_number")
    private String ticketNumber;
    @JsonProperty("submit_date")
    private String submitDate;
    @JsonProperty("sent_time")
    private String sentTime;
    @JsonProperty("drivers_licence_province_code")
    private String driversLicenceProvinceCode;
    @JsonProperty("person_gender_code")
    private String personGenderCode;
    @JsonProperty("person_residence_city_name")
    private String personResidenceCityName;
    @JsonProperty("person_residence_province_code")
    private String personResidenceProvinceCode;
    @JsonProperty("young_person_yn")
    private String youngPersonYn;
    @JsonProperty("offender_type_code")
    private String offenderTypeCode;
    @JsonProperty("violation_date")
    private String violationDate;
    @JsonProperty("violation_time")
    private String violationTime;
    @JsonProperty("violation_highway_desc")
    private String violationHighwayDesc;
    @JsonProperty("violation_city_code")
    private String violationCityCode;
    @JsonProperty("violation_city_name")
    private String violationCityName;
    @JsonProperty("vehicle_province_code")
    private String vehicleProvinceCode;
    @JsonProperty("vehicle_nsj_puj_cd")
    private String vehicleNsjPujCd;
    @JsonProperty("vehicle_make_code")
    private String vehicleMakeCode;
    @JsonProperty("vehicle_type_code")
    private String vehicleTypeCode;
    @JsonProperty("vehicle_year")
    private String vehicleYear;
    @JsonProperty("accident_yn")
    private String accidentYn;
    @JsonProperty("dispute_address_text")
    private String disputeAddressText;
    @JsonProperty("court_location_code")
    private String courtLocationCode;
    @JsonProperty("mre_agency_text")
    private String mreAgencyText;
    @JsonProperty("enforcement_jurisdiction_code")
    private String enforcementJurisdictionCode;
    @JsonProperty("certificate_of_service_date")
    private String certificateOfServiceDate;
    @JsonProperty("certificate_of_service_number")
    private String certificateOfServiceNumber;
    @JsonProperty("e_violation_form_number")
    private String eViolationFormNumber;
    @JsonProperty("enforcement_jurisdiction_name")
    private String enforcementJurisdictionName;
    @JsonProperty("mre_minor_version_text")
    private String mreMinorVersionText;
    @JsonProperty("count_quantity")
    private Integer countQuantity;
    @JsonProperty("enforcement_officer_number")
    private String enforcementOfficerNumber;
    @JsonProperty("enforcement_officer_name")
    private String enforcementOfficerName;
    @JsonProperty("sent_date")
    private String sentDate;
    @JsonProperty("enforcement_org_unit_cd")
    private String enforcementOrgUnitCd;
    @JsonProperty("enforcement_org_unit_cd_txt")
    private String enforcementOrgUnitCdTxt;
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

    @JsonProperty("submit_date")
    public String getSubmitDate() {
        return submitDate;
    }

    @JsonProperty("submit_date")
    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    @JsonProperty("sent_time")
    public String getSentTime() {
        return sentTime;
    }

    @JsonProperty("sent_time")
    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    @JsonProperty("drivers_licence_province_code")
    public String getDriversLicenceProvinceCode() {
        return driversLicenceProvinceCode;
    }

    @JsonProperty("drivers_licence_province_code")
    public void setDriversLicenceProvinceCode(String driversLicenceProvinceCode) {
        this.driversLicenceProvinceCode = driversLicenceProvinceCode;
    }

    @JsonProperty("person_gender_code")
    public String getPersonGenderCode() {
        return personGenderCode;
    }

    @JsonProperty("person_gender_code")
    public void setPersonGenderCode(String personGenderCode) {
        this.personGenderCode = personGenderCode;
    }

    @JsonProperty("person_residence_city_name")
    public String getPersonResidenceCityName() {
        return personResidenceCityName;
    }

    @JsonProperty("person_residence_city_name")
    public void setPersonResidenceCityName(String personResidenceCityName) {
        this.personResidenceCityName = personResidenceCityName;
    }

    @JsonProperty("person_residence_province_code")
    public String getPersonResidenceProvinceCode() {
        return personResidenceProvinceCode;
    }

    @JsonProperty("person_residence_province_code")
    public void setPersonResidenceProvinceCode(String personResidenceProvinceCode) {
        this.personResidenceProvinceCode = personResidenceProvinceCode;
    }

    @JsonProperty("young_person_yn")
    public String getYoungPersonYn() {
        return youngPersonYn;
    }

    @JsonProperty("young_person_yn")
    public void setYoungPersonYn(String youngPersonYn) {
        this.youngPersonYn = youngPersonYn;
    }

    @JsonProperty("offender_type_code")
    public String getOffenderTypeCode() {
        return offenderTypeCode;
    }

    @JsonProperty("offender_type_code")
    public void setOffenderTypeCode(String offenderTypeCode) {
        this.offenderTypeCode = offenderTypeCode;
    }

    @JsonProperty("violation_date")
    public String getViolationDate() {
        return violationDate;
    }

    @JsonProperty("violation_date")
    public void setViolationDate(String violationDate) {
        this.violationDate = violationDate;
    }

    @JsonProperty("violation_time")
    public String getViolationTime() {
        return violationTime;
    }

    @JsonProperty("violation_time")
    public void setViolationTime(String violationTime) {
        this.violationTime = violationTime;
    }

    @JsonProperty("violation_highway_desc")
    public String getViolationHighwayDesc() {
        return violationHighwayDesc;
    }

    @JsonProperty("violation_highway_desc")
    public void setViolationHighwayDesc(String violationHighwayDesc) {
        this.violationHighwayDesc = violationHighwayDesc;
    }

    @JsonProperty("violation_city_code")
    public String getViolationCityCode() {
        return violationCityCode;
    }

    @JsonProperty("violation_city_code")
    public void setViolationCityCode(String violationCityCode) {
        this.violationCityCode = violationCityCode;
    }

    @JsonProperty("violation_city_name")
    public String getViolationCityName() {
        return violationCityName;
    }

    @JsonProperty("violation_city_name")
    public void setViolationCityName(String violationCityName) {
        this.violationCityName = violationCityName;
    }

    @JsonProperty("vehicle_province_code")
    public String getVehicleProvinceCode() {
        return vehicleProvinceCode;
    }

    @JsonProperty("vehicle_province_code")
    public void setVehicleProvinceCode(String vehicleProvinceCode) {
        this.vehicleProvinceCode = vehicleProvinceCode;
    }

    @JsonProperty("vehicle_nsj_puj_cd")
    public String getVehicleNsjPujCd() {
        return vehicleNsjPujCd;
    }

    @JsonProperty("vehicle_nsj_puj_cd")
    public void setVehicleNsjPujCd(String vehicleNsjPujCd) {
        this.vehicleNsjPujCd = vehicleNsjPujCd;
    }

    @JsonProperty("vehicle_make_code")
    public String getVehicleMakeCode() {
        return vehicleMakeCode;
    }

    @JsonProperty("vehicle_make_code")
    public void setVehicleMakeCode(String vehicleMakeCode) {
        this.vehicleMakeCode = vehicleMakeCode;
    }

    @JsonProperty("vehicle_type_code")
    public String getVehicleTypeCode() {
        return vehicleTypeCode;
    }

    @JsonProperty("vehicle_type_code")
    public void setVehicleTypeCode(String vehicleTypeCode) {
        this.vehicleTypeCode = vehicleTypeCode;
    }

    @JsonProperty("vehicle_year")
    public String getVehicleYear() {
        return vehicleYear;
    }

    @JsonProperty("vehicle_year")
    public void setVehicleYear(String vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    @JsonProperty("accident_yn")
    public String getAccidentYn() {
        return accidentYn;
    }

    @JsonProperty("accident_yn")
    public void setAccidentYn(String accidentYn) {
        this.accidentYn = accidentYn;
    }

    @JsonProperty("dispute_address_text")
    public String getDisputeAddressText() {
        return disputeAddressText;
    }

    @JsonProperty("dispute_address_text")
    public void setDisputeAddressText(String disputeAddressText) {
        this.disputeAddressText = disputeAddressText;
    }

    @JsonProperty("court_location_code")
    public String getCourtLocationCode() {
        return courtLocationCode;
    }

    @JsonProperty("court_location_code")
    public void setCourtLocationCode(String courtLocationCode) {
        this.courtLocationCode = courtLocationCode;
    }

    @JsonProperty("mre_agency_text")
    public String getMreAgencyText() {
        return mreAgencyText;
    }

    @JsonProperty("mre_agency_text")
    public void setMreAgencyText(String mreAgencyText) {
        this.mreAgencyText = mreAgencyText;
    }

    @JsonProperty("enforcement_jurisdiction_code")
    public String getEnforcementJurisdictionCode() {
        return enforcementJurisdictionCode;
    }

    @JsonProperty("enforcement_jurisdiction_code")
    public void setEnforcementJurisdictionCode(String enforcementJurisdictionCode) {
        this.enforcementJurisdictionCode = enforcementJurisdictionCode;
    }

    @JsonProperty("certificate_of_service_date")
    public String getCertificateOfServiceDate() {
        return certificateOfServiceDate;
    }

    @JsonProperty("certificate_of_service_date")
    public void setCertificateOfServiceDate(String certificateOfServiceDate) {
        this.certificateOfServiceDate = certificateOfServiceDate;
    }

    @JsonProperty("certificate_of_service_number")
    public String getCertificateOfServiceNumber() {
        return certificateOfServiceNumber;
    }

    @JsonProperty("certificate_of_service_number")
    public void setCertificateOfServiceNumber(String certificateOfServiceNumber) {
        this.certificateOfServiceNumber = certificateOfServiceNumber;
    }

    @JsonProperty("e_violation_form_number")
    public String geteViolationFormNumber() {
        return eViolationFormNumber;
    }

    @JsonProperty("e_violation_form_number")
    public void seteViolationFormNumber(String eViolationFormNumber) {
        this.eViolationFormNumber = eViolationFormNumber;
    }

    @JsonProperty("enforcement_jurisdiction_name")
    public String getEnforcementJurisdictionName() {
        return enforcementJurisdictionName;
    }

    @JsonProperty("enforcement_jurisdiction_name")
    public void setEnforcementJurisdictionName(String enforcementJurisdictionName) {
        this.enforcementJurisdictionName = enforcementJurisdictionName;
    }

    @JsonProperty("mre_minor_version_text")
    public String getMreMinorVersionText() {
        return mreMinorVersionText;
    }

    @JsonProperty("mre_minor_version_text")
    public void setMreMinorVersionText(String mreMinorVersionText) {
        this.mreMinorVersionText = mreMinorVersionText;
    }

    @JsonProperty("count_quantity")
    public Integer getCountQuantity() {
        return countQuantity;
    }

    @JsonProperty("count_quantity")
    public void setCountQuantity(Integer countQuantity) {
        this.countQuantity = countQuantity;
    }

    @JsonProperty("enforcement_officer_number")
    public String getEnforcementOfficerNumber() {
        return enforcementOfficerNumber;
    }

    @JsonProperty("enforcement_officer_number")
    public void setEnforcementOfficerNumber(String enforcementOfficerNumber) {
        this.enforcementOfficerNumber = enforcementOfficerNumber;
    }

    @JsonProperty("enforcement_officer_name")
    public String getEnforcementOfficerName() {
        return enforcementOfficerName;
    }

    @JsonProperty("enforcement_officer_name")
    public void setEnforcementOfficerName(String enforcementOfficerName) {
        this.enforcementOfficerName = enforcementOfficerName;
    }

    @JsonProperty("sent_date")
    public String getSentDate() {
        return sentDate;
    }

    @JsonProperty("sent_date")
    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    @JsonProperty("enforcement_org_unit_cd")
    public String getEnforcementOrgUnitCd() {
        return enforcementOrgUnitCd;
    }

    @JsonProperty("enforcement_org_unit_cd")
    public void setEnforcementOrgUnitCd(String enforcementOrgUnitCd) {
        this.enforcementOrgUnitCd = enforcementOrgUnitCd;
    }

    @JsonProperty("enforcement_org_unit_cd_txt")
    public String getEnforcementOrgUnitCdTxt() {
        return enforcementOrgUnitCdTxt;
    }

    @JsonProperty("enforcement_org_unit_cd_txt")
    public void setEnforcementOrgUnitCdTxt(String enforcementOrgUnitCdTxt) {
        this.enforcementOrgUnitCdTxt = enforcementOrgUnitCdTxt;
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
        sb.append(evtissuanceeevent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ticketNumber");
        sb.append('=');
        sb.append(((this.ticketNumber == null)?"<null>":this.ticketNumber));
        sb.append(',');
        sb.append("submitDate");
        sb.append('=');
        sb.append(((this.submitDate == null)?"<null>":this.submitDate));
        sb.append(',');
        sb.append("sentTime");
        sb.append('=');
        sb.append(((this.sentTime == null)?"<null>":this.sentTime));
        sb.append(',');
        sb.append("driversLicenceProvinceCode");
        sb.append('=');
        sb.append(((this.driversLicenceProvinceCode == null)?"<null>":this.driversLicenceProvinceCode));
        sb.append(',');
        sb.append("personGenderCode");
        sb.append('=');
        sb.append(((this.personGenderCode == null)?"<null>":this.personGenderCode));
        sb.append(',');
        sb.append("personResidenceCityName");
        sb.append('=');
        sb.append(((this.personResidenceCityName == null)?"<null>":this.personResidenceCityName));
        sb.append(',');
        sb.append("personResidenceProvinceCode");
        sb.append('=');
        sb.append(((this.personResidenceProvinceCode == null)?"<null>":this.personResidenceProvinceCode));
        sb.append(',');
        sb.append("youngPersonYn");
        sb.append('=');
        sb.append(((this.youngPersonYn == null)?"<null>":this.youngPersonYn));
        sb.append(',');
        sb.append("offenderTypeCode");
        sb.append('=');
        sb.append(((this.offenderTypeCode == null)?"<null>":this.offenderTypeCode));
        sb.append(',');
        sb.append("violationDate");
        sb.append('=');
        sb.append(((this.violationDate == null)?"<null>":this.violationDate));
        sb.append(',');
        sb.append("violationTime");
        sb.append('=');
        sb.append(((this.violationTime == null)?"<null>":this.violationTime));
        sb.append(',');
        sb.append("violationHighwayDesc");
        sb.append('=');
        sb.append(((this.violationHighwayDesc == null)?"<null>":this.violationHighwayDesc));
        sb.append(',');
        sb.append("violationCityCode");
        sb.append('=');
        sb.append(((this.violationCityCode == null)?"<null>":this.violationCityCode));
        sb.append(',');
        sb.append("violationCityName");
        sb.append('=');
        sb.append(((this.violationCityName == null)?"<null>":this.violationCityName));
        sb.append(',');
        sb.append("vehicleProvinceCode");
        sb.append('=');
        sb.append(((this.vehicleProvinceCode == null)?"<null>":this.vehicleProvinceCode));
        sb.append(',');
        sb.append("vehicleNsjPujCd");
        sb.append('=');
        sb.append(((this.vehicleNsjPujCd == null)?"<null>":this.vehicleNsjPujCd));
        sb.append(',');
        sb.append("vehicleMakeCode");
        sb.append('=');
        sb.append(((this.vehicleMakeCode == null)?"<null>":this.vehicleMakeCode));
        sb.append(',');
        sb.append("vehicleTypeCode");
        sb.append('=');
        sb.append(((this.vehicleTypeCode == null)?"<null>":this.vehicleTypeCode));
        sb.append(',');
        sb.append("vehicleYear");
        sb.append('=');
        sb.append(((this.vehicleYear == null)?"<null>":this.vehicleYear));
        sb.append(',');
        sb.append("accidentYn");
        sb.append('=');
        sb.append(((this.accidentYn == null)?"<null>":this.accidentYn));
        sb.append(',');
        sb.append("disputeAddressText");
        sb.append('=');
        sb.append(((this.disputeAddressText == null)?"<null>":this.disputeAddressText));
        sb.append(',');
        sb.append("courtLocationCode");
        sb.append('=');
        sb.append(((this.courtLocationCode == null)?"<null>":this.courtLocationCode));
        sb.append(',');
        sb.append("mreAgencyText");
        sb.append('=');
        sb.append(((this.mreAgencyText == null)?"<null>":this.mreAgencyText));
        sb.append(',');
        sb.append("enforcementJurisdictionCode");
        sb.append('=');
        sb.append(((this.enforcementJurisdictionCode == null)?"<null>":this.enforcementJurisdictionCode));
        sb.append(',');
        sb.append("certificateOfServiceDate");
        sb.append('=');
        sb.append(((this.certificateOfServiceDate == null)?"<null>":this.certificateOfServiceDate));
        sb.append(',');
        sb.append("certificateOfServiceNumber");
        sb.append('=');
        sb.append(((this.certificateOfServiceNumber == null)?"<null>":this.certificateOfServiceNumber));
        sb.append(',');
        sb.append("eViolationFormNumber");
        sb.append('=');
        sb.append(((this.eViolationFormNumber == null)?"<null>":this.eViolationFormNumber));
        sb.append(',');
        sb.append("enforcementJurisdictionName");
        sb.append('=');
        sb.append(((this.enforcementJurisdictionName == null)?"<null>":this.enforcementJurisdictionName));
        sb.append(',');
        sb.append("mreMinorVersionText");
        sb.append('=');
        sb.append(((this.mreMinorVersionText == null)?"<null>":this.mreMinorVersionText));
        sb.append(',');
        sb.append("countQuantity");
        sb.append('=');
        sb.append(((this.countQuantity == null)?"<null>":this.countQuantity));
        sb.append(',');
        sb.append("enforcementOfficerNumber");
        sb.append('=');
        sb.append(((this.enforcementOfficerNumber == null)?"<null>":this.enforcementOfficerNumber));
        sb.append(',');
        sb.append("enforcementOfficerName");
        sb.append('=');
        sb.append(((this.enforcementOfficerName == null)?"<null>":this.enforcementOfficerName));
        sb.append(',');
        sb.append("sentDate");
        sb.append('=');
        sb.append(((this.sentDate == null)?"<null>":this.sentDate));
        sb.append(',');
        sb.append("enforcementOrgUnitCd");
        sb.append('=');
        sb.append(((this.enforcementOrgUnitCd == null)?"<null>":this.enforcementOrgUnitCd));
        sb.append(',');
        sb.append("enforcementOrgUnitCdTxt");
        sb.append('=');
        sb.append(((this.enforcementOrgUnitCdTxt == null)?"<null>":this.enforcementOrgUnitCdTxt));
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