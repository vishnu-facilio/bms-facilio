package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class CopyAssetReadingsData extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long sourceOrgId = -1;
	private long targetOrgId = -1;
	private long startTime = -1;
	private long endTime = -1;
	private long timeDiff = -1;
	private String assetList;
	private long targetModuleIdList;
	private long sourceModuleIdList;
	public String copyAssetReadings() throws Exception {

		
		try {
			FacilioChain context = ReadOnlyChainFactory.copySpecificAssetReadingToAnotherOrgChain();
			context.getContext().put(FacilioConstants.ContextNames.COPY_SOURCE_ORG_ID, getSourceOrgId());
			context.getContext().put(FacilioConstants.ContextNames.COPY_TARGET_ORG_ID, getTargetOrgId());
			context.getContext().put(FacilioConstants.ContextNames.COPY_START_TIME, getStartTime());
			context.getContext().put(FacilioConstants.ContextNames.COPY_END_TIME, getEndTime());
			context.getContext().put(FacilioConstants.ContextNames.COPY_TIME_DIFF, getTimeDiff());
			context.getContext().put(FacilioConstants.ContextNames.COPY_ASSET_LIST, conversionToList(assetList));
			context.getContext().put("TARGET_MODULE_LIST", targetModuleIdList);
			context.getContext().put("SOURCE_MODULE_LIST", sourceModuleIdList);
			context.execute();
		}catch(Exception e) {
			throw e;
		}
		

		return SUCCESS;
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

	public String getAssetList() {
		return assetList;
	}

	public void setAssetList(String assetList) {
		this.assetList = assetList;
	}

	public long getTimeDiff() {
		return timeDiff;
	}

	public void setTimeDiff(long timeDiff) {
		this.timeDiff = timeDiff;
	}

	public long getSourceModuleIdList() {
		return sourceModuleIdList;
	}

	public void setSourceModuleIdList(long sourceModuleIdList) {
		this.sourceModuleIdList = sourceModuleIdList;
	}

	public void setTargetModuleIdList(long targetModuleIdList) {
		this.targetModuleIdList = targetModuleIdList;
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		Iterator<String> keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = object.get(key);

			map.put(key, value);
		}
		return map;
	}
	
	private List<Map<String,Object>> conversionToList(String assetList) throws JSONException{
		JSONArray assetArrayList = new JSONArray(assetList);
		JSONObject jsonObj = null;
		List<Map<String, Object>> assets = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < assetArrayList.length(); i++) {
			jsonObj = assetArrayList.getJSONObject(i);
			Map<String, Object> dataValue = toMap(jsonObj);
			assets.add(dataValue);
		}
		return assets ;
	}
}
