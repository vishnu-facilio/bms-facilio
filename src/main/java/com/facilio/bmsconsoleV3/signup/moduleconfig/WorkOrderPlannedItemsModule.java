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

public class WorkOrderPlannedItemsModule extends BaseModuleConfig {
    public WorkOrderPlannedItemsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlannedItems = new ArrayList<FacilioView>();
        workOrderPlannedItems.add(getAllWorkOrderPlannedItems().setOrder(order++));
        workOrderPlannedItems.add(getWorkOrderPlannedItemsDetails().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderPlannedItems);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderPlannedItems() {
        FacilioModule plannedItemsModule = ModuleFactory.getWorkOrderPlannedItemsModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedItemsModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All WorkOrder Planned Items");
        allView.setSortFields(sortFields);

        return allView;
    }


    private static FacilioView getWorkOrderPlannedItemsDetails() {
        FacilioModule plannedItemsModule = ModuleFactory.getWorkOrderPlannedItemsModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedItemsModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("WorkOrder Planned Item Details");
        allView.setSortFields(sortFields);

        return allView;

    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedItemsModule = modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);

        FacilioForm plannedItemsForm = new FacilioForm();
        plannedItemsForm.setDisplayName("WORK ORDER PLANNED ITEMS");
        plannedItemsForm.setName("default_workOrderPlannedItems_web");
        plannedItemsForm.setModule(plannedItemsModule);
        plannedItemsForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        plannedItemsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> plannedItemsFormFields = new ArrayList<>();
        plannedItemsFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, 1, 2));
        plannedItemsFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", 2, 2));
        plannedItemsFormFields.add(new FormField("reservationType", FacilioField.FieldDisplayType.SELECTBOX, "Reservation Type", FormField.Required.REQUIRED, 3, 3));
        plannedItemsFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.REQUIRED, 4, 2));
        FormField totalCost = new FormField("totalCost", FacilioField.FieldDisplayType.NUMBER, "Total Cost", FormField.Required.REQUIRED, 5, 3);
        totalCost.setIsDisabled(true);
        plannedItemsFormFields.add(totalCost);


        FormSection section = new FormSection("Default", 1, plannedItemsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedItemsForm.setSections(Collections.singletonList(section));
        plannedItemsForm.setIsSystemForm(true);
        plannedItemsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedItemsForm);
    }
}
