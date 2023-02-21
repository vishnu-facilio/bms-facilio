package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class PeopleAction extends FacilioAction{

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

	private Boolean verifyStatus;
	public Boolean getVerifyStatus() {
		if (verifyStatus == null) {
			return true;
		}
		return verifyStatus;
	}
	public void setVerifyStatus(Boolean verifyStatus) {
		this.verifyStatus = verifyStatus;
	}

	private PeopleContext people;
	private List<PeopleContext> peopleList;
	
	private List<Long> peopleIds;
	
	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
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
	
	public PeopleContext getPeople() {
		return people;
	}
	public void setPeople(PeopleContext people) {
		this.people = people;
	}
	public List<PeopleContext> getPeopleList() {
		return peopleList;
	}
	public void setPeopleList(List<PeopleContext> peopleList) {
		this.peopleList = peopleList;
	}
	public List<Long> getPeopleIds() {
		return peopleIds;
	}
	public void setPeopleIds(List<Long> peopleIds) {
		this.peopleIds = peopleIds;
	}

	private String genericSearch;
	public String getGenericSearch() {
		return genericSearch;
	}
	public void setGenericSearch(String genericSearch) {
		this.genericSearch = genericSearch;
	}

	public String addPeople() throws Exception {
		
		if(!CollectionUtils.isEmpty(peopleList)) {
			addPeople(!getVerifyStatus());
		}
		return SUCCESS;
	}

	public void addPeople(boolean verifyStatus) throws Exception {
		FacilioChain c = TransactionChainFactory.addPeopleChain();
		c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, verifyStatus);
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		for(PeopleContext ppl : peopleList) {
			ppl.parseFormData();
			RecordAPI.handleCustomLookup(ppl.getData(), FacilioConstants.ContextNames.PEOPLE);
		}
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, peopleList);
		c.execute();
		setResult(FacilioConstants.ContextNames.PEOPLE, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
	}

	public String updatePeople() throws Exception {

		if(!CollectionUtils.isEmpty(peopleList)) {
			updatePeople(!getVerifyStatus());
		}
		return SUCCESS;
	}
	
	public String updatePeople(boolean verifyStatus) throws Exception {
		
		if(!CollectionUtils.isEmpty(peopleList)) {
			FacilioChain c = TransactionChainFactory.updatePeopleChain();
			c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, verifyStatus);
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			for(PeopleContext ppl : peopleList) {
				ppl.parseFormData();
				RecordAPI.handleCustomLookup(ppl.getData(), FacilioConstants.ContextNames.PEOPLE);
			}
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, peopleList);
			c.execute();
			setResult(FacilioConstants.ContextNames.PEOPLE, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deletePeople() throws Exception {
		
		if(!CollectionUtils.isEmpty(peopleIds)) {
			FacilioChain c = FacilioChainFactory.deletePeopleChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, peopleIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String peopleList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getPeopleListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "people");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "People.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "people.name");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}

		if(getGenericSearch() != null){
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "people.name,people.email");
			searchObj.put("query", getGenericSearch());
			chain.getContext().put(FacilioConstants.ContextNames.GENERIC_SEARCH, searchObj);
		}

 		if(!getFetchCount()) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			chain.getContext().put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
 	 	
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<PeopleContext> people = (List<PeopleContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.PEOPLE, people);
		}
		
		return SUCCESS;
	}
	
	public String getPeopleDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getPeopleDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		PeopleContext people = (PeopleContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.PEOPLE, people);
		
		return SUCCESS;
	}
	
	public String updateOccupantPortalAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(peopleList)) {
			FacilioChain c = TransactionChainFactory.updatePeopleAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, peopleList);
			c.execute();
			setResult(FacilioConstants.ContextNames.PEOPLE, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}

	public String getUserForPeopleId() throws Exception {
		if(CollectionUtils.isNotEmpty(peopleIds)){
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition("PEOPLE_ID","peopleId",StringUtils.join(peopleIds,","), NumberOperators.EQUALS));
			List<User> users = AccountUtil.getUserBean().getUsers(criteria,true,false);
			if (CollectionUtils.isNotEmpty(users)) {
				Map<Long, User> userMap =  users.stream().collect(Collectors.toMap(User::getPeopleId, Function.identity()));
				setResult(FacilioConstants.ContextNames.USERS, userMap);
			}
		}
		return SUCCESS;
	}
}
