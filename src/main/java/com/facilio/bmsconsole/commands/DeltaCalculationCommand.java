package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.reports.ReportsUtil;
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

		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}
		
		Map<String, Map<String,Object>> lastReadingsMap = (Map<String, Map<String,Object>>) context.get(FacilioConstants.ContextNames.LAST_READINGS);
		if(lastReadingsMap==null || lastReadingsMap.isEmpty()) {
			return false;
		}

		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields= bean.getAllFields(moduleName);
		long moduleId=bean.getModule(moduleName).getModuleId();
		Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(allFields);
		List<MarkedReadingContext> markedList=new ArrayList<MarkedReadingContext> ();
		
		for(ReadingContext reading:readings) {
			setDelta(fieldMap,"totalEnergyConsumption",moduleId, reading,lastReadingsMap,markedList);
			setDelta(fieldMap,"phaseEnergyR",moduleId,reading,lastReadingsMap,markedList);
			setDelta(fieldMap,"phaseEnergyY",moduleId,reading,lastReadingsMap,markedList);
			setDelta(fieldMap,"phaseEnergyB",moduleId,reading,lastReadingsMap,markedList);
		}
		context.put(FacilioConstants.ContextNames.MARKED_READINGS, markedList);
		return false;
	}


	private void setDelta(Map<String,FacilioField>  fieldMap, String fieldName,long moduleId, ReadingContext reading,Map<String, 
			Map<String,Object>> lastReadingsMap,List<MarkedReadingContext> markedList ) {
			
			FacilioField readingField=fieldMap.get(fieldName);
			FieldType dataType=readingField.getDataTypeEnum();
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
			Map<String,Object> oldDeltaStats=lastReadingsMap.get(resourceId+"_"+fieldName+"Delta");
			String lastDeltaReadingVal=null;
			Long lastDeltaTimestamp=null;
			
			if(oldDeltaStats!=null) {
				lastDeltaReadingVal=(String)oldDeltaStats.get("value");
				lastDeltaTimestamp=(Long)oldStats.get("ttime");
			}
			
			double lastReading =(double)FieldUtil.castOrParseValueAsPerType(dataType, lastReadingVal);
			Double lastDeltaReading =(Double)FieldUtil.castOrParseValueAsPerType(dataType, lastDeltaReadingVal);
			double delta=0;
			if(lastReading==-1) {
				//lastReading  check.. for very first reading 
				reading.addReading(fieldName+"Delta", delta);
				return;
			}
			double lastDelta=0;
			if(lastDeltaReading!=null) {
				
				lastDelta=lastDeltaReading;
				lastDelta=ReportsUtil.roundOff(lastDelta, 4);
			}
			long dataInterval=15*60*1000;//this we should get from jace interval from org settings in future
			long rearmInterval=1*60*1000;//this is an adjuster to consider little above than the given range..
			double leastMargin=50;// this is the least value above which the delta rule can be considered..
			MarkType type= MarkType.DECREMENTAL_VALUE;
			
			double currentReading=(double) FieldUtil.castOrParseValueAsPerType(dataType, readingVal);
			
			if(currentReading>=lastReading) { // this check ensures incremental & same reading scenario
				delta=currentReading-lastReading;
				delta=ReportsUtil.roundOff(delta, 4);
				
				if(delta>=leastMargin && lastDelta>=leastMargin && ((currentTimestamp-lastDeltaTimestamp) <=(dataInterval+rearmInterval))) {
					//if current delta or lastDelta  is zero no point in coming here..
					//if the time interval is less than or equals dataInterval+ adjuster range minutes we can check the below rule..
					if(delta >= 5*lastDelta) {
						//too high.. need to be marked & reading reset also done..
						reading.addReading(fieldName, lastReading);
						delta=0;
						type=MarkType.TOO_HIGH_VALUE;
						markedList.add(getMarkedReading(reading,readingField.getFieldId(),moduleId, type, currentReading,lastReading));
					}
					else if (delta >= 2*lastDelta) {
						//bit high.. only marking done but reading reset not done..
						type=MarkType.HIGH_VALUE;
						markedList.add(getMarkedReading(reading,readingField.getFieldId(),moduleId,type, currentReading,currentReading));
					}
					
					//need to think of any other rules here..
				}
			}
			else {//here current reading equals zero or lesser than last reading scenario..

				reading.addReading(fieldName, lastReading);
				if(currentReading==0) {
					type=MarkType.ZERO_VALUE;
				}
				else if(currentReading<0) {
					type=MarkType.NEGATIVE_VALUE;
				}
				
				markedList.add(getMarkedReading(reading,readingField.getFieldId(),moduleId,type,currentReading,lastReading));
			}
			reading.addReading(fieldName+"Delta", delta);
	}
	
	
	private MarkedReadingContext getMarkedReading(ReadingContext reading,long fieldId,long moduleId, MarkType markType, Object currentReading, Object lastReading) {
		
		MarkedReadingContext mReading= new MarkedReadingContext();
		mReading.setReading(reading);
		mReading.setFieldId(fieldId);
		mReading.setModuleId(moduleId);
		mReading.setMarkType(markType);
		mReading.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		mReading.setActualValue(String.valueOf(currentReading));
		mReading.setModifiedValue(String.valueOf(lastReading));
		return mReading;
		
	}

}
