package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;

public class AssociateAssetToContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		ContractsContext contractContext = ContractsAPI.getContractDetails(recordId);
		List<ContractAssociatedAssetsContext> assets = (List<ContractAssociatedAssetsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		if(contractContext.getStatus() == ContractsContext.Status.APPROVED.getValue()) {
			ContractsAPI.updateAssetsAssociated(recordId, assets);
		}
		else {
			throw new IllegalArgumentException("Assets can be associated to only approved contracts");
		}
	
		
		return false;
	}

}
