package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.InsuranceContext;
import com.facilio.constants.FacilioConstants;

public class AddInsuranceVendorRollUpsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InsuranceContext> insurances = (List<InsuranceContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(insurances)) {
			
		}
		return false;
	}

}
