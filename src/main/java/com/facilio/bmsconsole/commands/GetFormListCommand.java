package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;

import org.apache.commons.collections.CollectionUtils;

public class GetFormListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Integer> formTypes = (List<Integer>) context.get(FacilioConstants.ContextNames.FORM_TYPE);
		Boolean fetchExtendedModuleForms = (Boolean) context.get(ContextNames.FETCH_EXTENDED_MODULE_FORMS);
		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes));
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), formTypes, fetchExtendedModuleForms);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule((String)context.get(FacilioConstants.ContextNames.MODULE_NAME));
		if (module.getExtendModule() != null && fetchExtendedModuleForms != null && fetchExtendedModuleForms) {
			Map<String, FacilioForm> extendedModuleForms = new LinkedHashMap<>(FormFactory.getForms(module.getExtendModule().getName(), formTypes));
			if (extendedModuleForms != null) {
				for(Map.Entry<String, FacilioForm> entry :extendedModuleForms.entrySet()) {
					forms.put(entry.getKey(), entry.getValue());
				}
			}
		}
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
		
		List<FacilioForm> siteAssociatedFormsList = new ArrayList<>();
		List<FacilioForm> siteDisassociatedFormsList = new ArrayList<>();
		
		for (FacilioForm form: formsList) {
				if (form.getSiteIds() != null && form.getSiteIds().size() > 0 ) {
					siteAssociatedFormsList.add(form);
				}
				else {
					siteDisassociatedFormsList.add(form);
				}
		}
		if (siteDisassociatedFormsList  != null && siteDisassociatedFormsList.size() > 0) {
			siteAssociatedFormsList.addAll(siteDisassociatedFormsList);
		}
		if (siteAssociatedFormsList  != null && siteAssociatedFormsList.size() > 0) {
			formsList = new ArrayList<>();
			formsList = siteAssociatedFormsList;				
		}
		context.put(FacilioConstants.ContextNames.FORMS, formsList);
		context.put(FacilioConstants.ContextNames.STATE_FLOW_LIST, stateFlowMap);
		return false;
	}

}
