package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

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
	
	public String addVendor() throws Exception {
		FacilioContext context = new FacilioContext();
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
		LocationContext location = vendor.getAddress();
		
		
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
		
		vendor.setModifiedTime(System.currentTimeMillis());
		FacilioContext context1 = new FacilioContext();
		context1.put(FacilioConstants.ContextNames.RECORD, vendor);
		context1.put(FacilioConstants.ContextNames.ID, vendor.getId());
		context1.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		
		context1.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(vendor.getId()));
		context1.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

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
		}
		return SUCCESS;
	}
	
	public String vendorsCount() throws Exception {
		vendorList();
		setResult(FacilioConstants.ContextNames.VENDORS_COUNT, vendorsCount);
		return SUCCESS;
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
