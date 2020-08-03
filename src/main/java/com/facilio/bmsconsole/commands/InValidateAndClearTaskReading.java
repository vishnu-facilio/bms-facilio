package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskErrorContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class InValidateAndClearTaskReading extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(InValidateAndClearTaskReading.class.getName());
	
	long taskContextId, currentInputTime;
	String currentInputValue;
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.READING_FIELD_UNITS_VALIDATION)) {
			return false;
		}
		
		try {		
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
										
										if(currentTask.getInputValue().trim().isEmpty()) { //to be removed for invalidation
											return false;
										}
										
										currentInputValue = currentTask.getInputValue();
										currentInputTime = currentTask.getInputTime();
									
										if((currentInputValue.equals("-1") || StringUtils.isEmpty(currentInputValue)) && taskContext.getReadingDataId() != -1)  //past case and empty string
										{ 
											context.put(FacilioConstants.ContextNames.SKIP_VALIDATION, false);
											context.put(FacilioConstants.ContextNames.IGNORE_SPL_NULL_HANDLING, true);
											currentTask.setInputValue("");
											String deltaFieldName = numberField.getName()+"Delta";
		
											Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
											if (readingMap != null && !readingMap.isEmpty()) {	
												List<ReadingContext> readings = readingMap.get(numberField.getModule().getName());
												for(ReadingContext reading: readings) {
													if(reading.getId() == taskContext.getReadingDataId() && reading.getParentId() == taskContext.getResource().getId()) {
														reading.addReading(numberField.getName(), null);
														reading.addReading(deltaFieldName, null);
														if(rdm.getReadingDataId()!= -1 && taskContext.getReadingDataId() == rdm.getReadingDataId()) {
															rdm.setValue(null);
															//current value update case
														}
													}
												}							
											}
											
											Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
											if (recordMap != null && !recordMap.isEmpty()) {	
												List records = readingMap.get(numberField.getModule().getName());
												for(Object record: records) {
													Map<String, Object> recordData = FieldUtil.getAsProperties(record);
													if(recordData != null && (long)recordData.get(FacilioConstants.ContextNames.ID) == taskContext.getReadingDataId()) {
														recordData.put(numberField.getName(), null);
														recordData.put(deltaFieldName, null);
														if(rdm.getReadingDataId()!= -1 && taskContext.getReadingDataId() == rdm.getReadingDataId()) {
															rdm.setValue(null);
															//current value update case
														}
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
			LOGGER.log(Level.ERROR, "InValidateAndClearTaskReading Currenttask ID: "+ taskContextId + " Current Input Value: " + currentInputValue + 
					" Current Input Time: " + currentInputTime + " " + e.getMessage() , e);
		}
		return false;
	}

}
