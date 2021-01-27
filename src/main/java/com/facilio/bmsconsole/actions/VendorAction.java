package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.google.gson.JsonObject;

public class VendorAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	private VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	private long vendorId;
	public long getVendorId() {
		return vendorId;
	}
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	
	
	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
	}
	
	private List<ContactsContext> vendorContacts;
	
	public List<ContactsContext> getVendorContacts() {
		return vendorContacts;
	}
	public void setVendorContacts(List<ContactsContext> vendorContacts) {
		this.vendorContacts = vendorContacts;
	}

	private List<VendorContext> vendors;
	public List<VendorContext> getVendors() {
		return vendors;
	}
	public void setVendors(List<VendorContext> vendors) {
		this.vendors = vendors;
	}
	
	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}
	
	private String vendorString;
	public String getVendorString() {
		return vendorString;
	}
	public void setVendorString(String vendorString) {
		this.vendorString = vendorString;
	}
	
	private Map<String, Object> subFormFiles;
	public Map<String, Object> getSubFormFiles() {
		return subFormFiles;
	}
	public void setSubFormFiles(Map<String, Object> subFormFiles) {
		this.subFormFiles = subFormFiles;
	}
	
	
	public String addVendor() throws Exception {
		FacilioContext context = new FacilioContext();
		if (StringUtils.isNotEmpty(vendorString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(vendorString);
			vendor = FieldUtil.getAsBeanFromJson(json, VendorContext.class);
			
			if (vendor != null && MapUtils.isNotEmpty(subFormFiles)) {
				vendor.addSubFormFiles(subFormFiles);
			}
		}
		
		vendor.parseFormData();
		LocationContext location = vendor.getAddress();
		
		if(location!=null)
		{	
			location.setName(vendor.getName()+"_location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			FacilioChain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		vendor.setTtime(System.currentTimeMillis());
		vendor.setModifiedTime(System.currentTimeMillis());
		vendor.setSysCreatedTime(System.currentTimeMillis());
		if(vendor.getRegisteredBy() != null && vendor.getRegisteredBy().getId() > 0) {
			vendor.setVendorSource(1);
		}
		FacilioContext context1 = new FacilioContext();
		context1.put(FacilioConstants.ContextNames.RECORD, vendor);
		context1.put(FacilioConstants.ContextNames.CONTACTS, vendorContacts);
		context1.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		
		
		FacilioChain addVendorChain = TransactionChainFactory.getAddVendorChain();
		addVendorChain.execute(context1);
		
		setResult(FacilioConstants.ContextNames.VENDOR, vendor);
		return SUCCESS;
	}
	
	
	public String updateVendor() throws Exception {
		FacilioContext context = new FacilioContext();
		if (StringUtils.isNotEmpty(vendorString)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(vendorString);
			vendor = FieldUtil.getAsBeanFromJson(json, VendorContext.class);
			
			if (vendor != null && MapUtils.isNotEmpty(subFormFiles)) {
				vendor.addSubFormFiles(subFormFiles);
			}
		}
		LocationContext location = vendor.getAddress();
		vendor.parseFormData();
		
		if(location != null && location.getLat() != -1 && location.getLng() != -1)
		{
			location.setName(vendor.getName()+"_Location");
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				context.put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(location.getId()));
				FacilioChain editLocation = FacilioChainFactory.updateLocationChain();
				editLocation.execute(context);
				vendor.setAddress(location);
			}
			else {
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			vendor.setAddress(null);
		}
		
		if(vendor.getRegisteredBy() != null && vendor.getRegisteredBy().getId() > 0) {
			vendor.setVendorSource(1);
		}
		vendor.setModifiedTime(System.currentTimeMillis());
		FacilioContext context1 = new FacilioContext();
		context1.put(FacilioConstants.ContextNames.RECORD, vendor);
		context1.put(FacilioConstants.ContextNames.ID, vendor.getId());
		context1.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context1.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
		
		context1.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(vendor.getId()));
		context1.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
		context1.put(FacilioConstants.ContextNames.CONTACTS, vendorContacts);
		

		FacilioChain updateVendorChain = TransactionChainFactory.getUpdateVendorChain();
		updateVendorChain.execute(context1);
		setVendorId(vendor.getId());
		vendorDetails();
		setResult(FacilioConstants.ContextNames.VENDOR, vendor);
		return SUCCESS;
	}
	
	public String vendorDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getVendorId());

		FacilioChain storeRoomDetailsChain = ReadOnlyChainFactory.fetchVendorDetails();
		storeRoomDetailsChain.execute(context);

		setVendor((VendorContext) context.get(FacilioConstants.ContextNames.VENDOR));
		setResult(FacilioConstants.ContextNames.VENDOR, vendor);
		return SUCCESS;
	}
	
	public String vendorList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Vendors.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "vendors.name");
			searchObj.put("query", getSearch());
			context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
		}
		if (getCount()) { // only count
			context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
		} else {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}

		FacilioChain vendorListChain = ReadOnlyChainFactory.getVendorsList();
		vendorListChain.execute(context);
		if (getCount()) {
			setVendorsCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", vendorsCount);
		} else {
			vendors = (List<VendorContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (vendors == null) {
				vendors = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.VENDORS, vendors);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) vendorListChain.getContext().get("stateFlows"));
			setResult("stateFlows", getStateFlows());
		}
		return SUCCESS;
	}
	
	public String vendorsCount() throws Exception {
		vendorList();
		setResult(FacilioConstants.ContextNames.VENDORS_COUNT, vendorsCount);
		return SUCCESS;
	}
	
	public String v2deleteVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		
		VendorContext vendor = new VendorContext();
		vendor.setDeleted(true);
		
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, vendor);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, vendorsId);
		
		FacilioChain deleteVendorChain = TransactionChainFactory.getDeleteVendorsChain();
		deleteVendorChain.execute(context);
		setResult("vendorsId", vendorsId);
		return SUCCESS;
	}
	
	private List<Long> vendorsId;

	public List<Long> getvendorsId() {
		return vendorsId;
	}

	public void setVendorsId(List<Long> vendorsId) {
		this.vendorsId = vendorsId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	private Boolean count;

	public Boolean getCount() {
		if (count == null) {
			return false;
		}
		return count;
	}

	public void setCount(Boolean count) {
		this.count = count;
	}
	
	private Long vendorsCount;
	public Long getVendorsCount() {
		return vendorsCount;
	}
	public void setVendorsCount(Long vendorsCount) {
		this.vendorsCount = vendorsCount;
	}
	
	
}
