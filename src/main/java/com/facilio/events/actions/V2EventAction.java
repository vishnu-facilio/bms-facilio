package com.facilio.events.actions;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.events.constants.EventConstants;

public class V2EventAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	
	private JSONArray payload;
	public JSONArray getPayload() {
		return payload;
	}
	public void setPayload(JSONArray payload) {
		this.payload = payload;
	}
	
	public String addEvent() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, payload);
		
		Chain chain = TransactionChainFactory.getV2AddEventPayloadChain();
		chain.execute(context);
		return SUCCESS;
	}
}
