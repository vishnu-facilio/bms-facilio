package com.facilio.bmsconsoleV3.signup.fsmApp;

        import com.facilio.beans.ModuleBean;
        import com.facilio.bmsconsole.context.ApplicationContext;
        import com.facilio.bmsconsole.forms.FacilioForm;
        import com.facilio.bmsconsole.forms.FormFactory;
        import com.facilio.bmsconsole.forms.FormField;
        import com.facilio.bmsconsole.forms.FormSection;
        import com.facilio.bmsconsole.util.ApplicationApi;
        import com.facilio.bmsconsole.util.FormsAPI;
        import com.facilio.bmsconsoleV3.commands.AddSignupDataCommandV3;
        import com.facilio.bmsconsoleV3.signup.induction.AddInductionModules;
        import com.facilio.bmsconsoleV3.signup.inspection.AddInspectionModules;
        import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
        import com.facilio.bmsconsoleV3.util.V3ModuleAPI;
        import com.facilio.command.FacilioCommand;
        import com.facilio.constants.FacilioConstants;
        import com.facilio.fw.BeanFactory;
        import com.facilio.modules.FieldUtil;
        import com.facilio.modules.fields.FacilioField;
        import org.apache.commons.chain.Context;
        import org.apache.commons.collections4.CollectionUtils;
        import java.util.*;
public class AddFsmApplicationDefaultForms extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
//        if(!SignupUtil.fsmAppSignup()) {
            List<String> excludeForms = Arrays.asList("multi_web_pm");
            ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
            ApplicationContext mainApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            if (!AddSignupDataCommandV3.createForms) {
                for (String moduleName : V3ModuleAPI.getSystemModuleNamesForApp(fsm.getLinkName())) {
                    Map<String, FacilioForm> formsMap = FormFactory.getForms(moduleName, Collections.singletonList(mainApp.getLinkName()));
                    for (FacilioForm facilioform : formsMap.values()) {
                        FacilioForm defaultForm = FormFactory.getForm(moduleName, facilioform.getName());
                        if (!excludeForms.contains(defaultForm.getName())) {
                            FacilioForm form = FieldUtil.cloneBean(defaultForm, FacilioForm.class);
                            form.setName(defaultForm.getName() + "fsm");
                            form.setAppLinkName(fsm.getLinkName());
                            form.setAppId(fsm.getId());
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
            }
//        }
        return false;
    }
}