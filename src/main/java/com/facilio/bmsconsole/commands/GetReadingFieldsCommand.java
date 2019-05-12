package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class GetReadingFieldsCommand implements Command {

	private static Logger log = LogManager.getLogger(GetReadingFieldsCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
		context.put(FacilioConstants.ContextNames.VALIDATION_RULES, fieldVsRules);
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(FacilioModule reading : readings) {
				Boolean excludeEmptyFields = (Boolean) context.get(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS);
				Long parentId = excludeEmptyFields != null && excludeEmptyFields ? (Long) context.get(FacilioConstants.ContextNames.PARENT_ID) : null;
				if(AccountUtil.getCurrentOrg().getId() == 191l) {
					log.error("Module Name with fields ---- "+reading.getName() +" Fields -- "+modBean.getAllFields(reading.getName()));
				}
				List<FacilioField> dataPoints = ReadingsAPI.excludeDefaultAndEmptyReadingFields(modBean.getAllFields(reading.getName()),parentId);
				
				StringJoiner j = new StringJoiner(",");
				dataPoints.stream().forEach(f -> j.add(String.valueOf(f.getFieldId())));
				
				Criteria criteria = new Criteria();
		        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", j.toString(), NumberOperators.EQUALS));
		        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
		        List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
		       
		        if (readingRules != null && !readingRules.isEmpty()) {
		        	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
		            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
		        	for (ReadingRuleContext rule:  readingRules) {
		        		if (rule.getReadingFieldId() != -1) { 
		        			List<ReadingRuleContext> rules = fieldVsRules.get(rule.getReadingFieldId());
		        			if (rules == null) {
		        				rules = new ArrayList<>();
		        				fieldVsRules.put(rule.getReadingFieldId(), rules);
		        			}
		        			rules.add(rule);
		        		}
		        		long workflowId = rule.getWorkflowId();
		        		if (workflowId != -1) {
		        			rule.setWorkflow(workflowMap.get(workflowId));
		        		}
		        		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(rule.getId());
		        		rule.setActions(actions);
		        	}
		        }
				reading.setFields(dataPoints);
			}
		}
		return false;
	}
	
	

}
