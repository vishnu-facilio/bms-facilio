package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.context.ShiftRotationDetailsContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ShiftRotationAction extends ModuleAction {

	private static final long serialVersionUID = 1L;
	private ShiftRotationContext shiftRotation;

	public ShiftRotationContext getShiftRotation() {
		return shiftRotation;
	}

	public void setShiftRotation(ShiftRotationContext shiftRotation) {
		this.shiftRotation = shiftRotation;
	}

	private List<ShiftRotationDetailsContext> shiftRotationDetails;

	public List<ShiftRotationDetailsContext> getShiftRotationDetails() {
		return shiftRotationDetails;
	}

	public void setShiftRotationDetails(List<ShiftRotationDetailsContext> shiftRotationDetails) {
		this.shiftRotationDetails = shiftRotationDetails;
	}

	private List<Long> users;

	public List<Long> getUsers() {
		return users;
	}

	public void setUsers(List<Long> users) {
		this.users = users;
	}

	public String addShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shiftRotation);
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_APPLICABLE_FOR, shiftRotation.getApplicableFor());
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_DETAILS, shiftRotation.getShiftRotations());

		Chain c = TransactionChainFactory.getAddShiftRotationChain();
		c.execute(context);

		return SUCCESS;
	}
	
	public String updateShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shiftRotation);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(shiftRotation.getId()));
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_APPLICABLE_FOR, shiftRotation.getApplicableFor());
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_DETAILS, shiftRotation.getShiftRotations());

		Chain c = TransactionChainFactory.getUpdateShiftRotationChain();
		c.execute(context);

		return SUCCESS;
	}

	public String deleteShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		
		Chain chain = TransactionChainFactory.getDeleteShiftRotationChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
		return SUCCESS;
	}
	
	public String details() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getShiftRotationId());

		Chain chain = ReadOnlyChainFactory.getShiftRotationDetails();
		chain.execute(context);

		setShiftRotation((ShiftRotationContext) context.get(FacilioConstants.ContextNames.SHIFT_ROTATION));
		setResult(FacilioConstants.ContextNames.SHIFT_ROTATION, shiftRotation);
		return SUCCESS;
	}
	
	
	private long shiftRotationId;

	public long getShiftRotationId() {
		return shiftRotationId;
	}

	public void setShiftRotationId(long shiftRotationId) {
		this.shiftRotationId = shiftRotationId;
	}

	public String executeShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shiftRotation);

		Chain c = TransactionChainFactory.getExecuteShiftRotationCommand();
		c.execute(context);

		return SUCCESS;
	}

	public String shiftRotationList() throws Exception {

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Shift_Rotation.SCHEDULAR_NAME asc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "shiftRotation.schedularName");
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

		Chain c = ReadOnlyChainFactory.getShiftRotationList();
		c.execute(context);

		if (getCount()) {
			setShiftCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", shiftCount);
		} else {
			shiftRotationList = (List<ShiftRotationContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if (shiftRotationList == null) {
				shiftRotationList = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.SHIFT_ROTATION, shiftRotationList);
		}

		return SUCCESS;
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

	private Long shiftCount;

	public Long getShiftCount() {
		return shiftCount;
	}

	public void setShiftCount(Long shiftCount) {
		this.shiftCount = shiftCount;
	}
	
	private List<ShiftRotationContext> shiftRotationList;
	public List<ShiftRotationContext> getShiftRotationList() {
		return shiftRotationList;
	}
	public void setShiftRotationList(List<ShiftRotationContext> shiftRotationList) {
		this.shiftRotationList = shiftRotationList;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	private List<Long> recordIds;
	public List<Long> getRecordIds() {
		return recordIds;
	}
	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}
}
