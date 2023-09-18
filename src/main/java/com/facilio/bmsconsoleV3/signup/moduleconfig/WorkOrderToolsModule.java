package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
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

public class WorkOrderToolsModule extends BaseModuleConfig{
    public WorkOrderToolsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_TOOLS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderTools = new ArrayList<FacilioView>();
        workOrderTools.add(getAllWorkOrderTools().setOrder(order++));
        workOrderTools.add(getAllWorkOrderToolsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderTools);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderTools() {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrderToolsModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Tools");
        allView.setModuleName(workOrderToolsModule.getName());
        allView.setSortFields(Collections.singletonList(sortField));

        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("tool","Tool"));
        viewFields.add(new ViewField("storeRoom","StoreRoom"));
        viewFields.add(new ViewField("quantity","Quantity"));
        viewFields.add(new ViewField("duration","Duration"));
        viewFields.add(new ViewField("rate","Rate"));
        viewFields.add(new ViewField("cost","Total Cost"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllWorkOrderToolsDetailsView() {
        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Tools Details");
        detailsView.setModuleName(workOrderToolsModule.getName());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        detailsView.setAppLinkNames(appLinkNames);

        return detailsView;
    }

    @Override
    public void addData() throws Exception {
        super.addData();
//        List<String> appLinkNamesForSummaryWidget = new ArrayList<>();
//        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workOrderToolModule = moduleBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.WORKORDER_TOOLS);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.WORKORDER_TOOLS);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.WORKORDER_TOOLS);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.WO_SERVICE);

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

            groupField13.setName(sysModifiedTimeField.getName());
            groupField13.setDisplayName(sysModifiedTimeField.getDisplayName());
            groupField13.setFieldId(sysModifiedTimeField.getId());
            groupField13.setRowIndex(2);
            groupField13.setColIndex(1);
            groupField13.setColSpan(2);

            groupField14.setName(sysModifiedByField.getName());
            groupField14.setDisplayName(sysModifiedByField.getDisplayName());
            groupField14.setFieldId(sysModifiedByField.getId());
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

            pageWidget1.setName("workorderToolsWidget");
            pageWidget1.setDisplayName("Work Order Tools Widget");
            pageWidget1.setModuleId(workOrderToolModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(SignupUtil.getSignupApplicationLinkName()));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);

        FacilioForm workOrderToolsModuleForm = new FacilioForm();
        workOrderToolsModuleForm.setDisplayName("New Work Order Tool");
        workOrderToolsModuleForm.setName("default_workorderTool_web");
        workOrderToolsModuleForm.setModule(workOrderToolsModule);
        workOrderToolsModuleForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        workOrderToolsModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> workOrderToolsModuleFormFields = new ArrayList<>();
        int seqNum = 0;
        workOrderToolsModuleFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", ++seqNum,1));
        workOrderToolsModuleFormFields.add(new FormField("tool", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tool", FormField.Required.REQUIRED, "tool", ++seqNum, 1));
        workOrderToolsModuleFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Work Order", FormField.Required.OPTIONAL, "ticket", ++seqNum, 1));
        workOrderToolsModuleFormFields.add(new FormField("issuedTo", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Issue To", FormField.Required.REQUIRED, "users", ++seqNum, 1));
        workOrderToolsModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION,"Duration", FormField.Required.OPTIONAL,"duration",++seqNum,1));
        workOrderToolsModuleFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.DECIMAL, "Quantity", FormField.Required.REQUIRED, ++seqNum, 1));

        FormSection workOrderToolsModuleFormSection = new FormSection("Default", 1, workOrderToolsModuleFormFields, false);
        workOrderToolsModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        workOrderToolsModuleForm.setSections(Collections.singletonList(workOrderToolsModuleFormSection));
        workOrderToolsModuleForm.setIsSystemForm(true);
        workOrderToolsModuleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(workOrderToolsModuleForm);
    }
}
