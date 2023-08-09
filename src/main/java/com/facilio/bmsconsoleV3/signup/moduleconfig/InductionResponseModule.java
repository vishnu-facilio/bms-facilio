package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class InductionResponseModule extends BaseModuleConfig{
    public InductionResponseModule(){
        setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inductionResponse = new ArrayList<FacilioView>();
        inductionResponse.add(getAllInductionResponseViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Induction.INDUCTION_RESPONSE);
        groupDetails.put("views", inductionResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInductionResponseViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Responses.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inductions");
        allView.setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
        allView.setSortFields(sortFields);

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
        FacilioModule inductionResponseModule = modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE);

        FacilioForm inductionForm = new FacilioForm();
        inductionForm.setName("default_" + FacilioConstants.Induction.INDUCTION_RESPONSE + "_web");
        inductionForm.setModule(inductionResponseModule);
        inductionForm.setDisplayName("Standard");
        inductionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        inductionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inductionForm.setShowInWeb(true);

        List<FormField> inductionFormFields = new ArrayList<>();
        int i = 1;
        inductionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
        inductionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED, "building", i++, 2));
        inductionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site", i++, 3));
        inductionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
        inductionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
        inductionFormFields.add(new FormField("people", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
//        inductionForm.setFields(inductionFormFields);

        FormSection section = new FormSection("Default", 1, inductionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inductionForm.setSections(Collections.singletonList(section));
        inductionForm.setIsSystemForm(true);
        inductionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inductionForm);
    }
}
