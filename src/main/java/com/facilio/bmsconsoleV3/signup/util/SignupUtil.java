package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddSubModulesSystemFieldsCommad;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SignupUtil {


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
                        form.setIsSystemForm(true);
                        if (form.getAppLinkNamesForForm() == null || form.getAppLinkNamesForForm().isEmpty()) {
                                form.setAppId(allApplicationMap.get(form.getAppLinkName()!=null?form.getAppLinkName():FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
                                form.setAppLinkNamesForForm(Arrays.asList(form.getAppLinkName()!=null?form.getAppLinkName():FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
                        }
                        for (String linkName : form.getAppLinkNamesForForm()) {
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
        }
        public static void addForm(FacilioForm form) throws  Exception{
                FacilioChain newForm = TransactionChainFactory.getAddFormCommand();
                FacilioContext context=newForm.getContext();
                FacilioModule module = form.getModule();
                context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
                context.put(FacilioConstants.ContextNames.FORM,form);
                newForm.execute();
        }
}
