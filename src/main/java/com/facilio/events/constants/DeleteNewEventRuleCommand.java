package com.facilio.events.constants;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

public class DeleteNewEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			EventRuleContext oldRule = EventRulesAPI.getEventRule(id, false);
			if (oldRule != null) {
				context.put(EventConstants.EventContextNames.EVENT_RULE, oldRule);
				
				FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getIdCondition(id, module))
																;
				deleteBuilder.delete();
				deleteChildIds(oldRule);
			}
		}
		return false;
	}

	private void deleteChildIds(EventRuleContext oldRule) throws Exception {
		if(oldRule.getCriteriaId() != -1) {
			CriteriaAPI.deleteCriteria(oldRule.getCriteriaId());
		}
		
		if(oldRule.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(oldRule.getWorkflowId());
		}
		
		if(oldRule.getTransformTemplateId() != -1) {
			TemplateAPI.deleteTemplate(oldRule.getTransformTemplateId());
		}
	}
}
