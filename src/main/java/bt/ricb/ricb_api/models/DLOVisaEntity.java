package bt.ricb.ricb_api.models;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dloVisa")
public class DLOVisaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime applicationDate;
	private String cid;
	private String applicantName;
	private int contactNo;
	private String passportNo;
	private String tDNo;
	private String countryOfVisit;
	private String purpose;
	private String educationType;
	private String nameOfPrivateConsultancyFirm;
	private String nameOfTrainingInstitute;
	private String employmentType;
	private String nameOfOverseaEmploymentAgent;
	private String nameOfEmployingOrganization;
	private String biometricDone;
	private String guardianName;
	private String guardianCID;
	private String guardianContactNo;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getApplicationDate() {
		return this.applicationDate;
	}

	public void setApplicationDate(LocalDateTime applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getCid() {
		return this.cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getApplicantName() {
		return this.applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public int getContactNo() {
		return this.contactNo;
	}

	public void setContactNo(int contactNo) {
		this.contactNo = contactNo;
	}

	public String getPassportNo() {
		return this.passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String gettDNo() {
		return this.tDNo;
	}

	public void settDNo(String tDNo) {
		this.tDNo = tDNo;
	}

	public String getCountryOfVisit() {
		return this.countryOfVisit;
	}

	public void setCountryOfVisit(String countryOfVisit) {
		this.countryOfVisit = countryOfVisit;
	}

	public String getPurpose() {
		return this.purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getEducationType() {
		return this.educationType;
	}

	public void setEducationType(String educationType) {
		this.educationType = educationType;
	}

	public String getNameOfPrivateConsultancyFirm() {
		return this.nameOfPrivateConsultancyFirm;
	}

	public void setNameOfPrivateConsultancyFirm(String nameOfPrivateConsultancyFirm) {
		this.nameOfPrivateConsultancyFirm = nameOfPrivateConsultancyFirm;
	}

	public String getNameOfTrainingInstitute() {
		return this.nameOfTrainingInstitute;
	}

	public void setNameOfTrainingInstitute(String nameOfTrainingInstitute) {
		this.nameOfTrainingInstitute = nameOfTrainingInstitute;
	}

	public String getEmploymentType() {
		return this.employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}

	public String getNameOfOverseaEmploymentAgent() {
		return this.nameOfOverseaEmploymentAgent;
	}

	public void setNameOfOverseaEmploymentAgent(String nameOfOverseaEmploymentAgent) {
		this.nameOfOverseaEmploymentAgent = nameOfOverseaEmploymentAgent;
	}

	public String getNameOfEmployingOrganization() {
		return this.nameOfEmployingOrganization;
	}

	public void setNameOfEmployingOrganization(String nameOfEmployingOrganization) {
		this.nameOfEmployingOrganization = nameOfEmployingOrganization;
	}

	public String getBiometricDone() {
		return this.biometricDone;
	}

	public void setBiometricDone(String biometricDone) {
		this.biometricDone = biometricDone;
	}

	public String getGuardianName() {
		return this.guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianCID() {
		return this.guardianCID;
	}

	public void setGuardianCID(String guardianCID) {
		this.guardianCID = guardianCID;
	}

	public String getGuardianContactNo() {
		return this.guardianContactNo;
	}

	public void setGuardianContactNo(String guardianContactNo) {
		this.guardianContactNo = guardianContactNo;
	}
}
