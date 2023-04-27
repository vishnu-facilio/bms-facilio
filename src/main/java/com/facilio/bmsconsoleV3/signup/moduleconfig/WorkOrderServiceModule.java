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

public class WorkOrderServiceModule extends BaseModuleConfig {
    public WorkOrderServiceModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WO_SERVICE);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderService = new ArrayList<FacilioView>();
        workOrderService.add(getAllWorkOrderService().setOrder(order++));
        workOrderService.add(getAllWorkOrderServiceDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderService);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderService() {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderServiceModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Service");
        allView.setModuleName(workOrderServiceModule.getName());
        allView.setSortFields(Collections.singletonList(sortField));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllWorkOrderServiceDetailsView() {
        FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Service Details");
        detailsView.setModuleName(workOrderServiceModule.getName());

        return detailsView;
    }

    @Override
    public void addData() throws Exception {
        super.addData();
        List<String> appLinkNamesForSummaryWidget = new ArrayList<>();
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        for(String appLinkName: appLinkNamesForSummaryWidget) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderServiceModule = moduleBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WO_SERVICE);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WO_SERVICE);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WO_SERVICE);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WO_SERVICE);

            CustomPageWidget pageWidget1 = new CustomPageWidget();
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

            pageWidget1.setName("workorderServicesWidget");
            pageWidget1.setDisplayName("Work Order Services Widget");
            pageWidget1.setModuleId(workOrderServiceModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(appLinkName));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }

    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderServiceModule = modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE);

        FacilioForm workOrderServiceModuleForm = new FacilioForm();
        workOrderServiceModuleForm.setDisplayName("New Work Order Service");
        workOrderServiceModuleForm.setName("default_workorderService_web");
        workOrderServiceModuleForm.setModule(workOrderServiceModule);
        workOrderServiceModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workOrderServiceModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        int seqNum = 0;
        List<FormField> workOrderServiceModuleFormFields = new ArrayList<>();
        workOrderServiceModuleFormFields.add(new FormField("service", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service", FormField.Required.REQUIRED, "Service", ++seqNum, 1));
        workOrderServiceModuleFormFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME,"Start Time", FormField.Required.OPTIONAL, ++seqNum,1));
        workOrderServiceModuleFormFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME,"End Time", FormField.Required.OPTIONAL, ++seqNum,1));
        workOrderServiceModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION,"Duration", FormField.Required.OPTIONAL,"duration", ++seqNum,1));
        workOrderServiceModuleFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, ++seqNum, 1));

        FormSection workOrderServiceModuleFormSection = new FormSection("Default", 1, workOrderServiceModuleFormFields, false);
        workOrderServiceModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        workOrderServiceModuleForm.setSections(Collections.singletonList(workOrderServiceModuleFormSection));
        workOrderServiceModuleForm.setIsSystemForm(true);
        workOrderServiceModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(workOrderServiceModuleForm);

    }
}
