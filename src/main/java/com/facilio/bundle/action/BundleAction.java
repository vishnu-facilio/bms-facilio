package com.facilio.bundle.action;

import org.json.simple.JSONObject;

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
	
	public String copyCustomization() throws Exception {
		
		FacilioChain copyCustomizationChain = BundleTransactionChainFactory.getCopyCustomizationChain();
		
		FacilioContext context = copyCustomizationChain.getContext();
		
		copyCustomizationChain.execute();
		
		setData(BundleConstants.DOWNLOAD_URL, context.get(BundleConstants.DOWNLOAD_URL));
		
		return SUCCESS;
		
	}

}
