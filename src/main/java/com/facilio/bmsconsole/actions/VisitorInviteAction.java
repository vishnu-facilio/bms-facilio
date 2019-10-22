package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class VisitorInviteAction extends FacilioAction{
	
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

	private VisitorInviteContext visitorInvite;
	private List<VisitorInviteContext> visitorInvites;
	private List<Long> visitorInviteIds;
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private List<VisitorContext> invitees;
	
	public List<VisitorContext> getInvitees() {
		return invitees;
	}
	public void setInvitees(List<VisitorContext> invitees) {
		this.invitees = invitees;
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

	private long visitorId;
	
	public long getVisitorId() {
		return visitorId;
	}
	public void setVisitorId(long visitorId) {
		this.visitorId = visitorId;
	}

	private long inviteId;
	
	private List<Long> inviteeIds;
	
	public VisitorInviteContext getVisitorInvite() {
		return visitorInvite;
	}
	public void setVisitorInvite(VisitorInviteContext visitorInvite) {
		this.visitorInvite = visitorInvite;
	}
	public List<VisitorInviteContext> getVisitorInvites() {
		return visitorInvites;
	}
	public void setVisitorInvites(List<VisitorInviteContext> visitorInvites) {
		this.visitorInvites = visitorInvites;
	}
	public List<Long> getVisitorInviteIds() {
		return visitorInviteIds;
	}
	public void setVisitorInviteIds(List<Long> visitorInviteIds) {
		this.visitorInviteIds = visitorInviteIds;
	}
	public long getInviteId() {
		return inviteId;
	}
	public void setInviteId(long inviteId) {
		this.inviteId = inviteId;
	}
	public List<Long> getInviteeIds() {
		return inviteeIds;
	}
	public void setInviteeIds(List<Long> inviteeIds) {
		this.inviteeIds = inviteeIds;
	}
	
	public String addVisitorInvites() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorInvites)) {
			FacilioChain c = TransactionChainFactory.addVisitorInvitesChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorInvites);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_INVITES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	public String updateVisitorInvites() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorInvites)) {
			FacilioChain c = TransactionChainFactory.updateVisitorInvitesChain();
			c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, visitorInvites);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_INVITES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteVisitorInvites() throws Exception {
		
		if(!CollectionUtils.isEmpty(visitorInviteIds)) {
			FacilioChain c = FacilioChainFactory.deleteVisitorInvitesChain();
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, visitorInviteIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String getVisitorInvitesList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getVisitorInvitesListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "visitorinvite");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Visitor_Invites.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "visitorinvite.name");
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
		List<VisitorInviteContext> visitorEvents = (List<VisitorInviteContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.VISITOR_INVITES, visitorEvents);
		}
		
		return SUCCESS;
	}
	
	public String getVisitorInviteDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getVisitorInvitesDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		VisitorInviteContext visitorEvent = (VisitorInviteContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.VISITOR_INVITE, visitorEvent);
		
		return SUCCESS;
	}
	
	public String addInvitees() throws Exception {
		
		if(!CollectionUtils.isEmpty(invitees)) {
			FacilioChain c = TransactionChainFactory.addInviteesChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.INVITEES, invitees);
			c.getContext().put(FacilioConstants.ContextNames.VISITOR_INVITE_ID, inviteId);
			c.execute();
			setResult(FacilioConstants.ContextNames.VISITOR_INVITE_REL, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String deleteInvitees() throws Exception {
		
		if(!CollectionUtils.isEmpty(inviteeIds)) {
			FacilioChain c = FacilioChainFactory.deleteInviteVisitorRelChain();
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, inviteeIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	public String preRegisterVisitors() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.preRegisterVisitorsChain();
		chain.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		chain.getContext().put(FacilioConstants.ContextNames.RECORD, visitorInvite);
		chain.getContext().put(FacilioConstants.ContextNames.INVITEES, invitees);
		
		chain.execute();
		setResult(FacilioConstants.ContextNames.RECORD, visitorInvite);
		
		return SUCCESS;
	}

	public String qrScanVisitor() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.qrScanVisitorChain();
		chain.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		chain.getContext().put(FacilioConstants.ContextNames.VISITOR_INVITE_ID, inviteId);
		chain.getContext().put(FacilioConstants.ContextNames.VISITOR_ID, visitorId);
		chain.execute();
		setResult(FacilioConstants.ContextNames.VISITOR_INVITE_REL, chain.getContext().get(FacilioConstants.ContextNames.VISITOR_INVITE_REL));
		
		return SUCCESS;
		
	}

}
