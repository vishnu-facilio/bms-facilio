package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class CopyAssetReadingsData extends FacilioAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long sourceAssetId = -1;
	private long targetAssetId = -1;
	private long sourceOrgId = -1;
	private long targetOrgId = -1;
	private long startTime = -1;
	private long endTime = -1;
	private String moduleList;

	public String copyAssetReadings() throws Exception {
		
		JSONArray jsonArr = new JSONArray(moduleList);

		FacilioChain context = TransactionChainFactory.copySpecificAssetReadingToAnotherOrgChain();
		context.getContext().put(FacilioConstants.ContextNames.COPY_SOURCE_ASSET_ID, getSourceAssetId());
		context.getContext().put(FacilioConstants.ContextNames.COPY_TARGET_ASSET_ID, getTargetAssetId());
		context.getContext().put(FacilioConstants.ContextNames.COPY_SOURCE_ORG_ID, getSourceOrgId());
		context.getContext().put(FacilioConstants.ContextNames.COPY_TARGET_ORG_ID, getTargetOrgId());
		context.getContext().put(FacilioConstants.ContextNames.COPY_START_TIME, getStartTime());
		context.getContext().put(FacilioConstants.ContextNames.COPY_END_TIME, getEndTime());		
		context.getContext().put(FacilioConstants.ContextNames.COPY_MODULE_LIST, toList(jsonArr));
		context.execute();
		
		return SUCCESS;
	}


	public long getSourceAssetId() {
		return sourceAssetId;
	}
	public void setSourceAssetId(long sourceAssetId) {
		this.sourceAssetId = sourceAssetId;
	}
	public long getSourceOrgId() {
		return sourceOrgId;
	}
	public void setSourceOrgId(long sourceOrgId) {
		this.sourceOrgId = sourceOrgId;
	}
	public long getTargetOrgId() {
		return targetOrgId;
	}
	public void setTargetOrgId(long targetOrgId) {
		this.targetOrgId = targetOrgId;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getModuleList() {
		return moduleList;
	}
	public void setModuleList(String moduleList) {
		this.moduleList = moduleList;
	}
	public long getTargetAssetId() {
		return targetAssetId;
	}
	public void setTargetAssetId(long targetAssetId) {
		this.targetAssetId = targetAssetId;
	}

	private List<String> toList(JSONArray array) throws JSONException {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			String value = (String) array.get(i);
			list.add(value);
		}
		return list;
	}

}
