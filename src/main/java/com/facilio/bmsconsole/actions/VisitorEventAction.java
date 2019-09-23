package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorEventContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class VisitorEventAction extends FacilioAction{
	
	private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	private VisitorEventContext visitorEvent;
	
	public VisitorEventContext getVisitorEvent() {
		return visitorEvent;
	}
	public void setVisitorEvent(VisitorEventContext visitorEvent) {
		this.visitorEvent = visitorEvent;
	}

	private List<VisitorEventContext> visitorEvents;
	
	public List<VisitorEventContext> getVisitorEvents() {
		return visitorEvents;
	}
	public void setVisitorEvents(List<VisitorEventContext> visitorEvents) {
		this.visitorEvents = visitorEvents;
	}

	private List<Long> visitorEventIds;
	
	public List<Long> getVisitorEventIds() {
		return visitorEventIds;
	}
	public void setVisitorEventIds(List<Long> visitorEventIds) {
		this.visitorEventIds = visitorEventIds;
	}

	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private List<VisitorContext> eventInvitees;
	
	public List<VisitorContext> getEventInvitees() {
		return eventInvitees;
	}
	public void setEventInvitees(List<VisitorContext> eventInvitees) {
		this.eventInvitees = eventInvitees;
	}

	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long eventId;
	
	public long getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	private List<Long> eventInviteeIds;
	
	public List<Long> getEventInviteeIds() {
		return eventInviteeIds;
	}
	public void setEventInviteeIds(List<Long> eventInviteeIds) {
		this.eventInviteeIds = eventInviteeIds;
	}
	public String addVisitorEvents() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorEvents)) {
			FacilioChain c = TransactionChainFactory.addVisitorEventsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorEvents);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_EVENTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateVisitorEvents() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorEvents)) {
			FacilioChain c = TransactionChainFactory.updateVisitorEventsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorEvents);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_EVENTS, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteVisitorEvents() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorEventIds)) {
			FacilioChain c = FacilioChainFactory.deleteVisitorEventsChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, visitorEventIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getVisitorEventsList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorEventsListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorevent");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Events.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitorevent.name");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
		List<VisitorEventContext> visitorEvents = (List<VisitorEventContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITOR_EVENTS, visitorEvents);
		}
		
		return SUCCESS;
	}
	
	public String getVisitorEventDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getVisitorEventsDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		VisitorEventContext visitorEvent = (VisitorEventContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.VISITOR_EVENT, visitorEvent);
		
		return SUCCESS;
	}
	
	public String addEventInvitees() throws Exception {
		
		if(!CollectionUtils.isEmpty(eventInvitees)) {
			FacilioChain c = TransactionChainFactory.addEventInviteesChain();
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_EVENT_INVITEES, eventInvitees);
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_EVENT_ID, eventId);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_EVENT_REL, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String deleteEventInvitees() throws Exception {
		
		if(!CollectionUtils.isEmpty(eventInviteeIds)) {
			FacilioChain c = FacilioChainFactory.deleteEventVisitorRelChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, eventInviteeIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}

}
