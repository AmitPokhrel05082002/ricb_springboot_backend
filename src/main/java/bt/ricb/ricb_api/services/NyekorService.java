package bt.ricb.ricb_api.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bt.ricb.ricb_api.models.Nyekor;
import bt.ricb.ricb_api.repository.NyekorRepository;

@Service
public class NyekorService {
	
	@Autowired
	private NyekorRepository policyRepository;

    @Transactional
    public Long createPolicy(Nyekor policyRequest) throws Exception {
        return policyRepository.createPolicy(policyRequest);
    }
}
