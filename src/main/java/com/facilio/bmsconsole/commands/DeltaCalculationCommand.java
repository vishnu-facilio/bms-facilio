package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
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
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}

		Map<String, ReadingDataMeta> metaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.READING_DATA_META);
		if(metaMap==null || metaMap.isEmpty()) {
			return false;
		}
		System.err.println( Thread.currentThread().getName()+"Inside DeltaCommand####### lastReadingMap "+metaMap);

		Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			List<MarkedReadingContext> markedList=new ArrayList<MarkedReadingContext> ();
			for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
				String moduleName = entry.getKey();
				List<ReadingContext> readings = entry.getValue();
				if(moduleName==null || !moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)){
					continue;
				}
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> allFields= bean.getAllFields(moduleName);
				long moduleId=bean.getModule(moduleName).getModuleId();
				Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(allFields);
				for(ReadingContext reading:readings) {
					setDelta(fieldMap,"totalEnergyConsumption",moduleId, reading,metaMap,markedList);
					setDelta(fieldMap,"phaseEnergyR",moduleId,reading,metaMap,markedList);
					setDelta(fieldMap,"phaseEnergyY",moduleId,reading,metaMap,markedList);
					setDelta(fieldMap,"phaseEnergyB",moduleId,reading,metaMap,markedList);
				}
			}
			context.put(FacilioConstants.ContextNames.MARKED_READINGS, markedList);
			System.err.println( Thread.currentThread().getName()+"Inside DeltaCommand#######  "+markedList);

		}
		System.err.println( Thread.currentThread().getName()+"Exiting DeltaCommand#######  ");

		return false;
	}


	private void setDelta(Map<String,FacilioField>  fieldMap, String fieldName,long moduleId, ReadingContext reading,Map<String, 
			ReadingDataMeta> metaMap,List<MarkedReadingContext> markedList ) {
			
			FacilioField readingField=fieldMap.get(fieldName);
			FieldType dataType=readingField.getDataTypeEnum();
			Object readingVal=reading.getReading(fieldName);
			Object deltaVal=reading.getReading(fieldName+"Delta");
			FacilioField deltaField = fieldMap.get(fieldName+"Delta");
			if( deltaVal!=null) {// delta already set in reading
				return;
			}
			
			long currentTimestamp=reading.getTtime();
			long resourceId=reading.getParentId();
			
			ReadingDataMeta consumptionMeta = metaMap.get(resourceId+"_"+readingField.getFieldId());
			if(consumptionMeta == null) {
				return;
			}
			
			Double lastReading = (Double) consumptionMeta.getValue(); 
			Long lastTimestamp = consumptionMeta.getTtime();
			if(lastReading == null || lastTimestamp == null) {
				return;
			}
			
			if(currentTimestamp < lastTimestamp)  {
				//timestamp check .. for ignoring historical data..
				return;
			}
			ReadingDataMeta deltaMeta = metaMap.get(resourceId+"_"+deltaField.getFieldId());
			Double lastDeltaReading = null;
			Long lastDeltaTimestamp = null;
			if(deltaMeta != null) {
				lastDeltaReading = (Double) deltaMeta.getValue();
				lastDeltaTimestamp = deltaMeta.getTtime();
			}
			
			double delta = 0;
			if(lastReading == -1 && readingVal != null) {
				//lastReading  check.. for very first reading 
				reading.addReading(fieldName+"Delta", delta);
				return;
			}
			double lastDelta = 0;
			if(lastDeltaReading != null) {
				lastDelta = lastDeltaReading;
				lastDelta = ReportsUtil.roundOff(lastDelta, 4);
			}
			long dataInterval = 15 * 60 * 1000;//this we should get from jace interval from org settings in future
			long rearmInterval = 1 * 60 * 1000;//this is an adjuster to consider little above than the given range..
			double leastMargin = 50;// this is the least value above which the delta rule can be considered..
			MarkType type = MarkType.DECREMENTAL_VALUE;
			
			Double currentReading = (Double) FieldUtil.castOrParseValueAsPerType(dataType, readingVal);
			if(currentReading == null) {
				currentReading = new Double(0);//if the reading is null.. setting the reading as zero, to set the delta properly..
			}
			
			if(currentReading>=lastReading) { // this check ensures incremental & same reading scenario
				delta=currentReading-lastReading;
				delta=ReportsUtil.roundOff(delta, 4);
				
				if(delta>=leastMargin && lastDelta>=leastMargin) { 
					//if current delta or lastDelta  is zero or smaller value no point in coming here..
					
					long timeDiff=currentTimestamp-lastDeltaTimestamp;
					if(timeDiff <=(dataInterval+rearmInterval)) {
						
						//if the time diff is less than or equals dataInterval+ adjuster range minutes we can check the below rule..
						if(delta >= 10*lastDelta) {
							//too high.. need to be marked & reading reset also done..
							// do we really need resetting here or just notify them??
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
					else {
						//this means missing reading scenario..i.e reading coming after long interval.. 

						if (delta >= 2 * lastDelta) {
							if(timeDiff <= 86400000) { 
								//missing records for 24 hrs or less..
								//then this reading spike need not be considered for per day graphs i.e hourly plots for a day..
								type=MarkType.HIGH_VALUE_HOURLY_VIOLATION;
							}
							else {
								//missing records for more than 24 hrs .. 
								//then this reading spike need not be considered for monthly graphs i.e  day plots for a month..
								type=MarkType.HIGH_VALUE_DAILY_VIOLATION;
							}
							markedList.add(getMarkedReading(reading,readingField.getFieldId(),moduleId,type, currentReading,currentReading));
						}
						
					}
				}
			}
			else {//here current reading equals zero or lesser than last reading scenario..

				reading.addReading(fieldName, lastReading);
				if(currentReading == 0) {
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
