package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.sensor.SensorRollUpEventContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class ExecuteSensorRuleHistoryCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ExecuteSensorRuleHistoryCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try {
			DateRange dateRange = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
			Long assetCategoryId = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY);
			List<Long> assetIds = (List<Long>) context.get(FacilioConstants.ContextNames.ASSET_ID);

			if(assetCategoryId == null || dateRange == null) {
				throw new IllegalArgumentException("Insufficient params to execute sensor rule history");
			}
			if(assetIds == null || assetIds.isEmpty()) {
				List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryId);
				assetIds = assets.stream().map(asset -> asset.getId()).collect(Collectors.toList());
			}
			
			List<SensorRuleContext> sensorRules = SensorRuleUtil.getSensorRuleByCategoryId(assetCategoryId, true);
			List<ReadingContext> readings = new ArrayList<ReadingContext>();
			List<SensorRollUpEventContext> sensorMeterRollUpEvents = new ArrayList<SensorRollUpEventContext>();

			if(sensorRules != null && sensorRules.isEmpty()) {	
				Set<FacilioField> sensorRuleFields = sensorRules.stream().map(sensorRule -> sensorRule.getReadingField()).collect(Collectors.toSet());
				for(FacilioField sensorRuleField:sensorRuleFields) 
				{
					List<ReadingContext> fieldReadings = SensorRuleUtil.fetchReadingsForSensorRuleField(sensorRuleField, assetIds, dateRange.getStartTime(), dateRange.getEndTime());
					if(fieldReadings != null && !fieldReadings.isEmpty()) {
						readings.addAll(fieldReadings);
					}	
				}
				SensorRuleUtil.executeSensorRules(sensorRules,readings, true, sensorMeterRollUpEvents);
			}	
				
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE, "Error in ExecuteSensorRuleHistoryCommand -- "+
					" Exception: " + e.getMessage() , e);
		}
		
		return false;
	}

}
