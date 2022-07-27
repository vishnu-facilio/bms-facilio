package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class MigratingDemoAssetReadings extends FacilioAction{
	private static final Logger LOGGER = LogManager.getLogger(MigratingDemoAssetReadings.class.getName());
	private static final long serialVersionUID = 1L;

	private long categoryId=-1;
	private long targetFieldId = -1;
	private String sourceFieldIdList;

	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public long getTargetFieldId() {
		return targetFieldId;
	}
	public void setTargetFieldId(long targetFieldId) {
		this.targetFieldId = targetFieldId;
	}
	
	public String getSourceFieldIdList() {
		return sourceFieldIdList;
	}
	public void setSourceFieldIdList(String sourceFieldIdList) {
		this.sourceFieldIdList = sourceFieldIdList;
	}
	public String removeDuplicateReading() throws Exception {
		List<AssetContext>  assetList=AssetsAPI.getAssetListOfCategory(getCategoryId());
		JSONArray jsonArr = new JSONArray(sourceFieldIdList);
		List<Long> fieldList = new ArrayList<Long>();
		if (jsonArr != null) { 
		   for (int i=0;i<jsonArr.length();i++){ 
			   fieldList.add(Long.parseLong(jsonArr.getString(i)));
		   } 
		} 
		for(AssetContext asset:assetList) {
			long targetAssetId = asset.getId();
			LOGGER.info("Target asset Id : "+targetAssetId + " total assets count :"+assetList.size());
			LOGGER.info("TargetFieldId is :" +targetFieldId);
			for(long field:fieldList) {
				Map<String,Object> oldRecord = new HashMap<String, Object>();
				oldRecord.put(FacilioConstants.ContextNames.FIELD_ID,field);
				oldRecord.put("resourceId", targetAssetId);
				FacilioChain chain = TransactionChainFactory.getMigrateReadingDataChain();
				chain.getContext().put(FacilioConstants.ContextNames.PARENT_ID, targetAssetId);
				chain.getContext().put(FacilioConstants.ContextNames.FIELD_ID, getTargetFieldId());
				chain.getContext().put(FacilioConstants.ContextNames.RECORD, oldRecord);
				chain.execute();
				LOGGER.info(" merged field value fieldid :" + field);
			}
		}
		
		return SUCCESS;
	}
	
	public static List<Long> toList(JSONArray array) throws JSONException {
		List<Long> list = new ArrayList<Long>();
		for (int i = 0; i < array.length(); i++) {
			long value = (long) array.get(i);

			list.add(value);
		}
		return list;
	}

}
