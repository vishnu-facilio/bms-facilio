package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SmartControlKioskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

public class SmartControlKioskAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	private SmartControlKioskContext smartControlKiosk;




	
	public SmartControlKioskContext getSmartControlKiosk() {
		return smartControlKiosk;
	}
	public void setSmartControlKiosk(SmartControlKioskContext smartControlKiosk) {
		this.smartControlKiosk = smartControlKiosk;
	}
	public String getSmartControlKioskList() throws Exception{
		
		FacilioChain chain=ReadOnlyChainFactory.getSmartControlKioskListChain();
		FacilioContext context=chain.getContext();		
		chain.execute();
		
		setResult("smartControlKiosks",context.get(ContextNames.RECORD_LIST));
		return SUCCESS;
	}

	public String addOrUpdateSmartControlKiosk() throws Exception{
		
		FacilioChain chain=TransactionChainFactory.addOrUpdateSmartControlKioskChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD,getSmartControlKiosk());
		chain.execute();
		setResult("smartControlKiosk",context.get(ContextNames.RECORD));
		return SUCCESS;
	}

	
	
}