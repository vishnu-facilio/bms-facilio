package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class RunThroughReadingRulesCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(RunThroughReadingRulesCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		
		if (id == -1 || range == null || range.getStartTime() == -1 || range.getEndTime() == -1) {
			throw new IllegalArgumentException("In sufficient params for running Alarm Rules for historical data");
		}
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(id);
		if (rule == null || !(rule instanceof ReadingRuleContext)) {
			throw new IllegalArgumentException("Invalid Alarm rule id for running through historical data");
		}
		
		ReadingRuleContext readingRule = (ReadingRuleContext) rule;
		if (readingRule.getMatchedResources() == null || readingRule.getMatchedResources().isEmpty()) {
			return false;
		}
		
		List<ReadingContext> readings = fetchReadings(readingRule, range);
		executeWorkflows(readingRule, readings, (FacilioContext) context);
		return false;
	}
	
	private void executeWorkflows(ReadingRuleContext readingRule, List<ReadingContext> readings, FacilioContext context) throws Exception {
		if (readings != null && !readings.isEmpty()) {
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
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

	private List<ReadingContext> fetchReadings(ReadingRuleContext readingRule, DateRange range) throws Exception {
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
																.andCondition(CriteriaAPI.getCondition(ttimeField, range.toString(), DateOperators.BETWEEN))
																;
		
		return selectBuilder.get();
	}
}
