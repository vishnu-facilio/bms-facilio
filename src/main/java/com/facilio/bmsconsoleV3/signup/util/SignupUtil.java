package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SignupUtil {

        private static Logger LOGGER = LogManager.getLogger(SignupUtil.class.getName());
	public static void addNotesAndAttachmentModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule customNotesModule = new FacilioModule();
        customNotesModule.setName(module.getName() + "notes");
        customNotesModule.setDisplayName(module.getDisplayName() + " Notes");
        customNotesModule.setTableName("Notes");
        customNotesModule.setType(FacilioModule.ModuleType.NOTES);
        modules.add(customNotesModule);


        FacilioModule customAttachmentModule = new FacilioModule();
        customAttachmentModule.setName(module.getName() + "attachments");
        customAttachmentModule.setDisplayName(module.getDisplayName() + " Attachments");
        customAttachmentModule.setTableName("Attachments");
        customAttachmentModule.setType(FacilioModule.ModuleType.ATTACHMENTS);

        modules.add(customAttachmentModule);

        if (modules != null && modules.size() > 0) {
            for (FacilioModule subModule: modules) {
            	AddSubModulesSystemFieldsCommad.addModuleBasedFields(module, subModule);
            }
        }
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();

	}

        /**
         * Helper methods declared to get the different FacilioFields.
         * viz. {@link StringField}, {@link NumberField}, {@link BooleanField}, {@link SystemEnumField}, {@link LookupField}
         */

        /**
         * Helper method to get {@link StringField}
         */
        public static StringField getStringField(FacilioModule module, String name, String displayName, String columnName, FacilioField.FieldDisplayType displayType,
                                           Boolean required, Boolean disabled, Boolean isDefault, Boolean isMainField, Long orgId) {
                StringField stringField = FieldFactory.getField(name, displayName, columnName, module, FieldType.STRING);
                stringField.setDisplayType(displayType);
                stringField.setRequired(required);
                stringField.setDisabled(disabled);
                stringField.setDefault(isDefault);
                stringField.setMainField(isMainField);
                stringField.setOrgId(orgId);
                return stringField;
        }

        /**
         * Helper method to get {@link NumberField}
         */
        public static NumberField getNumberField(FacilioModule module, String name, String displayName, String columnName,
                                           FacilioField.FieldDisplayType displayType, Boolean required, Boolean disabled,
                                           Boolean isDefault, Long orgId) {
                NumberField numberField = FieldFactory.getField(name, displayName, columnName, module, FieldType.NUMBER);
                numberField.setDisplayType(displayType);
                numberField.setRequired(required);
                numberField.setDisabled(disabled);
                numberField.setDefault(isDefault);
                numberField.setOrgId(orgId);
                return numberField;
        }

        /**
         * Helper method to get {@link DateField}
         */
        public static DateField getDateField(FacilioModule module, String name, String displayName, String columnName,
                                                 FacilioField.FieldDisplayType displayType, Boolean required, Boolean disabled,
                                                 Boolean isDefault, Long orgId) {
                DateField dateField = FieldFactory.getField(name, displayName, columnName, module, FieldType.DATE);
                dateField.setDisplayType(displayType);
                dateField.setRequired(required);
                dateField.setDisabled(disabled);
                dateField.setDefault(isDefault);
                dateField.setOrgId(orgId);
                return dateField;
        }

        /**
         * Helper method to get {@link BooleanField}
         */
        public static BooleanField getBooleanField(FacilioModule module, String name, String displayName, String columnName,
                                             FacilioField.FieldDisplayType displayType, Long accessType, Boolean required,
                                             Boolean disabled, Boolean isDefault, Long orgId) {
                BooleanField booleanField = FieldFactory.getField(name, displayName, columnName, module, FieldType.BOOLEAN);
                booleanField.setDisplayType(displayType);
                booleanField.setRequired(required);
                booleanField.setDisabled(disabled);
                booleanField.setDefault(isDefault);
                booleanField.setOrgId(orgId);

                if (accessType != null) {
                        booleanField.setAccessType(accessType);
                }
                return booleanField;
        }

        /**
         * Helper method to get {@link SystemEnumField}
         */
        public static SystemEnumField getSystemEnumField(FacilioModule module, String name, String displayName, String columnName,
                                                   String enumName, FacilioField.FieldDisplayType displayType, Boolean required,
                                                   Boolean disabled, Boolean isDefault, Long orgId) {
                SystemEnumField systemEnumField = FieldFactory.getField(name, displayName, columnName, module, FieldType.SYSTEM_ENUM);
                systemEnumField.setDisplayType(displayType);
                systemEnumField.setRequired(required);
                systemEnumField.setDisabled(disabled);
                systemEnumField.setDefault(isDefault);
                systemEnumField.setOrgId(orgId);
                systemEnumField.setEnumName(enumName);
                return systemEnumField;
        }

        /**
         * Helper method to get {@link LookupField}
         */
        public static LookupField getLookupField(FacilioModule fieldModule, FacilioModule lookUpModule, String name, String displayName,
                                           String columnName, String specialType, FacilioField.FieldDisplayType displayType, Boolean required,
                                           Boolean disabled, Boolean isDefault, Long orgId) {
                return getLookupField(fieldModule, lookUpModule, name, displayName, columnName, specialType, displayType, required, disabled, isDefault, orgId, false);
        }
        public static LookupField getLookupField(FacilioModule fieldModule, FacilioModule lookUpModule, String name, String displayName,
                                           String columnName, String specialType, FacilioField.FieldDisplayType displayType, Boolean required,
                                           Boolean disabled, Boolean isDefault, Long orgId, boolean isMainField) {
                LookupField lookupField = FieldFactory.getField(name, displayName, columnName, fieldModule, FieldType.LOOKUP);
                lookupField.setDisplayType(displayType);
                lookupField.setRequired(required);
                lookupField.setDisabled(disabled);
                lookupField.setDefault(isDefault);
                lookupField.setOrgId(orgId);
                lookupField.setMainField(isMainField);

                if (lookUpModule != null) {
                        lookupField.setLookupModule(lookUpModule);
                        lookupField.setLookupModuleId(lookUpModule.getModuleId());
                }

                if (specialType != null) {
                        lookupField.setSpecialType(specialType);
                }
                return lookupField;
        }

        /**
         * Helper method to add modules via using SystemModuleChain
         *
         * @param modules - List of modules to be added as System Module
         * @throws Exception
         */
        public static void addModules(FacilioModule... modules) throws Exception {
                FacilioChain addModulesChain = TransactionChainFactory.addSystemModuleChain();
                addModulesChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Arrays.asList(modules));
                addModulesChain.execute();
        }
        public static void addFormForModules(List<FacilioForm> forms,List<ApplicationContext> allApplications, String moduleName) throws Exception {

                LOGGER.info("Started adding Forms for module   : " +moduleName);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Map<String,FacilioField> allFields = modBean.getAllFields(moduleName).stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(),(name1, name2) -> { return name1; }));

                Map<String, ApplicationContext> allApplicationMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(allApplications)) {
                        allApplicationMap = allApplications.stream().collect(Collectors.toMap(ApplicationContext::getLinkName, Function.identity()));
                }
                for (FacilioForm form : forms) {
                        List<FormSection> newSections = new ArrayList<>();
                        for (FormSection section : form.getSections()) {
                                List<FormField> formFields = FormsAPI.getFormFieldsFromSections(Collections.singletonList(section));
                                List<FormField> newFormFields = new ArrayList<>();
                                for (FormField formField : formFields) {
                                        if (formField.getName() != null) {
                                                FacilioField field = allFields.get(formField.getName());
                                                if (field != null) {
                                                        formField.setField(field);
                                                        formField.setName(field.getName());
                                                        formField.setFieldId(field.getId());
                                                }
                                                newFormFields.add(formField);
                                        }
                                }
                                section.setFields(newFormFields);
                                newSections.add(section);
                        }
                        form.setSections(newSections);
                        if (form.getAppLinkNamesForForm() == null || form.getAppLinkNamesForForm().isEmpty()) {
                                String linkName = form.getAppLinkName();
                                if(linkName.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && SignupUtil.maintenanceAppSignup()) {
                                        linkName = null;
                                }
                                form.setAppId(allApplicationMap.get(linkName!=null?form.getAppLinkName():SignupUtil.getSignupApplicationLinkName()).getId());
                                form.setAppLinkNamesForForm(Arrays.asList(linkName!=null?form.getAppLinkName():SignupUtil.getSignupApplicationLinkName()));
                        }
                        List<String> appLinkName = new ArrayList<>(form.getAppLinkNamesForForm());
                        if(SignupUtil.maintenanceAppSignup()) {
                                appLinkName.remove(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                        }
                        for (String linkName : appLinkName) {
                                if (Objects.equals(linkName, "wokplace")) {
                                        continue;
                                }
                                if (!Objects.equals(linkName, "newapp")) {
                                        FacilioForm facilioForm = new FacilioForm();
                                        facilioForm = FieldUtil.cloneBean(form, FacilioForm.class);
                                        facilioForm.setAppLinkName(linkName);
                                        if (!Objects.equals(linkName, "service")) {
                                                if(Objects.equals(linkName, "employee")){
                                                        facilioForm.setName(form.getName() + "_" + linkName +"_portal");
                                                }else {
                                                        facilioForm.setName(form.getName() + "_" + linkName);
                                                }
                                        }
                                        facilioForm.setAppId(allApplicationMap.get(linkName).getId());
                                        facilioForm.setType(1);
                                        addForm(facilioForm);
                                }else {
                                        form.setAppLinkName(linkName);
                                        form.setAppId(allApplicationMap.get(linkName).getId());
                                        addForm(form);
                                }
                        }
                }
                LOGGER.info("Completed adding Forms for module : " +moduleName);
        }
        private static void addForm(FacilioForm form) throws  Exception{
                form.setIsSystemForm(true);
                FacilioChain newForm = TransactionChainFactory.getAddFormCommand();
                FacilioContext context=newForm.getContext();
                FacilioModule module = form.getModule();
                context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
                context.put(FacilioConstants.ContextNames.FORM,form);
                newForm.execute();
                if(form.getDefaultFormRules()!=null) {
                        addDefaultFormRule(form);
                }
        }
        private static void addDefaultFormRule(FacilioForm form) throws  Exception{
                FacilioChain defaultFormRuleChain = TransactionChainFactory.getAddDefaultFormRule();
                FacilioContext context= defaultFormRuleChain.getContext();
                context.put(FacilioConstants.ContextNames.FORM,form);
                defaultFormRuleChain.execute();
        }

        public static boolean maintenanceAppSignup() {

                try {
                        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MAINTENANCE_APP_SIGNUP)) {
                                return true;
                        }
                } catch(Exception e) {
                        LOGGER.info("Unable to check feature license for maintenance app signup");
                }
                return false;
        }

        public static String getSignupApplicationLinkName () {
                if(maintenanceAppSignup()) {
                        return FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP;
                }
                return FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
        }

        /**
         * Creates a FacilioStatus for Stateflow transitions
         *
         * @param module Facilio Module
         * @param status Status name
         * @param statusType Status type
         * @param displayName Display Name of Status
         * @return FacilioStatus state
         * **/
        public static FacilioStatus createUntimedFacilioStatus(FacilioModule module, String status, String displayName,
                                                               FacilioStatus.StatusType statusType) throws Exception {
                FacilioStatus statusObj = new FacilioStatus();
                statusObj.setStatus(status);
                statusObj.setDisplayName(displayName);
                statusObj.setTypeCode(statusType.getIntVal());
                statusObj.setTimerEnabled(false);
                TicketAPI.addStatus(statusObj, module);
                return statusObj;
        }
        /**
         * Creates a Stateflow Transition without actions & criteria
         *
         * @param module Owning module
         * @param name Name of transition
         * @param sf Owning Stateflow
         * @param from From State
         * @param to To State
         * @param transitionType  type of Transition
         * @return StateflowTransition
         * **/
        public static StateflowTransitionContext createStateflowTransition(FacilioModule module,
                                                                           StateFlowRuleContext sf,
                                                                           String name,
                                                                           FacilioStatus from,
                                                                           FacilioStatus to,
                                                                           AbstractStateTransitionRuleContext.TransitionType transitionType,
                List<ActionContext> actions) throws Exception {

                StateflowTransitionContext stateFlowTransitionContext = new StateflowTransitionContext();
                stateFlowTransitionContext.setName(name);
                stateFlowTransitionContext.setModule(module);
                stateFlowTransitionContext.setModuleId(module.getModuleId());
                stateFlowTransitionContext.setActivityType(EventType.STATE_TRANSITION);
                stateFlowTransitionContext.setActions(actions);
                stateFlowTransitionContext.setExecutionOrder(1);
                stateFlowTransitionContext.setButtonType(1);
                stateFlowTransitionContext.setFromStateId(from.getId());
                stateFlowTransitionContext.setToStateId(to.getId());
                stateFlowTransitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
                stateFlowTransitionContext.setType(transitionType);
                stateFlowTransitionContext.setStateFlowId(sf.getId());

                FacilioChain chain = TransactionChainFactory.getAddOrUpdateStateFlowTransition();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlowTransitionContext);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                chain.execute();
                return stateFlowTransitionContext;
        }
}
