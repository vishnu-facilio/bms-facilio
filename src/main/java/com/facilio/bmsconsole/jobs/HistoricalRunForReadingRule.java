package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.ExpressionAggregateOperator;
import com.facilio.workflows.util.WorkflowUtil;

public class HistoricalRunForReadingRule extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRunForReadingRule.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(jc.getJobId(), true);
			if (readingRule.getMatchedResources() == null || readingRule.getMatchedResources().isEmpty()) {
				return;
			}
			
			JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			long startTime = (long) props.get("startTime");
			long endTime = (long) props.get("endTime");
			
			LOGGER.info("Historical execution of rule : "+readingRule.getId()+" for resources : "+readingRule.getMatchedResources().keySet());
			Map<String, List<ReadingDataMeta>> supportFieldsRDM = null;
			List<WorkflowFieldContext> fields = null;
			if (readingRule.getWorkflow() != null) {
				fields = WorkflowUtil.getWorkflowFields(readingRule.getWorkflow().getId());
				LOGGER.info("Dependent fields : "+fields);
			
				if (fields != null && !fields.isEmpty()) {
					supportFieldsRDM = getSupportingData(fields, startTime, endTime, -1);
					LOGGER.info("Support Fields RDM Values size : "+supportFieldsRDM.size());
				}
			}
			for (long resourceId : readingRule.getMatchedResources().keySet()) {
				LOGGER.info("Gonna fetch data and execute rule for resource : "+resourceId);
				Map<String, List<ReadingDataMeta>> currentFields = supportFieldsRDM;
				Map<String, List<ReadingDataMeta>> currentRDMList = getSupportingData(fields, startTime, endTime, resourceId);
				if (currentRDMList != null && !currentRDMList.isEmpty()) {
					LOGGER.info("Current resource Support Fields RDM Values size : "+currentRDMList.size());
					if (supportFieldsRDM == null) {
						currentFields = currentRDMList;
					}
					else {
						currentFields = new HashMap<>(supportFieldsRDM);
						currentFields.putAll(currentRDMList);
					}
				}
				
				long processStartTime = System.currentTimeMillis();
				long currentStartTime = startTime - (ReadingsAPI.getDataInterval(resourceId, readingRule.getReadingField().getModule()) * 60 * 1000);
				List<ReadingContext> readings = fetchReadings(readingRule, resourceId, currentStartTime, endTime);
				executeWorkflows(readingRule, readings, currentFields, fields);
				LOGGER.info("Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" for resource : "+resourceId+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - processStartTime));
			}
			long timeTaken = (System.currentTimeMillis() - jobStartTime);
			LOGGER.info("Total Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
			
			if (AccountUtil.getCurrentOrg().getId() == 135) {
				JSONObject json = new JSONObject();
				json.put("to", "praveen@facilio.com, manthosh@facilio.com, shivaraj@facilio.com");
				json.put("sender", "noreply@facilio.com");
				json.put("subject", "Historical Run completed for Rule : "+jc.getJobId());
				json.put("message", "Total Time taken for Historical Run for Reading Rule : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+timeTaken);
				
				AwsUtil.sendEmail(json);
			}
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("HistoricalRunForReadingRule", "Historical Run failed for reading rule : "+jc.getJobId(), e);
		}
	}
	
	private void executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings, Map<String, List<ReadingDataMeta>> supportFieldsRDM, List<WorkflowFieldContext> fields) throws Exception {
		if (readings != null && !readings.isEmpty()) {
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
			
			FacilioContext context = new FacilioContext(); 
			ReadingDataMeta prevRDM = null;			
			int itr = 0;
			/*for (; itr < readings.size(); itr++) {
				prevRDM = getRDM(readings.get(itr), readingRule.getReadingField());
				if (prevRDM != null) {
					break;
				}
			}*/
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> allFields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
			Map<String, Integer> lastItr = new HashMap<>(); //To store itr of currently matched rdm itr
			
			for (int i = itr; i < readings.size(); i++) {
				ReadingContext reading = readings.get(i);
				LOGGER.debug("Executing rule for reading : "+reading);
				try {
					ReadingDataMeta currentRDM = getRDM(reading, readingRule.getReadingField());
					LOGGER.debug("Current RDM : "+currentRDM);
					if (currentRDM != null) {
						context.put(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META, Collections.singletonMap(ReadingsAPI.getRDMKey(reading.getParentId(), readingRule.getReadingField()), prevRDM));
						
						Map<String, ReadingDataMeta> rdmCache = getCurrentRDMs(reading, fieldMap);
						LOGGER.debug("Current RDMs : "+rdmCache);
						getOtherRDMs(reading.getParentId(), reading.getTtime(), supportFieldsRDM, rdmCache, lastItr, fields);
						LOGGER.debug("After other RDM : "+rdmCache);
//						LOGGER.info("Current RDM : "+rdmCache.keySet());
//						LOGGER.info("Current RDM Size : "+rdmCache.size());
						
						context.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, rdmCache);
						
						Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(readingRule.getReadingField().getModule().getName(), readingRule.getReadingField().getModule().getName(), FieldUtil.getAsProperties(reading), recordPlaceHolders);
						WorkflowRuleAPI.evaluateWorkflow(readingRule, readingRule.getReadingField().getModule().getName(), reading, null, recordPlaceHolders, context);
						
						prevRDM = currentRDM;
					}
				}
				catch (Exception e) {
					StringBuilder builder = new StringBuilder("Error during execution of rule : ");
					builder.append(readingRule.getId());
					builder.append(" for Record : ")
							.append(reading.getId())
							.append(" of module : ")
							.append(readingRule.getReadingField().getModule().getName());
					LOGGER.log(Level.ERROR, builder.toString(), e);
					throw e;
				}
			}
		}
	}
	
	private void getOtherRDMs(long resourceId, long ttime, Map<String, List<ReadingDataMeta>> rdmMap, Map<String, ReadingDataMeta> rdmCache, Map<String, Integer> lastItr, List<WorkflowFieldContext> fields) {
		if (rdmMap != null && !rdmMap.isEmpty() && fields != null && !fields.isEmpty()) {
			for (WorkflowFieldContext field : fields) {
				if (field.getAggregationEnum() == ExpressionAggregateOperator.LAST_VALUE) {
					long parentId = field.getResourceId() == -1 ? resourceId : field.getResourceId();
					String rdmKey = ReadingsAPI.getRDMKey(parentId, field.getField());
					List<ReadingDataMeta> rdmList = rdmMap.get(ReadingsAPI.getRDMKey(parentId, field.getField()));
					ReadingDataMeta prevRDM = null;
					Integer itr = lastItr.get(rdmKey);
					if (itr == null) {
						itr = 0;
					}
					
					if (rdmList != null && !rdmList.isEmpty()) {
						for (; itr < rdmList.size(); itr++) {
							ReadingDataMeta rdm = rdmList.get(itr);
							if (rdm.getTtime() > ttime) {
								break;
							}
							prevRDM = rdm;
						}
					}
					if (prevRDM != null) {
						rdmCache.put(rdmKey, prevRDM);
						lastItr.put(rdmKey, itr - 1);
					}
				}
			}
		}
	}
	
	private Map<String, ReadingDataMeta> getCurrentRDMs(ReadingContext reading, Map<String, FacilioField> fieldMap) {
		Map<String, ReadingDataMeta> rdmCache = new HashMap<>();
		Map<String, Object> data = reading.getReadings();
		if (data != null && !data.isEmpty()) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				FacilioField field = fieldMap.get(entry.getKey());
				if (field != null) {
					ReadingDataMeta rdm = getRDM(reading, field);
					rdmCache.put(ReadingsAPI.getRDMKey(reading.getParentId(), field), rdm);
				}
			}
		}
		return rdmCache;
	}
	
	private Map<String, List<ReadingDataMeta>> getSupportingData(List<WorkflowFieldContext> fields, long startTime, long endTime, long resourceId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, List<ReadingDataMeta>> supportingValues = new HashMap<>();
		for (WorkflowFieldContext field : fields) {
			if (field.getAggregationEnum() == ExpressionAggregateOperator.LAST_VALUE) {
				if (resourceId == -1 && field.getResourceId() == -1) {
					continue;
				}
				if (resourceId != -1 && field.getResourceId() != -1) {
					continue;
				}
				long parentId = resourceId == -1? field.getResourceId() : resourceId;
				
				FacilioField valField = modBean.getField(field.getFieldId());
				field.setField(valField);
				String rdmKey = ReadingsAPI.getRDMKey(parentId, valField);
				
				List<FacilioField> allFields = modBean.getAllFields(valField.getModule().getName());
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
				FacilioField parentField = fieldMap.get("parentId");
				FacilioField ttimeField = fieldMap.get("ttime");
				
				List<FacilioField> selectFields = new ArrayList<>();
				selectFields.add(valField);
				selectFields.add(parentField);
				selectFields.add(ttimeField);
				
				SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.select(selectFields)
																		.module(valField.getModule())
																		.beanClass(ReadingContext.class)
																		.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
																		.andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
																		.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
																		.orderBy("TTIME")
																		;

				List<ReadingContext> values = selectBuilder.get();
				if (values != null && !values.isEmpty()) {
					List<ReadingDataMeta> rdms = new ArrayList<>();
					for (ReadingContext value : values) {
						ReadingDataMeta rdm = getRDM(value, valField);
						rdms.add(rdm);
					}
					supportingValues.put(rdmKey, rdms);
				}
				else {
					selectBuilder = new SelectRecordsBuilder<ReadingContext>()
										.select(selectFields)
										.module(valField.getModule())
										.beanClass(ReadingContext.class)
										.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
										.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(startTime), DateOperators.IS_BEFORE))
										.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
										.orderBy("TTIME DESC")
										.limit(1)
										;
					values = selectBuilder.get();
					ReadingDataMeta rdm = null;
					if (values != null && !values.isEmpty()) {
						rdm = getRDM(values.get(0), valField);
					}
					else {
						rdm = ReadingsAPI.getReadingDataMeta(parentId, valField);
					}
					if (rdm != null) {
						supportingValues.put(rdmKey, Collections.singletonList(rdm));
					}
				}
			}
		}
		return supportingValues;
	}
	
	private ReadingDataMeta getRDM(ReadingContext value, FacilioField valField) {
		Object val = value.getReading(valField.getName());
		if (val != null) {
			ReadingDataMeta rdm = new ReadingDataMeta();
			rdm.setFieldId(valField.getFieldId());
			rdm.setField(valField);
			rdm.setTtime(value.getTtime());
			rdm.setValue(val);
			rdm.setReadingDataId(value.getId());
			rdm.setResourceId(value.getParentId());
			return rdm;
		}
		return null;
	}

	private List<ReadingContext> fetchReadings(ReadingRuleContext readingRule, long resourceId, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentField = fieldMap.get("parentId");
		FacilioField ttimeField = fieldMap.get("ttime");
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(fields)
																.module(readingRule.getReadingField().getModule())
																.beanClass(ReadingContext.class)
																.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(resourceId), PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
																.andCondition(CriteriaAPI.getCondition(readingRule.getReadingField(), CommonOperators.IS_NOT_EMPTY))
																.orderBy("TTIME")
																;
		
		return selectBuilder.get();
	}
}
