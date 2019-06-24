package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ContractAction extends FacilioAction{

	private long recordId;
	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}


	public String configureNotification() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, recordId );
		Chain chain = TransactionChainFactory.getActivePurchaseContractPrice();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.UNIT_PRICE, context.get(FacilioConstants.ContextNames.UNIT_PRICE));
		
		return SUCCESS;
	}
	
}
