package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;

import java.util.*;

public class WorkOrderPlannedServicesModule extends BaseModuleConfig {
    public WorkOrderPlannedServicesModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
    }

    @Override
    public void addData() throws Exception {
        super.addData();
//        ArrayList<String> apps = new ArrayList<>();
//        if(!SignupUtil.maintenanceAppSignup()) {
//            apps.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//        }
//        apps.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderPlannedItemModule = moduleBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);

            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WO_PLANNED_SERVICES);

            SummaryWidget pageWidget1 = new SummaryWidget();
            SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();

            SummaryWidgetGroupFields groupField11 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField12 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField13 = new SummaryWidgetGroupFields();
            SummaryWidgetGroupFields groupField14 = new SummaryWidgetGroupFields();

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


            List<SummaryWidgetGroupFields> groupOneFields = new ArrayList<>();
            groupOneFields.add(groupField11);
            groupOneFields.add(groupField12);
            groupOneFields.add(groupField13);
            groupOneFields.add(groupField14);


            widgetGroup1.setName("moreDetails");
            widgetGroup1.setDisplayName("More Details");
            widgetGroup1.setColumns(4);
            widgetGroup1.setFields(groupOneFields);

            List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
            widgetGroupList.add(widgetGroup1);

            pageWidget1.setName("plansWidget");
            pageWidget1.setDisplayName("Plans Widget");
            pageWidget1.setModuleId(workOrderPlannedItemModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(SignupUtil.getSignupApplicationLinkName()));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderPlannedServices = new ArrayList<FacilioView>();
        workOrderPlannedServices.add(getAllWorkOrderPlannedServices().setOrder(order++));
        workOrderPlannedServices.add(getWorkOrderPlannedServicesDetails().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderPlannedServices);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderPlannedServices() {
        FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedServicesModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All WorkOrder Planned Services");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);


        return allView;
    }

    private static FacilioView getWorkOrderPlannedServicesDetails() {
        FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(plannedServicesModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("WorkOrder Planned Service Details");
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
        FacilioModule plannedServicesModule = modBean.getModule(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);

        FacilioForm plannedServicesForm = new FacilioForm();
        plannedServicesForm.setDisplayName("WORK ORDER PLANNED SERVICES");
        plannedServicesForm.setName("default_workOrderPlannedServices_web");
        plannedServicesForm.setModule(plannedServicesModule);
        plannedServicesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        plannedServicesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> plannedServicesFormFields = new ArrayList<>();

        plannedServicesFormFields.add(new FormField("service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service", FormField.Required.REQUIRED,"service", 1, 1));
        plannedServicesFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, 2, 1));
        plannedServicesFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.DECIMAL, "Unit Price", FormField.Required.OPTIONAL, 3, 1));
        plannedServicesFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, 4, 1));
//        FormField totalCost = new FormField("totalCost", FacilioField.FieldDisplayType.DECIMAL, "Total Cost", FormField.Required.OPTIONAL, 5, 3);
//        totalCost.setIsDisabled(true);
//        plannedServicesFormFields.add(totalCost);
        plannedServicesForm.setFields(plannedServicesFormFields);

        FormSection section = new FormSection("Default", 1, plannedServicesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        plannedServicesForm.setSections(Collections.singletonList(section));
        plannedServicesForm.setIsSystemForm(true);
        plannedServicesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(plannedServicesForm);

    }

}
