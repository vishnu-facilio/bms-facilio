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

public class WorkOrderPlannedToolsModule extends BaseModuleConfig {
    public WorkOrderPlannedToolsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WO_PLANNED_TOOLS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlannedTools = new ArrayList<FacilioView>();
        workOrderPlannedTools.add(getAllWorkOrderPlannedTools().setOrder(order++));
        workOrderPlannedTools.add(getWorkOrderPlannedToolsDetails().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderPlannedTools);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderPlannedTools() {
        FacilioModule plannedToolsModule = ModuleFactory.getWorkOrderPlannedToolsModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedToolsModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All WorkOrder Planned Tools");
        allView.setSortFields(sortFields);

        return allView;
    }

    private static FacilioView getWorkOrderPlannedToolsDetails() {
        FacilioModule plannedToolsModule = ModuleFactory.getWorkOrderPlannedToolsModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedToolsModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("WorkOrder Planned Tool Details");
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedToolsModule = modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_TOOLS);

        FacilioForm plannedToolsForm = new FacilioForm();
        plannedToolsForm.setDisplayName("WORK ORDER PLANNED TOOLS");
        plannedToolsForm.setName("default_workOrderPlannedTools_web");
        plannedToolsForm.setModule(plannedToolsModule);
        plannedToolsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        plannedToolsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> plannedToolsFormFields = new ArrayList<>();
        plannedToolsFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, 1, 2));
        plannedToolsFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", 2, 2));
        plannedToolsFormFields.add(new FormField("rate", FacilioField.FieldDisplayType.NUMBER, "Rate", FormField.Required.REQUIRED, 4, 3));
        plannedToolsFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.NUMBER, "Duration", FormField.Required.REQUIRED, 5, 2));
        FormField totalCost = new FormField("totalCost", FacilioField.FieldDisplayType.NUMBER, "Total Cost", FormField.Required.REQUIRED, 6, 3);
        totalCost.setIsDisabled(true);
        plannedToolsFormFields.add(totalCost);

        plannedToolsForm.setFields(plannedToolsFormFields);

        FormSection section = new FormSection("Default", 1, plannedToolsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedToolsForm.setSections(Collections.singletonList(section));
        plannedToolsForm.setIsSystemForm(true);
        plannedToolsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedToolsForm);
    }
}
