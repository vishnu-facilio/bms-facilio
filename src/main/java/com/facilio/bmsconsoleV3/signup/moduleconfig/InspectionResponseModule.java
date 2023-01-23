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

public class InspectionResponseModule extends BaseModuleConfig{
    public InspectionResponseModule(){
        setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inspectionResponse = new ArrayList<FacilioView>();
        inspectionResponse.add(getAllInspectionResponseViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Inspection.INSPECTION_RESPONSE);
        groupDetails.put("views", inspectionResponse);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInspectionResponseViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Responses.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inspections");
        allView.setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inspectionResponseModule = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

        FacilioForm inspectionForm = new FacilioForm();
        inspectionForm.setName("default_"+FacilioConstants.Inspection.INSPECTION_RESPONSE+"_web");
        inspectionForm.setModule(inspectionResponseModule);
        inspectionForm.setDisplayName("Standard");
        inspectionForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        inspectionForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inspectionForm.setShowInWeb(true);

        List<FormField> inspectionFormFields = new ArrayList<>();
        int i = 1;
        inspectionFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.OPTIONAL, i++, 1));
        inspectionFormFields.add(new FormField("parent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent", FormField.Required.OPTIONAL, i++, 1));
        inspectionFormFields.add(new FormField("createdTime", FacilioField.FieldDisplayType.DATETIME, "Created Time", FormField.Required.REQUIRED,  "building",i++, 2));
        inspectionFormFields.add(new FormField("scheduledWorkStart", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start", FormField.Required.OPTIONAL, "site",i++, 3));
        inspectionFormFields.add(new FormField("scheduledWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Scheduled End", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("actualWorkStart", FacilioField.FieldDisplayType.DATETIME, "Actual Start", FormField.Required.OPTIONAL, i++, 3));
        inspectionFormFields.add(new FormField("actualWorkEnd", FacilioField.FieldDisplayType.DATETIME, "Actual End", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("actualWorkDuration", FacilioField.FieldDisplayType.DATETIME, "Actual Duration", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("status", FacilioField.FieldDisplayType.DECISION_BOX, "Response Status", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("sourceType", FacilioField.FieldDisplayType.DECISION_BOX, "Source", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Space/Asset", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("assignedTo", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Assigned To", FormField.Required.OPTIONAL, i++, 2));
        inspectionFormFields.add(new FormField("assignmentGroup", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Team", FormField.Required.OPTIONAL, i++, 2));
//        inspectionForm.setFields(inspectionFormFields);

        FormSection section = new FormSection("Default", 1, inspectionFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inspectionForm.setSections(Collections.singletonList(section));
        inspectionForm.setIsSystemForm(true);
        inspectionForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inspectionForm);
    }
}
