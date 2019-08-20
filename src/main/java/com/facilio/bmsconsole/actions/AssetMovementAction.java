package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;

public class AssetMovementAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private AssetMovementContext assetMovement;
	
	public AssetMovementContext getAssetMovement() {
		return assetMovement;
	}

	public void setAssetMovement(AssetMovementContext assetMovement) {
		this.assetMovement = assetMovement;
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
	
	public String addAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain chain = TransactionChainFactory.getAddAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String approveAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.APPROVE_ASSET_MOVEMENT);
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(assetMovement.getId()));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain chain = TransactionChainFactory.getUpdateAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String rejectAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.REJECT_ASSET_MOVEMENT);
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(assetMovement.getId()));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain chain = TransactionChainFactory.getUpdateAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String completeAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.COMPLETE_ASSET_MOVEMENT);
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(assetMovement.getId()));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain chain = TransactionChainFactory.getUpdateAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String updateAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(assetMovement.getId()));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		Chain chain = TransactionChainFactory.getUpdateAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String listAssetMovement() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "assetmovement");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Asset_Movement.ID asc");
 		
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
	 		
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "assetmovement.id");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		JSONObject pagination = new JSONObject();
 	 	pagination.put("page", getPage());
 	 	pagination.put("perPage", getPerPage());
 	 	if (getPerPage() < 0) {
 	 		pagination.put("perPage", 5000);
 	 	}
 	 	
		Chain chain = ReadOnlyChainFactory.getAssetMovementListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,(Long) context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<AssetMovementContext> assetMovementRecords = (List<AssetMovementContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, assetMovementRecords);
		}
		return SUCCESS;
	}
	
}
