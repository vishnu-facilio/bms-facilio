package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.MarkingUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeltaCalculationCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(DeltaCalculationCommand.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading==true) {
			return false;
		}

		Map<String, ReadingDataMeta> metaMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
		if(metaMap==null || metaMap.isEmpty()) {
			return false;
		}
		LOGGER.debug("Inside DeltaCommand####### lastReadingMap "+metaMap);

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
				FacilioModule module=bean.getModule(moduleName);
				Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(allFields);
				List<Pair<Long, FacilioField>> deltaRdmPairs = new ArrayList<>();
				Boolean skipLastReadingCheck = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_LAST_READING_CHECK);
				if (skipLastReadingCheck == null) {
					skipLastReadingCheck = false;
				}
				for(ReadingContext reading:readings) {
					addDeltaRDM(fieldMap,"totalEnergyConsumption", reading,metaMap, deltaRdmPairs);
					addDeltaRDM(fieldMap,"phaseEnergyR",reading,metaMap, deltaRdmPairs);
					addDeltaRDM(fieldMap,"phaseEnergyY",reading,metaMap, deltaRdmPairs);
					addDeltaRDM(fieldMap,"phaseEnergyB",reading,metaMap, deltaRdmPairs);
				}
				if (!deltaRdmPairs.isEmpty()) {
					List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(deltaRdmPairs) ;
					
					for(ReadingDataMeta meta : metaList) {
						metaMap.put(ReadingsAPI.getRDMKey(meta.getResourceId(), meta.getField()), meta);
					}
					
					for(ReadingContext reading:readings) {
						setDelta(fieldMap,"totalEnergyConsumption",module, reading,metaMap,markedList, skipLastReadingCheck);
						setDelta(fieldMap,"phaseEnergyR",module,reading,metaMap,markedList, skipLastReadingCheck);
						setDelta(fieldMap,"phaseEnergyY",module,reading,metaMap,markedList, skipLastReadingCheck);
						setDelta(fieldMap,"phaseEnergyB",module,reading,metaMap,markedList, skipLastReadingCheck);
					}
				}
				
			}
			context.put(FacilioConstants.ContextNames.MARKED_READINGS, markedList);
			LOGGER.debug("Inside DeltaCommand#######  "+markedList);

		}
		LOGGER.debug("Exiting DeltaCommand#######  ");

		return false;
	}

	private  void addDeltaRDM(Map<String,FacilioField>  fieldMap,String fieldName,ReadingContext reading,Map<String, 
			ReadingDataMeta> metaMap,List<Pair<Long,FacilioField>> deltaRdmPairs) {
		
		FacilioField readingField=fieldMap.get(fieldName);
		Object readingVal=reading.getReading(fieldName);
		String deltaFieldName = fieldName+"Delta";
		FacilioField deltaField=fieldMap.get(deltaFieldName);
		Object deltaVal=reading.getReading(deltaFieldName);
		
		long resourceId=reading.getParentId();

		if( deltaVal!=null || readingVal==null) {// delta already set in reading or reading is null..
			return;
		}
		ReadingDataMeta consumptionMeta = metaMap.get(ReadingsAPI.getRDMKey(resourceId, readingField));
		if(consumptionMeta == null) {
			return;
		}
		Double lastReading = (Double) consumptionMeta.getValue(); 
		if(lastReading == null || lastReading<0) {
			return;
		}
		deltaRdmPairs.add(Pair.of(resourceId, deltaField));
	}
	
	private void setDelta(Map<String,FacilioField>  fieldMap, String fieldName,FacilioModule module, ReadingContext reading,Map<String, 
			ReadingDataMeta> metaMap,List<MarkedReadingContext> markedList, Boolean skipLastReadingCheck ) {
			
			FacilioField readingField=fieldMap.get(fieldName);
			FieldType dataType=readingField.getDataTypeEnum();
			Object readingVal=reading.getReading(fieldName);
			String deltaFieldName = fieldName+"Delta";
			FacilioField deltaField=fieldMap.get(deltaFieldName);
			Object deltaVal=reading.getReading(deltaFieldName);
			
			long resourceId=reading.getParentId();

			if( deltaVal!=null || readingVal==null) {// delta already set in reading or reading is null..
				return;
			}
						
			long energyFieldId= readingField.getFieldId();
			ReadingDataMeta consumptionMeta = metaMap.get(ReadingsAPI.getRDMKey(resourceId, readingField));
			if(consumptionMeta == null) {
				return;
			}
			
			Double lastReading = (Double) consumptionMeta.getValue(); 
			if(lastReading == null || lastReading <0) {
				return;
			}
			
			Double lastDelta=0.0;
			ReadingDataMeta deltaMeta = metaMap.get(ReadingsAPI.getRDMKey(resourceId, deltaField));
			if(deltaMeta!=null) {
				lastDelta = (Double) deltaMeta.getValue(); 
			} 
			else {
				LOGGER.info("Delta Meta null for resource "+resourceId+" for fieldId: "+deltaField);
			}
			
			long deltaFieldId=deltaField.getFieldId();
			
			
			Double currentReading = (Double) FieldUtil.castOrParseValueAsPerType(dataType, readingVal);
			
			long moduleId=module.getModuleId();
			MarkType type = MarkType.NEGATIVE_VALUE;
			if(currentReading<=0) {
				reading.addReading(fieldName, lastReading);
				if(currentReading == 0) {
					type=MarkType.ZERO_VALUE;
				}
				markedList.add(MarkingUtil.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
				currentReading=lastReading;
			}
			else if(currentReading<lastReading && !skipLastReadingCheck) {
							
					long ttime=getQueryTime(resourceId, readingField, module, INTERVAL_COUNT);
					List<Double> actualValues=MarkingUtil.getActualValues(resourceId, energyFieldId, ttime, type);
					int size=actualValues.size();
					
					 double lastVal= getLastValForMeterReset (currentReading, actualValues);
					if(lastVal==-1) {
						type=MarkType.DECREMENTAL_VALUE;//data correction done here	
						markedList.add(MarkingUtil.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
						markedList.add(MarkingUtil.getMarkedReading(reading,deltaFieldId,moduleId,type,currentReading,lastReading));
						reading.addReading(fieldName, lastReading);
						currentReading=lastReading;
						if(size>=INTERVAL_COUNT) {
							sendEmail(getDecrementalString(resourceId), getEmailBody(lastReading,currentReading,actualValues));
						}
					}
					else {
						type=MarkType.RESET_VALUE;//data reset done here, obviously data correction..
						markedList.add(MarkingUtil.getMarkedReading(reading,energyFieldId,moduleId,type,lastReading,currentReading));
						markedList.add(MarkingUtil.getMarkedReading(reading,deltaFieldId,moduleId,type,lastReading,currentReading));
						lastReading=lastVal;//inorder to get the complete delta without missing out the previous intervalCheck(4) intervals
						reading.setMarked(true);//this to avoid any spike in graph.. bcoz of above delta setting..
						//send error email for action..
						String subject="Reset Done! " +getDecrementalString(resourceId);
						sendEmail(subject, getEmailBody(lastReading,currentReading,actualValues));
					}
			}
			
			Double delta= currentReading-lastReading;
			
			long currentTime=(reading.getTtime()!=-1)? reading.getTtime():System.currentTimeMillis() ;
			long lastDataTime=consumptionMeta.getTtime();
			float dataGapCount= DeviceAPI.getDataGapCount(resourceId, readingField, module,currentTime, lastDataTime);
			
			if(delta>DELATA_HIGH_VAL_BAND && delta>lastDelta) {
				//Not sure how to handle, if there is an erratic meter reading.. 
				// for now assuming that the delta is too high than 10K
				double estimatedDelta= getEstimatedDelta(lastDelta, dataGapCount);
				if(delta > estimatedDelta*DELTA_MF ) {
					type=MarkType.TOO_HIGH_VALUE;//data correction done here	
					markedList.add(MarkingUtil.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
					markedList.add(MarkingUtil.getMarkedReading(reading,deltaFieldId,moduleId,type,currentReading,lastReading));
					currentReading=lastReading;
					delta=0.0;
					String subject="Data correction Done! " +getHighValString(resourceId);
					sendEmail(subject, getEmailBody(lastReading,currentReading,null));
				}
				else {
					//send email alone with no data correction..
					sendEmail(getHighValString(resourceId), getEmailBody(lastReading,currentReading,null));
				}
			}
			
			reading.addReading(deltaFieldName, ReportsUtil.roundOff(delta,4));			
			if(dataGapCount>1 && delta > 0)	 
			{
				//need to mark as hourly violation..
				type=MarkType.HIGH_VALUE_HOURLY_VIOLATION;
				
				markedList.add(MarkingUtil.getMarkedReading(reading,deltaFieldId,moduleId,type,currentReading,lastReading));
				reading.setMarked(true);

			}
			
	}

	private static final int INTERVAL_COUNT=4;
	
	private static final long DELATA_HIGH_VAL_BAND=10000;
	
	private static final long DELTA_MF=1000;
	
	private static final long RESET_BAND_MF=50;
	
	
	private Double getEstimatedDelta(Double lastDelta, float dataGapCount) {
		
		//int gapMultiplier= (dataGapCount==0)?1:dataGapCount;//if there is no gap.. we can consider it as 1 for calculation ease..
		double deltaAdjuster= (lastDelta<=1)?lastDelta+1:lastDelta;//no harm in adding 1 to delta .. this is to avoid zero & 1 last delta
		double estimatedDelta= (deltaAdjuster)*(dataGapCount);//
		return estimatedDelta;
		
	}
	
	private long getQueryTime(long resourceId, FacilioField field, FacilioModule module, int intervals) {
		
		long millis=60*1000;
		long dataIntervalMillis=15*millis;
		
		try {
			dataIntervalMillis = ReadingsAPI.getDataInterval(resourceId, field, module)*millis;
		} catch (Exception e) {
			LOGGER.error("Exception while getting data interval", e);
		}
		long adjusterMillis=dataIntervalMillis/2;//adding adjuster..
		long diffTime=(dataIntervalMillis*intervals)+adjusterMillis;
		return System.currentTimeMillis()-diffTime;

	}
	
	
	private double getLastValForMeterReset(Double latestValue,List<Double> actualValues ) {

		int size=actualValues.size();
		if(size<INTERVAL_COUNT) {
			return -1;
		}
		double diff=0.0;
		int loopCount=0;

		for(int i=size-1;i>=0;i--) {
			Double lastValue=actualValues.get(i);
			diff=latestValue/lastValue;//zero should not come here..
			if(diff<1 || diff>RESET_BAND_MF) {
				//atleast 1 delta -ve  or out of range out of just 4 values, so it's hard to decide the reset with confidence..
				//safer side we should abandon the checking
				return -1;
			}//else do we need to do anything ??
			if(loopCount==INTERVAL_COUNT) {
				//here returning like this.. inorder to get the complete delta without missing out the previous 4 intervals
				return lastValue;
			}
			latestValue=lastValue;
		}
		return -1;
	}

	
	private String getDecrementalString(long resourceId) {
		return "Consecutive Decremental values detected for meter: "+resourceId;
	}
	
	private String getHighValString(long resourceId) {
		return "Too High value received for meter: "+resourceId;
	}
	
	private String getEmailBody(Double lastReading,Double currentReading,List<Double> actualValues) {

		return "Last reading: "+lastReading+"\nCurrent Reading: "+currentReading+" ,\nPrevious Actual Vals : "+actualValues;
	}
	
	private void sendEmail(String subject, String body) {
		CommonCommandUtil.emailAlert(subject, body);
	}
}
