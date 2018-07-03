package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class GetReadingFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(FacilioModule reading : readings) {
				Boolean excludeEmptyFields = (Boolean) context.get(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS);
				Long parentId = excludeEmptyFields != null && excludeEmptyFields ? (Long) context.get(FacilioConstants.ContextNames.PARENT_ID) : null;
				List<FacilioField> dataPoints = ReadingsAPI.excludeDefaultAndEmptyReadingFields(modBean.getAllFields(reading.getName()),parentId);
				
				StringJoiner j = new StringJoiner(",");
				dataPoints.stream().forEach(f -> j.add(String.valueOf(f.getFieldId())));
				
				Criteria criteria = new Criteria();
		        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", j.toString(), NumberOperators.EQUALS));
		        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
		        List<ReadingRuleContext> readingRules = WorkflowRuleAPI.getReadingRules(criteria);
		       
		        if (readingRules != null && !readingRules.isEmpty()) {
		        	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
		            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
		            Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
		            
		        	for (ReadingRuleContext r:  readingRules) {
		        		if (r.getReadingFieldId() != -1) { 
		        			List<ReadingRuleContext> rules = fieldVsRules.get(r.getReadingFieldId());
		        			if (rules == null) {
		        				rules = new ArrayList<>();
		        				fieldVsRules.put(r.getReadingFieldId(), rules);
		        			}
		        			rules.add(r);
		        		}
		        		long workflowId = r.getWorkflowId();
		        		if (workflowId != -1) {
		        			r.setWorkflow(workflowMap.get(workflowId));
		        		}
		        		List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), r.getId());
		        		r.setActions(actions);
		        	}
		        	dataPoints.forEach(t -> t.setReadingRules(fieldVsRules.get(t.getFieldId())));
		        }
				reading.setFields(dataPoints);
			}
		}
		return false;
	}
	
	

}
