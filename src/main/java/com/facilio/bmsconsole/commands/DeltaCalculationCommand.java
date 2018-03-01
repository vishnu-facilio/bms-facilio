package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;

public class DeltaCalculationCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(!moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)){
			return false;
		}

		List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
		if(readings == null) {
			ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
			if(reading != null) {
				readings = Collections.singletonList(reading);
			}
		}
		if(readings == null || readings.isEmpty()) {
			return false;
		}

		Map<String, Map<String,Object>> lastReadingsMap = (Map<String, Map<String,Object>>) context.get(FacilioConstants.ContextNames.LAST_READINGS);
		if(lastReadingsMap==null || lastReadingsMap.isEmpty()) {
			return false;
		}

		for(ReadingContext reading:readings) {
			setDelta("totalEnergyConsumption", reading,lastReadingsMap);
			setDelta("phaseEnergyR",reading,lastReadingsMap);
			setDelta("phaseEnergyY",reading,lastReadingsMap);
			setDelta("phaseEnergyB",reading,lastReadingsMap);
		}

		return false;
	}


	private void setDelta( String fieldName,ReadingContext reading,Map<String, Map<String,Object>> lastReadingsMap ) {
			
			double currentReading=(double)reading.getReading(fieldName);
			long currentTimestamp=reading.getTtime();
			long resourceId=reading.getParentId();
			
			Map<String,Object> oldStats=lastReadingsMap.get(resourceId+"_"+fieldName);
			if(oldStats==null) {
				return;
			}
			
			double lastReading=(double)oldStats.get("value");
			double lastTimestamp=(double)oldStats.get("ttime");
			double delta=0;
			if(currentTimestamp<=lastTimestamp || lastReading==-1) {
				//timestamp check .. for ignoring historical data..
				//lastReading  check.. for very first reading & no such reading meters
				return;
			}
			

			if(currentReading>=lastReading) { // this check ensures incremental & same reading scenario
				delta=currentReading-lastReading;
				//need to think of any other rules here..
			}
			else {//here currentreading equals zero or lesser than last reading scenario..

				reading.addReading(fieldName, lastReading);
			}
			reading.addReading(fieldName+"Delta", delta);
	}
	

}
