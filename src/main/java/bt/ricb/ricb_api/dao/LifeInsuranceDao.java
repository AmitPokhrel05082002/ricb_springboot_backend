package bt.ricb.ricb_api.dao;

import bt.ricb.ricb_api.models.FamilyDetailsDto;
import bt.ricb.ricb_api.models.LifeInsuranceMainDto;
import bt.ricb.ricb_api.models.NomineeDto;
import bt.ricb.ricb_api.models.PolicyCoverDto;
import bt.ricb.ricb_api.models.PolicyDiscountLoadDTO;
import bt.ricb.ricb_api.models.PolicyDto;
import bt.ricb.ricb_api.models.PolicyPremiumDto;

public interface LifeInsuranceDao {
	void lifeInsuranceMain(LifeInsuranceMainDto paramLifeInsuranceMainDto);

	void insertDiscLoadDetails(PolicyDiscountLoadDTO paramPolicyDiscountLoadDTO);

	void insertCoverDetails(PolicyCoverDto paramPolicyCoverDto);

	void insertNomineeDetails(NomineeDto paramNomineeDto);

	void insertFamilyDetails(FamilyDetailsDto paramFamilyDetailsDto);

	void insertPremiumDetails(PolicyPremiumDto paramPolicyPremiumDto);

	void insertPolicyDetails(PolicyDto paramPolicyDto);
}