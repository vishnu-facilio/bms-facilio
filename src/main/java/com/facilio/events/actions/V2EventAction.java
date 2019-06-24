package com.facilio.events.actions;

import java.util.Collections;
import java.util.List;

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
	
	private List<ReadingEventContext> events;
	public List<ReadingEventContext> getEvents() {
		return events;
	}
	public void setEvents(List<ReadingEventContext> events) {
		this.events = events;
	}

	public String addEvent() throws Exception {
		FacilioContext context = new FacilioContext();
//		context.put(EventConstants.EventContextNames.EVENT_PAYLOAD, payload);
		context.put(EventConstants.EventContextNames.EVENT_LIST, events);
		
		Chain chain = TransactionChainFactory.getV2AddEventChain();
		chain.execute(context);
		return SUCCESS;
	}
}
