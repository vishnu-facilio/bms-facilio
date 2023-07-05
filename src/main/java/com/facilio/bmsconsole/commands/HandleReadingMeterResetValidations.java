package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.MarkedReadingContext.MarkType;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import com.facilio.bmsconsole.util.MarkingUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class HandleReadingMeterResetValidations extends FacilioCommand {

	
	long taskContextId, currentInputTime;
	String currentInputValue;
	
	private static final Logger LOGGER = LogManager.getLogger(HandleReadingMeterResetValidations.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		try { 
			
			if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION)) {
				return false;
			}
			
			TaskContext currentTask = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
			Boolean skipValidation = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_VALIDATION);

			if(currentTask != null && currentTask.getInputValue() != null) {
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				if(recordIds != null && !recordIds.isEmpty()) {
					Map<Long, TaskContext> oldTasks = (Map<Long, TaskContext>) context.get(FacilioConstants.ContextNames.TASK_MAP);
					for(int i = 0; i < recordIds.size(); i++) {
						TaskContext taskContext = oldTasks.get(recordIds.get(i));
						if(taskContext.getInputTypeEnum() != null)
						{
							switch(taskContext.getInputTypeEnum()) 
							{
							case READING:
								
								if (taskContext.getReadingField() != null && taskContext.getResource() != null) 
								{
									switch(taskContext.getReadingField().getDataTypeEnum())  
									{   
									case NUMBER:
									case DECIMAL:
											
										taskContextId = taskContext.getId();
										ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(taskContext.getResource().getId(), taskContext.getReadingField());
										NumberField numberField = (NumberField) taskContext.getReadingField();
										
										if(rdm != null && rdm.getValue() != null && rdm.getValue().equals("-1.0")) {
											return false;
										}
										
										if(currentTask.getInputValue().trim().isEmpty()) {
											return false;
										}
										
										currentInputValue = currentTask.getInputValue();
										currentInputTime = currentTask.getInputTime();
										JSONObject addlInfoJson = currentTask.getAdditionalInfo();
										boolean isMeterReset = false;
										if(addlInfoJson != null && addlInfoJson.get("isMeterReset") != null && addlInfoJson.get("meterResetConsumption") != null && (Boolean)addlInfoJson.get("isMeterReset")) {
											isMeterReset = true;  
										}
									
										if(SensorRuleUtil.isCounterField(numberField) && isMeterReset) 
										{
											context.put(FacilioConstants.ContextNames.SKIP_VALIDATION, false);
											context.put(FacilioConstants.ContextNames.DELTA_RESETTED, true);
											String deltaFieldName = numberField.getName()+"Delta";
													
											if(currentTask.getReadingFieldUnit() <= 0) {
												LOGGER.log(Level.INFO,"Meter Reset Unit missing from client, Currenttask ID: "+ taskContextId + " Current Input Time: " + currentInputTime + " Current Input Value: " + currentInputValue);
											}
											Unit currentInputUnit = getCurrentInputUnit(rdm, currentTask, numberField);	

											Double currentValue = FacilioUtil.parseDouble(currentInputValue);
											Double currentValueInSiUnit = (currentInputUnit != null) ? UnitsUtil.convertToSiUnit(currentValue, currentInputUnit) : currentValue;				
											
											Double currentDeltaValue = FacilioUtil.parseDouble(addlInfoJson.get("meterResetConsumption"));
											Double currentDeltaValueInSiUnit = (currentInputUnit != null) ? UnitsUtil.convertToSiUnit(currentDeltaValue, currentInputUnit) : currentDeltaValue;				
	
											Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
											if (readingMap != null && !readingMap.isEmpty()) {	
												List<ReadingContext> readings = readingMap.get(numberField.getModule().getName());
												for(ReadingContext reading: readings) {
													if(reading.getParentId() == taskContext.getResource().getId()) {														
														reading.addReading(numberField.getName(), currentValueInSiUnit);
														reading.addReading(deltaFieldName, currentDeltaValueInSiUnit);
														
														MarkType type = MarkType.TASK_METER_RESET_VALUE;
														MarkedReadingContext resettedReading = MarkingUtil.getMarkedReading(reading,numberField.getFieldId(),numberField.getModuleId(),type,currentValueInSiUnit,currentDeltaValueInSiUnit);
														resettedReading.setTtime(currentInputTime);
														resettedReading.setResourceId(reading.getParentId());
														MarkingUtil.addMarkedreadings(Collections.singletonList(resettedReading));
													}
												}							
											}
											
											Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
											if (recordMap != null && !recordMap.isEmpty()) {	
												List records = readingMap.get(numberField.getModule().getName());
												for(Object record: records) {
													Map<String, Object> recordData = FieldUtil.getAsProperties(record);
													if(recordData != null && (long)recordData.get(FacilioConstants.ContextNames.PARENT_ID) == taskContext.getResource().getId()) {
														recordData.put(numberField.getName(), currentValueInSiUnit);
														recordData.put(deltaFieldName, currentDeltaValueInSiUnit);
//														if(rdm.getReadingDataId()!= -1 && taskContext.getReadingDataId() == rdm.getReadingDataId()) {
//															rdm.setValue(null);
//															//current value update case
//														}
													}
												}							
											}
												
										}
									}
								}
								break;
							}
						}
					}
				}
			}	
			
		}
		catch(Exception e) {
			LOGGER.log(Level.ERROR, "HandleReadingMeterResetValidations Currenttask ID: "+ taskContextId + " Current Input Value: " + currentInputValue + 
					" Current Input Time: " + currentInputTime + " " + e.getMessage() , e);
		}
		return false;
	}
	
	public static Unit getCurrentInputUnit(ReadingDataMeta rdm,TaskContext currentTask,NumberField numberField) throws Exception {
		if(currentTask.getReadingFieldUnit() > 0) {
			return Unit.valueOf(currentTask.getReadingFieldUnit());
		}
		else if(rdm.getUnit() > 0) {
			LOGGER.log(Level.INFO,"Task Unit missing from client, fetching from rdm, Currenttask ID: "+ currentTask.getId() + " Current Input Time: " + currentTask.getInputTime() + " Current Input Value: " + currentTask.getInputValue() + "  Unit "+ Unit.valueOf(rdm.getUnit()));
			return Unit.valueOf(rdm.getUnit());
		}
		else if (numberField.getMetric() > 0)
		{
			LOGGER.log(Level.INFO,"Task Unit missing from client, fetching from orgLevelUnit, Currenttask ID: "+ currentTask.getId() + " Current Input Time: " + currentTask.getInputTime() + " Current Input Value: " + currentTask.getInputValue() + " Unit "+AccountUtil.getOrgBean().getOrgDisplayUnit(numberField.getMetric()));
			return AccountUtil.getOrgBean().getOrgDisplayUnit(numberField.getMetric());
		}
		else {
			LOGGER.log(Level.INFO,"Unit missing from client, No unit case, Currenttask ID: "+ currentTask.getId() + " Current Input Time: " + currentTask.getInputTime() + " Current Input Value: " + currentTask.getInputValue());
		}
		return null;
	}

}
