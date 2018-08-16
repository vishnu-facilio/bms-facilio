package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
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
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class HistoricalRunForReadingRule extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(HistoricalRunForReadingRule.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long jobStartTime = System.currentTimeMillis();
			ReadingRuleContext readingRule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(jc.getJobId());
			if (readingRule.getMatchedResources() == null || readingRule.getMatchedResources().isEmpty()) {
				return;
			}
			
			JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			long startTime = (long) props.get("startTime");
			long endTime = (long) props.get("endTime");
			List<ReadingContext> readings = fetchReadings(readingRule, startTime, endTime);
			executeWorkflows(readingRule, readings);
			LOGGER.info("Time taken for Historical Run for Reading Rul : "+jc.getJobId()+" between "+startTime+" and "+endTime+" is "+(System.currentTimeMillis() - jobStartTime));
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			CommonCommandUtil.emailException("HistoricalRunForReadingRule", "Historical Run failed for reading rule : "+jc.getJobId(), e);
		}
	}
	
	private void executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings) throws Exception {
		if (readings != null && !readings.isEmpty()) {
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
			
			FacilioContext context = new FacilioContext(); 
			context.put(FacilioConstants.ContextNames.READING_DATA_META, fetchRDM(readingRule));
			
			for (ReadingContext reading : readings) {
				try {
					Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
					CommonCommandUtil.appendModuleNameInKey(readingRule.getReadingField().getModule().getName(), readingRule.getReadingField().getModule().getName(), FieldUtil.getAsProperties(reading), recordPlaceHolders);
					WorkflowRuleAPI.evaluateWorkflow(readingRule, readingRule.getReadingField().getModule().getName(), reading, recordPlaceHolders, context);
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
	
	private Map<String, ReadingDataMeta> fetchRDM(ReadingRuleContext readingRule) throws Exception {
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		for (Long id : readingRule.getMatchedResources().keySet()) {
			rdmPairs.add(Pair.of(id, readingRule.getReadingField()));
		}
		Map<String, ReadingDataMeta> rdm = ReadingsAPI.getReadingDataMetaMap(rdmPairs);
		return rdm;
	}

	private List<ReadingContext> fetchReadings(ReadingRuleContext readingRule, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(readingRule.getReadingField().getModule().getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField parentField = fieldMap.get("parentId");
		FacilioField ttimeField = fieldMap.get("ttime");
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(readingRule.getReadingField());
		selectFields.add(parentField);
		selectFields.add(ttimeField);
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																.select(selectFields)
																.module(readingRule.getReadingField().getModule())
																.beanClass(ReadingContext.class)
																.andCondition(CriteriaAPI.getCondition(parentField, readingRule.getMatchedResources().keySet(), PickListOperators.IS))
																.andCondition(CriteriaAPI.getCondition(ttimeField, startTime+","+endTime, DateOperators.BETWEEN))
																;
		
		return selectBuilder.get();
	}
}
