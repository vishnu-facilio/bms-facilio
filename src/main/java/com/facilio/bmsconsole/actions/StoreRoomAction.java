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
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class StoreRoomAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	StoreRoomContext storeRoom;
	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}
	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}
	
	public String addStoreRoom() throws Exception {
		FacilioContext context = new FacilioContext();
//		LocationContext location = storeRoom.getLocation();
//		if(location!=null)
//		{
//			context.put(FacilioConstants.ContextNames.RECORD, location);
//			Chain addLocation = FacilioChainFactory.addLocationChain();
//			addLocation.execute(context);
//			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
//			location.setId(locationId);
//		}
		storeRoom.setTtime(System.currentTimeMillis());
		storeRoom.setModifiedTime(System.currentTimeMillis());
		context.put(FacilioConstants.ContextNames.RECORD, storeRoom);
		Chain addStoreRoom = TransactionChainFactory.getAddStoreRoomChain();
		addStoreRoom.execute(context);
		
		setResult(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		return SUCCESS;
	}
	
	public String updateStoreRoom() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD, storeRoom);
		context.put(FacilioConstants.ContextNames.ID, storeRoom.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(storeRoom.getId()));
		context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

		Chain updatestoreroomChain = TransactionChainFactory.getUpdateStoreRoomChain();
		updatestoreroomChain.execute(context);
		setStoreRoomId(storeRoom.getId());
		StoreRoomDetails();
		setResult(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		return SUCCESS;
	}
	
	public String StoreRoomDetails() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getStoreRoomId());

		Chain storeRoomDetailsChain = ReadOnlyChainFactory.fetchStoreRoomDetails();
		storeRoomDetailsChain.execute(context);

		setStoreRoom((StoreRoomContext) context.get(FacilioConstants.ContextNames.STORE_ROOM));
		setResult(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		return SUCCESS;
	}

	
	public String storeRoomList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Store_room.ID desc");
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
			context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
		if (getSearch() != null) {
			JSONObject searchObj = new JSONObject();
			searchObj.put("fields", "store_room.name");
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

		Chain storeRoomList = ReadOnlyChainFactory.getStoreRoomList();
		storeRoomList.execute(context);
		if (getCount()) {
			setStoreRoomCount((Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
			setResult("count", storeRoomCount);
		} else {
			storerooms = (List<StoreRoomContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			// Temp...needs to handle in client
			if (storerooms == null) {
				storerooms = new ArrayList<>();
			}
			setResult(FacilioConstants.ContextNames.STORE_ROOMS, storerooms);
		}
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
	
	private Long storeRoomCount;
	public Long getStoreRoomCount() {
		if (storeRoomCount == null) {
			storeRoomCount = 0L;
		}
		return storeRoomCount;
	}
	public void setStoreRoomCount(Long storeRoomCount) {
		this.storeRoomCount = storeRoomCount;
	}
	
	private List<StoreRoomContext> storerooms;
	public List<StoreRoomContext> getStorerooms() {
		return storerooms;
	}
	public void setStorerooms(List<StoreRoomContext> storerooms) {
		this.storerooms = storerooms;
	}
	
	private long storeRoomId;
	public long getStoreRoomId() {
		return storeRoomId;
	}
	public void setStoreRoomId(long storeRoomId) {
		this.storeRoomId = storeRoomId;
	}
	
}
