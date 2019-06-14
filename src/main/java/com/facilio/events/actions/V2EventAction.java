package com.facilio.events.actions;

import java.util.Collections;

import org.apache.commons.chain.Chain;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingEventContext;
import com.facilio.chain.FacilioContext;
import com.facilio.events.constants.EventConstants;

public class V2EventAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	
	private JSONObject payload;
	public JSONObject getPayload() {
		return payload;
	}
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}
	
	private ReadingEventContext event;
	public ReadingEventContext getEvent() {
		return event;
	}
	public void setEvent(ReadingEventContext event) {
		this.event = event;
	}

	public String addEvent() throws Exception {
		FacilioContext context = new FacilioContext();
//		context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, payload);
		context.put(EventConstants.EventContextNames.EVENT_LIST, Collections.singletonList(event));
		
		Chain chain = TransactionChainFactory.getV2AddEventChain();
		chain.execute(context);
		return SUCCESS;
	}
}
