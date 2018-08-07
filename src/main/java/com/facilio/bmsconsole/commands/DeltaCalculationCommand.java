package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class DeltaCalculationCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(DeltaCalculationCommand.class.getName());
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
					setDelta(fieldMap,"totalEnergyConsumption",module, reading,metaMap,markedList, deltaRdmPairs, skipLastReadingCheck);
					setDelta(fieldMap,"phaseEnergyR",module,reading,metaMap,markedList, deltaRdmPairs, skipLastReadingCheck);
					setDelta(fieldMap,"phaseEnergyY",module,reading,metaMap,markedList, deltaRdmPairs, skipLastReadingCheck);
					setDelta(fieldMap,"phaseEnergyB",module,reading,metaMap,markedList, deltaRdmPairs, skipLastReadingCheck);
				}
				
				if (!deltaRdmPairs.isEmpty()) {
					List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(deltaRdmPairs) ;
					
					for(ReadingDataMeta meta : metaList) {
						long resourceId = meta.getResourceId();
						long fieldId = meta.getField().getFieldId();
						metaMap.put(resourceId+"_"+fieldId, meta);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.MARKED_READINGS, markedList);
			LOGGER.debug("Inside DeltaCommand#######  "+markedList);

		}
		LOGGER.debug("Exiting DeltaCommand#######  ");

		return false;
	}

	private void setDelta(Map<String,FacilioField>  fieldMap, String fieldName,FacilioModule module, ReadingContext reading,Map<String, 
			ReadingDataMeta> metaMap,List<MarkedReadingContext> markedList,List<Pair<Long, FacilioField>> deltaRdmPairs, Boolean skipLastReadingCheck ) {
			
			FacilioField readingField=fieldMap.get(fieldName);
			FieldType dataType=readingField.getDataTypeEnum();
			Object readingVal=reading.getReading(fieldName);
			String deltaFieldName = fieldName+"Delta";
			Object deltaVal=reading.getReading(deltaFieldName);
			

			if( deltaVal!=null || readingVal==null) {// delta already set in reading or reading is null..
				return;
			}
			
			long resourceId=reading.getParentId();
			
			long energyFieldId= readingField.getFieldId();
			ReadingDataMeta consumptionMeta = metaMap.get(resourceId+"_"+energyFieldId);
			if(consumptionMeta == null) {
				return;
			}
			
			Double lastReading = (Double) consumptionMeta.getValue(); 
			if(lastReading == null) {
				return;
			}
			Double currentReading = (Double) FieldUtil.castOrParseValueAsPerType(dataType, readingVal);
			
			long moduleId=module.getModuleId();
			MarkType type = MarkType.NEGATIVE_VALUE;
			if(currentReading<=0) {
				reading.addReading(fieldName, lastReading);
				if(currentReading == 0) {
					type=MarkType.ZERO_VALUE;
				}
				markedList.add(DeviceAPI.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
				currentReading=lastReading;
			}
			else if(currentReading<lastReading) {
				type=MarkType.DECREMENTAL_VALUE;
				long deltaFieldId=fieldMap.get(deltaFieldName).getFieldId();
				markedList.add(DeviceAPI.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
				markedList.add(DeviceAPI.getMarkedReading(reading,deltaFieldId,moduleId,type,currentReading,lastReading));
				if (!skipLastReadingCheck) {
					reading.addReading(fieldName, lastReading);
					currentReading=lastReading;
				}
			}
			
			Double delta= currentReading-lastReading;
			reading.addReading(deltaFieldName, ReportsUtil.roundOff(delta,4));
			deltaRdmPairs.add(Pair.of(reading.getParentId(), fieldMap.get(deltaFieldName)));
			long currentTime=(reading.getTtime()!=-1)? reading.getTtime():System.currentTimeMillis() ;
			boolean dataGap= DeviceAPI.isDataGap(resourceId, module,currentTime, consumptionMeta.getTtime());
			
			if(dataGap)	 
			{
				//need to mark as hourly violation..
				type=MarkType.HIGH_VALUE_HOURLY_VIOLATION;
				markedList.add(DeviceAPI.getMarkedReading(reading,energyFieldId,moduleId,type,currentReading,lastReading));
				reading.setMarked(true);

			}
			
	}

	

}
