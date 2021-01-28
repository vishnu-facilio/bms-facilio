package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateRange;

public class RunThroughSensorRuleCommand extends FacilioCommand{
	private static final Logger LOGGER = Logger.getLogger(RunThroughSensorRuleCommand.class.getName());
	
	@SuppressWarnings("null")
	@Override
	public boolean executeCommand(Context context) throws Exception {

		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		Long assetCategoryId = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
		List<Long> assetIds = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_ID);

		if(range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("Invalid daterange to run sensor rule.");
		}
		
		if(assetCategoryId == null) {
			throw new IllegalArgumentException("Insufficient params for assetCategoryId to execute sensor rule history");
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(FacilioConstants.ContextNames.DATE_RANGE, range);
		jsonObject.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategoryId);
		jsonObject.put(FacilioConstants.ContextNames.ASSET_ID, assetIds);
		
		BmsJobUtil.deleteJobWithProps(assetCategoryId, "'ExecuteSensorRuleHistoryJob'");
		BmsJobUtil.scheduleOneTimeJobWithProps(assetCategoryId, "'ExecuteSensorRuleHistoryJob'", 30, "history", jsonObject);

		return false;
	}
	
}
