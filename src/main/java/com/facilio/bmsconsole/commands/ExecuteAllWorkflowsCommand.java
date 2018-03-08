package com.facilio.bmsconsole.commands;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class ExecuteAllWorkflowsCommand implements Command 
{
	RuleType[] ruleTypes;
	public ExecuteAllWorkflowsCommand(RuleType... ruleTypes) {
		// TODO Auto-generated constructor stub
		this.ruleTypes = ruleTypes;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List records = (List) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(records == null) {
			Object record = context.get(FacilioConstants.ContextNames.RECORD);
			if(record != null) {
				records = Collections.singletonList(record);
			}
		}
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//		records = new LinkedList<>(records);
		if(records != null && !records.isEmpty()) {
			Map<String, Map<String,Object>> lastReadingMap =(Map<String, Map<String,Object>>)context.get(FacilioConstants.ContextNames.LAST_READINGS);
			records = new LinkedList<>(records);
			ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			if(activityType != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getActiveWorkflowRulesFromActivityAndRuleType(moduleId, Collections.singletonList(activityType), ruleTypes);
				
				if(workflowRules != null && workflowRules.size() > 0) {
					Map<String, Object> placeHolders = new HashMap<>();
					CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
					CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
					
					for(WorkflowRuleContext workflowRule : workflowRules) {
						Map<String, Object> rulePlaceHolders = new HashMap<>(placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "rule", FieldUtil.getAsProperties(workflowRule), rulePlaceHolders);
						Iterator<Integer> it = records.iterator();
						while (it.hasNext()) {
							Object record = it.next();
							if(workflowRule.getRuleTypeEnum() == RuleType.READING_RULE && ((ReadingRuleContext)workflowRule).getResourceId() != ((ReadingContext)record).getParentId()) { //Reading Rule check for specific assets used in the rule
								continue;
							}
							Map<String, Object> recordPlaceHolders = new HashMap<>(rulePlaceHolders);
							CommonCommandUtil.appendModuleNameInKey(moduleName, moduleName, FieldUtil.getAsProperties(record), recordPlaceHolders);
							boolean criteriaFlag = evaluateCriteria(workflowRule, record, recordPlaceHolders);
							boolean workflowFlag = evaluateWorkflowExpression(workflowRule, record, recordPlaceHolders);
							boolean miscFlag = evaluateMisc(workflowRule, record, recordPlaceHolders, lastReadingMap);
							if(criteriaFlag && workflowFlag && miscFlag) {
								executeWorkflowActions(workflowRule, record, context, recordPlaceHolders);
								if(workflowRule.getRuleTypeEnum().stopFurtherRuleExecution()) {
									it.remove();
								}
							}
						}
					}
				}	
			}
		}
		return false;
	}
	
	private boolean evaluateWorkflowExpression (WorkflowRuleContext workflowRule, Object record, Map<String, Object> placeHolders) throws Exception {
		boolean workflowFlag = true;
		if (workflowRule.getWorkflow() != null && workflowRule.getWorkflow().isBooleanReturnWorkflow()) {
			double result = (double) WorkflowUtil.getWorkflowExpressionResult(workflowRule.getWorkflow().getWorkflowString(), placeHolders);
			workflowFlag = result == 1;
		}
		return workflowFlag;
	}
	
	private boolean evaluateCriteria (WorkflowRuleContext workflowRule, Object record, Map<String, Object> placeHolders) throws Exception {
		boolean criteriaFlag = true;
		Criteria criteria = workflowRule.getCriteria();
		if(criteria != null) {
			switch (workflowRule.getRuleTypeEnum()) {
				case READING_RULE:
				case PM_READING_RULE:
					criteriaFlag = workflowRule.getCriteria().computePredicate(placeHolders).evaluate(record);
					if(criteriaFlag) {
						updateLastValueForReadingRule((ReadingRuleContext) workflowRule, (ReadingContext) record);
					}
					break;
				default:
					criteriaFlag = criteria.computePredicate().evaluate(record);
					break;
			}
		}
		return criteriaFlag;
	}
	
	private boolean evaluateMisc (WorkflowRuleContext workflowRule, Object record, Map<String, Object> placeHolders, Map<String, Map<String, Object>> lastReadingMap) throws Exception {
		boolean miscFlag = true;
		switch (workflowRule.getRuleTypeEnum()) {
			case READING_RULE:
			case PM_READING_RULE:
				miscFlag = evelauteMiscForReadingRule((ReadingRuleContext)workflowRule, (ReadingContext) record, placeHolders, lastReadingMap);
				break;
			default:
				break;
		}
		return miscFlag;
	}
	
	private boolean evelauteMiscForReadingRule (ReadingRuleContext readingRule, ReadingContext record, Map<String, Object> placeHolders, Map<String, Map<String, Object>> lastReadingMap) throws Exception {
		switch (readingRule.getThresholdTypeEnum()) {
			case FLAPPING:
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField(readingRule.getReadingFieldId());
				Object currentReadingObj = FieldUtil.castOrParseValueAsPerType(field.getDataTypeEnum(), record.getReading(field.getName()));
				if (currentReadingObj != null && currentReadingObj instanceof Number) {
					double diff = calculateDiff(readingRule, currentReadingObj, record, field, lastReadingMap);
					double flapRange = Math.abs(readingRule.getMaxFlapValue() - readingRule.getMinFlapValue());
					return isFlapped(readingRule, record, diff, flapRange);
				}
				break;
			default:
				break;
		}
		return true;
	}
	
	private boolean isFlapped(ReadingRuleContext readingRule, ReadingContext record, double diff, double flapRange) throws Exception {
		boolean flapThreshold = false;
		if (diff >= flapRange) {
			List<Long> flapsToBeDeleted = new ArrayList<>();
			List<Map<String, Object>> flaps = getFlaps(readingRule.getId());
			int flapCount = 0;
			if (flaps != null && !flaps.isEmpty()) {
				flapCount = flaps.size();
				for(Map<String, Object> flap : flaps) {
					if (record.getTtime() - (long) flap.get("flapTime") > readingRule.getFlapInterval()) {
						flapsToBeDeleted.add((Long) flap.get("id"));
						flapCount--;
					}
				}
			}
			flapCount++;
			flapThreshold = flapCount == readingRule.getFlapFrequency();
			if (flapThreshold) {
				//Reset prev flaps
				flapsToBeDeleted.clear();
				for(Map<String, Object> flap : flaps) {
					flapsToBeDeleted.add((Long) flap.get("id"));
				}
				flapCount = 0;
			}
			else {
				addFlap(readingRule.getId(), record.getTtime());
			}
			updateFlapCount(readingRule.getId(), flapCount);
			deleteOldFlaps(flapsToBeDeleted);
			
		}
		return flapThreshold;
	}
	
	private void deleteOldFlaps(List<Long> flapsToBeDeleted) throws SQLException {
		// TODO Auto-generated method stub
		if (!flapsToBeDeleted.isEmpty()) {
			FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(flapsToBeDeleted, module))
															;
			deleteBuilder.delete();
		}
	}

	private void updateFlapCount(long ruleId, int flapCount) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setFlapCount(flapCount);
		
		FacilioModule module = ModuleFactory.getReadingRuleModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ruleId, module));
		
		updateBuilder.update(FieldUtil.getAsProperties(rule));
	}
	
	private long addFlap(long ruleId, long flapTime) throws Exception {
		Map<String, Object> newFlap = new HashMap<>();
		newFlap.put("ruleId", ruleId);
		newFlap.put("flapTime", flapTime);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getReadingRuleFlapsFields())
														.table(ModuleFactory.getReadingRuleFlapsModule().getTableName());
		
		return insertBuilder.insert(newFlap);
	}
	
	private double calculateDiff(ReadingRuleContext rule, Object currentReadingObj, ReadingContext record, FacilioField field, Map<String, Map<String, Object>> lastReadingMap) throws Exception {
		double diff = -1;
		Map<String, Object> lastValMap = lastReadingMap.get(record.getParentId()+"_"+field.getName());
		Number lastReading = (Number) FieldUtil.castOrParseValueAsPerType(field.getDataTypeEnum(), lastValMap.get("value"));
		
		if (lastReading == null) {
			return 0;
		}
		if (currentReadingObj instanceof Double) {
			double lastVal =  (double)lastReading;
			if (lastVal == -1) {
				return 0;
			}
			diff = Math.abs((double) currentReadingObj - lastVal);
		}
		else if (currentReadingObj instanceof Long) {
			long lastVal = (long) lastReading;
			if (lastVal == -1) {
				return 0;
			}
			diff = Math.abs((int) currentReadingObj - lastVal);
		}
		else {
			throw new IllegalArgumentException("Flapping is supported only for Number/ Decimal data types");
		}
		return diff;
	}
	
	private List<Map<String, Object>> getFlaps(long ruleId) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getReadingRuleFlapsModule();
		List<FacilioField> fields = FieldFactory.getReadingRuleFlapsFields();
		FacilioField ruleIdField = FieldFactory.getAsMap(fields).get("ruleId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.orderBy("flapTime")
														.andCondition(CriteriaAPI.getCondition(ruleIdField, String.valueOf(ruleId), PickListOperators.IS));
		
		return selectBuilder.get();
	}

	private void updateLastValueForReadingRule(ReadingRuleContext readingRule, ReadingContext record) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Criteria criteria = readingRule.getCriteria();
		Condition condition = criteria.getConditions().get(1);
		long lastValue = new Double(record.getReading(condition.getFieldName()).toString()).longValue();
		WorkflowRuleAPI.updateLastValueInReadingRule(readingRule.getId(), lastValue);
	}
	
	private void executeWorkflowActions(WorkflowRuleContext rule, Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		long ruleId = rule.getId();
		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), ruleId);
		if(actions != null) {
			context.put(FacilioConstants.ContextNames.CURRENT_WORKFLOW_RULE, rule);
			context.put(FacilioConstants.ContextNames.CURRENT_RECORD, record);
			for(ActionContext action : actions)
			{
				action.executeAction(placeHolders, context);
			}
		}
	}
}
