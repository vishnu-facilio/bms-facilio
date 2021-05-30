package com.facilio.bundle.action;

import com.facilio.accounts.dto.Organization;
import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.Getter;

@Getter @Setter @Log4j
public class BundleAction extends V3Action{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Organization organization;
	
	public String copyCustomiation() throws Exception {
		
		LOGGER.info("organization : "+organization);
		
		FacilioChain copyCustomizationChain = BundleTransactionChainFactory.getCopyCustomizationChain();
		
		FacilioContext context = copyCustomizationChain.getContext();
		
		context.put(BundleConstants.DESTINATION_ORG, organization);
		
		copyCustomizationChain.execute();
		
		return null;
		
	}

}
