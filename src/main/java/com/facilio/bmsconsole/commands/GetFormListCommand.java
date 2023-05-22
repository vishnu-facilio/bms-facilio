package com.facilio.bmsconsole.commands;

import java.util.*;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
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
import org.apache.commons.collections4.MapUtils;
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

		Boolean skipTemplatePermission = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_TEMPLATE_PERMISSION,false);
		Boolean fetchExtendedModuleForms = (Boolean) context.getOrDefault(ContextNames.FETCH_EXTENDED_MODULE_FORMS,false);
		Boolean fetchDisabledForms = (Boolean) context.getOrDefault(ContextNames.FETCH_DISABLED_FORMS,false);
		Boolean fetchHiddenForms = (Boolean) context.getOrDefault(ContextNames.FETCH_HIDDEN_FORMS, false);


		List<FacilioForm> formsList = getFormsList(moduleName, fetchExtendedModuleForms, fetchDisabledForms, appLinkNames, skipTemplatePermission);
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
		Map<Long, String> stateFlowMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(stateFlowIds)) {
			List<WorkflowRuleContext> stateFlowBaseDetails = WorkflowRuleAPI.getWorkflowRules(stateFlowIds, false, false);
			for (WorkflowRuleContext rule : stateFlowBaseDetails) {
				stateFlowMap.put(rule.getId(), rule.getName());
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

	private List<FacilioForm> getFormsList(String moduleName, Boolean fetchExtendedModuleForms, Boolean fetchDisabledForms,List<String> appLinkNames,Boolean skipTemplatePermission) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Map<String, FacilioForm> forms = new LinkedHashMap<>();

		if(!skipTemplatePermission){
			forms = new LinkedHashMap<>(FormFactory.getForms(moduleName, appLinkNames));

			boolean isMigratedApp = false;
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			Map<String,Object> migratedAppStatus = CommonCommandUtil.getOrgInfo(orgId,"isMigratedApp");
			if(MapUtils.isNotEmpty(migratedAppStatus)){
				Object value = migratedAppStatus.getOrDefault("value",false);
				isMigratedApp =  FacilioUtil.parseBoolean(value);
			}

			if(isMigratedApp){
				ApplicationContext applicationContext = ApplicationApi.getApplicationForId(appId);
				if(applicationContext!=null && Objects.equals(applicationContext.getLinkName(), FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)){
					List<String> newAppLinkName = new ArrayList<>();
					newAppLinkName.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
					forms = new LinkedHashMap<>(FormFactory.getForms(moduleName, newAppLinkName));
				}
			}

		}


		if (!forms.isEmpty()) {
			for(Map.Entry<String, FacilioForm> entry :forms.entrySet()) {
				entry.getValue().setModule(module);
			}
		}
		else {
			forms = new LinkedHashMap<>();
		}

		Map<String, FacilioForm> extendedForms = new LinkedHashMap<>();
		if (module.getExtendModule() != null && fetchExtendedModuleForms != null && fetchExtendedModuleForms && !skipTemplatePermission) {
			Map<String, FacilioForm> extendedFactoryForms = FormFactory.getForms(module.getExtendModule().getName(), appLinkNames);
			if (!extendedFactoryForms.isEmpty()) {
				for(Map.Entry<String, FacilioForm> entry :extendedFactoryForms.entrySet()) {
					entry.getValue().setModule(module.getExtendModule());
					extendedForms.put(entry.getKey(), entry.getValue());
				}
			}
		}

		Map<String, FacilioForm> dbForms=FormsAPI.getFormsAsMap(moduleName, fetchExtendedModuleForms, fetchDisabledForms, appId, skipTemplatePermission);
		List<String> formsFromFactory = new ArrayList<>();
		if(!forms.isEmpty() ){
			for(String formName : forms.keySet()){
				if(dbForms != null && !dbForms.isEmpty()){
					Set<String> dbFormsName = dbForms.keySet();
					if(!dbFormsName.contains(formName)){
						LOGGER.info("formFactoryTracking formName : "+ formName +"And moduleName : "+ moduleName  + "list command");
						formsFromFactory.add(formName);
					}
				}
				else {
					LOGGER.info("formFactoryTracking formName : "+ formName +"And moduleName : "+ moduleName  + "list command");
					formsFromFactory.add(formName);
				}
			}
			if(CollectionUtils.isNotEmpty(formsFromFactory)) {
				Map<String, Long> formFactoryFormIdVsName = FormsAPI.getFormIdsFromName(formsFromFactory, module.getModuleId(),appId);

				for(String formName : formsFromFactory){
					if(formFactoryFormIdVsName.get(formName)!=null && formFactoryFormIdVsName.get(formName)>0){
						forms.remove(formName);
					}
					else{
						LOGGER.info("formFactoryTracking formFactory form : " + formName + " moduleName : "+ moduleName +" don't has a DB entry ");
					}
				}
			}
		}

		if (MapUtils.isNotEmpty(dbForms)) {

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
