package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class TermsAndConditionModule extends BaseModuleConfig{
    public TermsAndConditionModule(){
        setModuleName(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> termsAndCondition = new ArrayList<FacilioView>();
        termsAndCondition.add(getAllTermsAndConditionView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
        groupDetails.put("views", termsAndCondition);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTermsAndConditionView() {
        FacilioField name = new FacilioField();
        name.setName("name");
        name.setDataType(FieldType.STRING);
        name.setColumnName("name");
        name.setModule(ModuleFactory.getTermsAndConditionModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All T&C(s)");
        allView.setSortFields(Arrays.asList(new SortField(name, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule termsAndConditionsModule = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);

        FacilioForm termsAndConditionForm = new FacilioForm();
        termsAndConditionForm.setDisplayName("TERMS AND CONDITION");
        termsAndConditionForm.setName("web_default");
        termsAndConditionForm.setModule(termsAndConditionsModule);
        termsAndConditionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        termsAndConditionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> termsAndConditionFormFields = new ArrayList<>();
        termsAndConditionFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        termsAndConditionFormFields.add(new FormField("termType", FacilioField.FieldDisplayType.TEXTBOX, "Term Type", FormField.Required.OPTIONAL, 2, 1));
        termsAndConditionFormFields.add(new FormField("shortDesc", FacilioField.FieldDisplayType.TEXTAREA, "Short Description", FormField.Required.OPTIONAL, 3, 1));
        FormField descField = new FormField("longDesc", FacilioField.FieldDisplayType.TEXTAREA, "Long Description", FormField.Required.OPTIONAL, 4, 1);
        descField.addToConfig("richText", true);
        termsAndConditionFormFields.add(descField);
        termsAndConditionFormFields.add(new FormField("defaultOnPo", FacilioField.FieldDisplayType.DECISION_BOX, "Default On PO", FormField.Required.OPTIONAL, 5, 2));
        termsAndConditionFormFields.add(new FormField("defaultOnQuotation", FacilioField.FieldDisplayType.DECISION_BOX, "Default On Quotation", FormField.Required.OPTIONAL, 6, 2));

        FormSection section = new FormSection("Default", 1, termsAndConditionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        termsAndConditionForm.setSections(Collections.singletonList(section));
        termsAndConditionForm.setIsSystemForm(true);
        termsAndConditionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(termsAndConditionForm);
    }
}
