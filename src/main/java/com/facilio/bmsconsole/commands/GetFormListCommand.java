package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetFormListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
        
        ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (app == null) {
			app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		}
        appId = app.getId();
        List<String> appLinkNames = new ArrayList<>(); 
        appLinkNames.add(app.getLinkName());  
		Boolean fetchExtendedModuleForms = (Boolean) context.get(ContextNames.FETCH_EXTENDED_MODULE_FORMS);
		
		Boolean fetchDisabledForms = (Boolean) context.get(ContextNames.FETCH_DISABLED_FORMS);
		
		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), appLinkNames));
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap((String)context.get(FacilioConstants.ContextNames.MODULE_NAME), fetchExtendedModuleForms, fetchDisabledForms, appId);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule((String)context.get(FacilioConstants.ContextNames.MODULE_NAME));
		if (forms != null) {
			for(Map.Entry<String, FacilioForm> entry :forms.entrySet()) {
				entry.getValue().setModule(module);
				forms.put(entry.getKey(), entry.getValue());
			}
		}
		if (module.getExtendModule() != null && fetchExtendedModuleForms != null && fetchExtendedModuleForms) {
			Map<String, FacilioForm> extendedModuleForms = new LinkedHashMap<>(FormFactory.getForms(module.getExtendModule().getName(), appLinkNames));
			if (extendedModuleForms != null) {
				for(Map.Entry<String, FacilioForm> entry :extendedModuleForms.entrySet()) {
					entry.getValue().setModule(module.getExtendModule());
					forms.put(entry.getKey(), entry.getValue());
				}
			}
		}
		if (dbForms != null) { 
			for(Map.Entry<String, FacilioForm> entry :dbForms.entrySet()) {
				if (entry.getValue().getModuleId() > 0) {
					FacilioModule formModule = modBean.getModule(entry.getValue().getModuleId());
					entry.getValue().setModule(formModule);
				}
				forms.put(entry.getKey(), entry.getValue());
			}
		}
		List<FacilioForm> formsList = new ArrayList<>(forms.values());
		formsList.removeIf(form -> form.isHideInList() || (AccountUtil.getCurrentAccount().isFromMobile() && form.getShowInMobile() != null && !form.getShowInMobile()));

		List<Long> stateFlowIds = new ArrayList<>();
		for (FacilioForm form : formsList) {
			if (form.getShowInMobile() == null) {
				form.setShowInMobile(true);
			}
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
