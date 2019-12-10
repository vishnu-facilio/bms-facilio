package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections.CollectionUtils;

public class GetFormListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Integer> formTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORM_TYPE);
		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes));
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes);
		
		if (dbForms != null) {
			for(Map.Entry<String, FacilioForm> entry :dbForms.entrySet()) {
				forms.put(entry.getKey(), entry.getValue());
			}
		}
		List<FacilioForm> formsList = new ArrayList<>(forms.values());
		formsList.removeIf(form -> form.isHideInList() || (AccountUtil.getCurrentAccount().isFromMobile() && !form.getShowInMobile()));

		List<Long> stateFlowIds = new ArrayList<>();
		for (FacilioForm form : formsList) {
			if (form.getStateFlowId() > 0) {
				stateFlowIds.add(form.getStateFlowId());
			}
		}

		Map<Long, WorkflowRuleContext> stateFlowMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(stateFlowIds)) {
			List<WorkflowRuleContext> stateFlowBaseDetails = WorkflowRuleAPI.getWorkflowRules(stateFlowIds, false, false);
			for (WorkflowRuleContext rule : stateFlowBaseDetails) {
				stateFlowMap.put(rule.getId(), rule);
			}
		}

		context.put(FacilioConstants.ContextNames.FORMS, formsList);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, stateFlowMap);
		return false;
	}

}
