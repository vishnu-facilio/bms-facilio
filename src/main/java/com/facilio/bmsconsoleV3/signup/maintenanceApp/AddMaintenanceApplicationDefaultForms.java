package com.facilio.bmsconsoleV3.signup.maintenanceApp;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.induction.AddInductionModules;
import com.facilio.bmsconsoleV3.signup.inspection.AddInspectionModules;
import com.facilio.bmsconsoleV3.util.V3ModuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.*;
public class AddMaintenanceApplicationDefaultForms extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<String> excludeForms = Arrays.asList("multi_web_pm");
        ApplicationContext maintenance = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        ApplicationContext mainApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (String moduleName : V3ModuleAPI.getSystemModuleNamesForApp(maintenance.getLinkName())) {
            Map<String, FacilioForm> formsMap = FormFactory.getForms(moduleName, Collections.singletonList(mainApp.getLinkName()));
            for (FacilioForm facilioform : formsMap.values()) {
                FacilioForm defaultForm = FormFactory.getForm(moduleName, facilioform.getName());
                if (!excludeForms.contains(defaultForm.getName())) {
                    FacilioForm form = FieldUtil.cloneBean(defaultForm,FacilioForm.class);
                    form.setName(defaultForm.getName() + "_maintenance");
                    form.setAppLinkName(maintenance.getLinkName());
                    form.setAppId(maintenance.getId());
                    if (CollectionUtils.isNotEmpty(form.getSections())) {
                        List<FormSection> newSections = new ArrayList<>();
                        for (FormSection section : form.getSections()) {
                            List<FormField> formFields = FormsAPI.getFormFieldsFromSections(Collections.singletonList(section));
                            List<FormField> newFormFields = new ArrayList<>();
                            for (FormField formField : formFields) {
                                if (formField.getName() != null) {
                                    FacilioField field = modBean.getField(formField.getName(), moduleName);
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
                    } else if (CollectionUtils.isNotEmpty(form.getFields())) {
                        List<FormField> newFormFields = new ArrayList<>();
                        for (FormField formField : form.getFields()) {
                            if (formField.getName() != null) {
                                FacilioField field = modBean.getField(formField.getName(), moduleName);
                                if (field != null) {
                                    formField.setField(field);
                                    formField.setName(field.getName());
                                    formField.setFieldId(field.getId());
                                }
                                newFormFields.add(formField);
                            }
                        }
                        form.setFields(newFormFields);
                    }
                    form.setShowInMobile(null);
                    FormsAPI.createForm(form, modBean.getModule(moduleName));
                }
            }
        }
        AddInductionModules inductionModules = new AddInductionModules();
        inductionModules.addDefaultFormForInductionTemplateMaintenanceApp(modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE));
        AddInspectionModules inspectionModules = new AddInspectionModules();
        inspectionModules.addDefaultFormForInspectionTemplateMaintenanceApp(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE));
        return false;
    }
}