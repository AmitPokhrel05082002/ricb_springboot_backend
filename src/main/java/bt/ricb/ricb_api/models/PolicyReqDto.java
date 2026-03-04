package bt.ricb.ricb_api.models;

import bt.ricb.ricb_api.models.CustomerDto;
import java.sql.Date;
import java.util.List;

public class PolicyReqDto {
	private String carrierType;
	private String carrierNo;
	private String startDate;
	private String endDate;
	private String originPlace;
	private String destination;
	private Integer duration;

	public String getCarrierType() {
		return this.carrierType;
	}

	private String operatorName;
	private String customerCode;
	private String productCode;
	private String proposerCid;
	private String passport;
	private String underwritingYear;
	private Date proposalDate;

	public void setCarrierType(String carrierType) {
		this.carrierType = carrierType;
	}

	public String getCarrierNo() {
		return this.carrierNo;
	}

	public void setCarrierNo(String carrierNo) {
		this.carrierNo = carrierNo;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getOriginPlace() {
		return this.originPlace;
	}

	public void setOriginPlace(String originPlace) {
		this.originPlace = originPlace;
	}

	public String getDestination() {
		return this.destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getOperatorName() {
		return this.operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getCustomerCode() {
		return this.customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProposerCid() {
		return this.proposerCid;
	}

	public void setProposerCid(String proposerCid) {
		this.proposerCid = proposerCid;
	}

	public String getPassport() {
		return this.passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getUnderwritingYear() {
		return this.underwritingYear;
	}

	public void setUnderwritingYear(String underwritingYear) {
		this.underwritingYear = underwritingYear;
	}

	public Date getProposalDate() {
		return this.proposalDate;
	}

	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}

	public List<CustomerDto> getCustomers() {
		return null;
	}
}
