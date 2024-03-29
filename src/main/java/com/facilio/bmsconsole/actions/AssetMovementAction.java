package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetMovementContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class AssetMovementAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private AssetMovementContext assetMovement;
	
	public AssetMovementContext getAssetMovement() {
		return assetMovement;
	}

	public void setAssetMovement(AssetMovementContext assetMovement) {
		this.assetMovement = assetMovement;
	}

	private Map<String, List<WorkflowRuleContext>> stateFlows;
	public Map<String, List<WorkflowRuleContext>> getStateFlows() {
		return stateFlows;
	}
	public void setStateFlows(Map<String, List<WorkflowRuleContext>> stateFlows) {
		this.stateFlows = stateFlows;
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
	
	private Long stateTransitionId;
	public Long getStateTransitionId() {
		return stateTransitionId;
	}
	public void setStateTransitionId(Long stateTransitionId) {
		this.stateTransitionId = stateTransitionId;
	}

	public String addAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		if(assetMovement != null) {
			if((!StringUtils.isNotEmpty(assetMovement.getFromGeoLocation()) && AssetsAPI.checkIfAssetMovementNecessary(assetMovement.getToGeoLocation(), assetMovement.getFromGeoLocation(), assetMovement.getAssetId())) || StringUtils.isEmpty(assetMovement.getFromGeoLocation())) {
				AssetContext asset = AssetsAPI.getAssetInfo(assetMovement.getAssetId());
				context.put(FacilioConstants.ContextNames.ASSET, asset);
				FacilioChain chain = TransactionChainFactory.getAddAssetMovementChain();
				chain.execute(context);
				setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
			}
		}
		return SUCCESS;
	}
	
	public String updateAssetMovement() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, assetMovement);
		context.put(FacilioConstants.ContextNames.TRANSITION_ID, getStateTransitionId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,Collections.singletonList(assetMovement.getId()));
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		
		FacilioChain chain = TransactionChainFactory.getUpdateAssetMovementChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, context.get(FacilioConstants.ContextNames.RECORD));
		return SUCCESS;
	}
	
	public String listAssetMovement() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "assetmovement");
		context.put(FacilioConstants.ContextNames.SORTING_QUERY, "Asset_Movement.ID desc");
 		
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
 	 	
		FacilioChain chain = ReadOnlyChainFactory.getAssetMovementListChain();
		chain.execute(context);
		
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,context.get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<AssetMovementContext> assetMovementRecords = (List<AssetMovementContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.ASSET_MOVEMENT_RECORDS, assetMovementRecords);
			setStateFlows((Map<String, List<WorkflowRuleContext>>) context.get("stateFlows"));
			setResult("stateFlows", getStateFlows());
			
		}
		return SUCCESS;
	}
	
}
