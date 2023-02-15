package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.modules.FieldUtil;
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
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetFormListCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetFormListCommand.class);
	long appId = -1;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);

		ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
		appId = app.getId();

		List<String> appLinkNames = Collections.singletonList(app.getLinkName());

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		

		Boolean fetchExtendedModuleForms = (Boolean) context.get(ContextNames.FETCH_EXTENDED_MODULE_FORMS);
		Boolean fetchDisabledForms = (Boolean) context.get(ContextNames.FETCH_DISABLED_FORMS);
		Boolean fetchHiddenForms = (Boolean) context.getOrDefault(ContextNames.FETCH_HIDDEN_FORMS, false);


		List<FacilioForm> formsList = getFormsList(moduleName, fetchExtendedModuleForms, fetchDisabledForms, appLinkNames);
		if(!fetchHiddenForms) {
			formsList.removeIf(form -> form.isHideInList() || (AccountUtil.getCurrentAccount().isFromMobile() && form.getShowInMobile() != null && !form.getShowInMobile()));
		}

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

	private List<FacilioForm> getFormsList(String moduleName, Boolean fetchExtendedModuleForms, Boolean fetchDisabledForms,List<String> appLinkNames) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Map<String, FacilioForm> forms = new LinkedHashMap<>(FormFactory.getForms(moduleName, appLinkNames));
		if (!forms.isEmpty()) {
			for(Map.Entry<String, FacilioForm> entry :forms.entrySet()) {
				entry.getValue().setModule(module);
			}
		}
		else {
			forms = new LinkedHashMap<>();
		}

		Map<String, FacilioForm> extendedForms = new LinkedHashMap<>();
		if (module.getExtendModule() != null && fetchExtendedModuleForms != null && fetchExtendedModuleForms) {
			Map<String, FacilioForm> extendedFactoryForms = FormFactory.getForms(module.getExtendModule().getName(), appLinkNames);
			if (!extendedFactoryForms.isEmpty()) {
				for(Map.Entry<String, FacilioForm> entry :extendedFactoryForms.entrySet()) {
					entry.getValue().setModule(module.getExtendModule());
					extendedForms.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap(moduleName, fetchExtendedModuleForms, fetchDisabledForms, appId);
		if(!forms.isEmpty() ){
			for(String formName : forms.keySet()){
				if(dbForms != null && !dbForms.isEmpty()){
					Set<String> dbFormsName = dbForms.keySet();
					if(!dbFormsName.contains(formName)){
						LOGGER.info("formFactoryTracking formName : "+ formName +"And moduleName : "+ moduleName  + "list command");
					}
				}
				else {
					LOGGER.info("formFactoryTracking formName : "+ formName +"And moduleName : "+ moduleName  + "list command");
				}
			}
		}
		if (dbForms != null) {

			Map<Long,FacilioModule> moduleIdVsModuleMap = new HashMap<>();
			for(Map.Entry<String, FacilioForm> entry :dbForms.entrySet()) {
				FacilioForm dbForm = entry.getValue();
				if (dbForm.getModuleId() > 0) {
					FacilioModule formModule;
					if(moduleIdVsModuleMap.containsKey(dbForm.getModuleId())){
						formModule = moduleIdVsModuleMap.get(dbForm.getModuleId());
						dbForm.setModule(formModule);
					}else {
						formModule = modBean.getModule(dbForm.getModuleId());
						FacilioModule newFormModule = FieldUtil.cloneBean(formModule,FacilioModule.class);
						newFormModule.setFields(null);
						moduleIdVsModuleMap.put(dbForm.getModuleId(), newFormModule);
						dbForm.setModule(newFormModule);
					}

					if (formModule.getName().equals(moduleName)) {
						forms.put(entry.getKey(), entry.getValue());
					}
					else {
						extendedForms.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		
		if (!extendedForms.isEmpty()) {
			forms.putAll(extendedForms);
		}
		
		if (forms.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return new ArrayList<>(forms.values());
	}

}
