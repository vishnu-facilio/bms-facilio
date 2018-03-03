package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class DeltaCalculationCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
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
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(moduleName==null || !moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)){
			return false;
		}

		Map<String, Map<String,Object>> lastReadingsMap = (Map<String, Map<String,Object>>) context.get(FacilioConstants.ContextNames.LAST_READINGS);
		if(lastReadingsMap==null || lastReadingsMap.isEmpty()) {
			return false;
		}

		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields= bean.getAllFields(moduleName);
		Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(allFields);
		
		for(ReadingContext reading:readings) {
			setDelta(fieldMap.get("totalEnergyConsumption"), reading,lastReadingsMap);
			setDelta(fieldMap.get("phaseEnergyR"),reading,lastReadingsMap);
			setDelta(fieldMap.get("phaseEnergyY"),reading,lastReadingsMap);
			setDelta(fieldMap.get("phaseEnergyB"),reading,lastReadingsMap);
		}

		return false;
	}


	private void setDelta( FacilioField field,ReadingContext reading,Map<String, Map<String,Object>> lastReadingsMap ) {
			
			String fieldName= field.getName();
			FieldType type=field.getDataTypeEnum();
			Object readingVal=reading.getReading(fieldName);
			Object deltaVal=reading.getReading(fieldName+"Delta");
			if(readingVal==null || deltaVal!=null) {//no such reading meters or delta already set in reading
				return;
			}
			
			long currentTimestamp=reading.getTtime();
			long resourceId=reading.getParentId();
			
			Map<String,Object> oldStats=lastReadingsMap.get(resourceId+"_"+fieldName);
			if(oldStats==null) {
				return;
			}
			
			String lastReadingVal=(String)oldStats.get("value");
			Long lastTimestamp=(Long)oldStats.get("ttime");
			if(lastReadingVal==null || lastTimestamp==null) {
				
				return;
			}
			
			if(currentTimestamp<lastTimestamp)  {
				//timestamp check .. for ignoring historical data..
				return;
			}
			double lastReading =(double)FieldUtil.castOrParseValueAsPerType(type, lastReadingVal);
			double delta=0;
			if(lastReading==-1) {
				//lastReading  check.. for very first reading 
				reading.addReading(fieldName+"Delta", delta);
				return;
			}
			
			double currentReading=(double) FieldUtil.castOrParseValueAsPerType(type, readingVal);
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
