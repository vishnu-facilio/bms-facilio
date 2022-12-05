package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;

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

    @Override
    public void addData() throws Exception {
        super.addData();

        ArrayList<String> apps = new ArrayList<>();
        apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        for(String app : apps){
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderPlannedItemModule = moduleBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
            FacilioField isReservedField = moduleBean.getField("isReserved", FacilioConstants.ContextNames.WO_PLANNED_ITEMS);

            CustomPageWidget pageWidget1 = new CustomPageWidget();
            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField15 = new SummaryWidgetGroupFields();

            groupField11.setName(sysCreatedByField.getName());
            groupField11.setDisplayName(sysCreatedByField.getDisplayName());
            groupField11.setFieldId(sysCreatedByField.getId());
            groupField11.setRowIndex(1);
            groupField11.setColIndex(1);
            groupField11.setColSpan(2);

            groupField12.setName(sysCreatedTimeField.getName());
            groupField12.setDisplayName(sysCreatedTimeField.getDisplayName());
            groupField12.setFieldId(sysCreatedTimeField.getId());
            groupField12.setRowIndex(1);
            groupField12.setColIndex(3);
            groupField12.setColSpan(2);

            groupField13.setName(sysModifiedByField.getName());
            groupField13.setDisplayName(sysModifiedByField.getDisplayName());
            groupField13.setFieldId(sysModifiedByField.getId());
            groupField13.setRowIndex(2);
            groupField13.setColIndex(1);
            groupField13.setColSpan(2);

            groupField14.setName(sysModifiedTimeField.getName());
            groupField14.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField14.setFieldId(sysModifiedTimeField.getId());
            groupField14.setRowIndex(2);
            groupField14.setColIndex(3);
            groupField14.setColSpan(2);

            groupField15.setName(isReservedField.getName());
            groupField15.setDisplayName(isReservedField.getDisplayName());
            groupField15.setFieldId(isReservedField.getId());
            groupField15.setRowIndex(3);
            groupField15.setColIndex(1);
            groupField15.setColSpan(2);

            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);
            groupOneFields.add(groupField14);
            groupOneFields.add(groupField15);


            widgetGroup1.setName("moreDetails");
            widgetGroup1.setDisplayName("More Details");
            widgetGroup1.setColumns(4);
            widgetGroup1.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);

            pageWidget1.setName("plansWidget");
            pageWidget1.setDisplayName("Plans Widget");
            pageWidget1.setModuleId(workOrderPlannedItemModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(app));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }

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
        plannedItemsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> plannedItemsFormFields = new ArrayList<>();
        plannedItemsFormFields.add(new FormField("itemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item type", FormField.Required.REQUIRED,"itemTypes", 1, 1));
        plannedItemsFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.REQUIRED, 2, 1));
        plannedItemsFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 1));
        plannedItemsFormFields.add(new FormField("reservationType", FacilioField.FieldDisplayType.SELECTBOX, "Reservation Type", FormField.Required.OPTIONAL, 4, 1));
        plannedItemsFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 5, 1));
//        FormField totalCost = new FormField("totalCost", FacilioField.FieldDisplayType.NUMBER, "Total Cost", FormField.Required.OPTIONAL, 6, 3);
//        totalCost.setIsDisabled(true);
//        plannedItemsFormFields.add(totalCost);
        plannedItemsForm.setFields(plannedItemsFormFields);
        FormSection section = new FormSection("Default", 1, plannedItemsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedItemsForm.setSections(Collections.singletonList(section));
        plannedItemsForm.setIsSystemForm(true);
        plannedItemsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedItemsForm);
    }
}
