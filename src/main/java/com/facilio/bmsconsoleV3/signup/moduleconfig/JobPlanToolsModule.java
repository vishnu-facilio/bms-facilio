package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CustomPageWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.bmsconsole.context.SummaryWidgetGroupFields;
import com.facilio.bmsconsole.context.ViewField;
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

public class JobPlanToolsModule extends BaseModuleConfig {

    public JobPlanToolsModule() {
        setModuleName(FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> JobPlanToolsViews = new ArrayList<FacilioView>();
        JobPlanToolsViews.add(getAllJobPlanToolsViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", JobPlanToolsViews);
        groupVsViews.add(groupDetails);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        groupDetails.put("appLinkNames", appLinkNames);

        return groupVsViews;
    }

    private static FacilioView getAllJobPlanToolsViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getJobPlanToolsModule());
        SortField sortField = new SortField(createdTime, false);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Job Plan Tools");
        FacilioModule jobPlanToolsModule = ModuleFactory.getJobPlanToolsModule();
        allView.setModuleName(jobPlanToolsModule.getName());
        allView.setSortFields(Collections.singletonList(sortField));
        allView.setFields(getAllViewColumns());

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("toolType","Tool Type"));
        columns.add(new ViewField("description","Description","toolType"));
        columns.add(new ViewField("storeRoom","Storeroom"));
        columns.add(new ViewField("quantity","Quantity"));
        columns.add(new ViewField("duration","Duration"));
        return columns;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule jobPlanToolsModule = modBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TOOLS);

        FacilioForm jobPlanToolForm = new FacilioForm();
        jobPlanToolForm.setDisplayName("New Job Plan Tool");
        jobPlanToolForm.setName("default_jobPlanTool_web");
        jobPlanToolForm.setModule(jobPlanToolsModule);
        jobPlanToolForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        jobPlanToolForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));


        int seqNum = 0;
        List<FormField> jobPlanToolFormFields = new ArrayList<>();
        jobPlanToolFormFields.add(new FormField("toolType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tool", FormField.Required.REQUIRED, "toolType", ++seqNum,1));
        jobPlanToolFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", ++seqNum, 1));
        jobPlanToolFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.NUMBER, "Quantity", FormField.Required.OPTIONAL, ++seqNum, 1));
        jobPlanToolFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Duration", FormField.Required.OPTIONAL, ++seqNum, 1));

        FormSection section = new FormSection("Default", 1, jobPlanToolFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        jobPlanToolForm.setSections(Collections.singletonList(section));
        jobPlanToolForm.setIsSystemForm(true);
        jobPlanToolForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(jobPlanToolForm);
    }

    @Override
    public void addData() throws Exception {
        super.addData();
        List<String> appLinkNamesForSummaryWidget = new ArrayList<>();
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNamesForSummaryWidget.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        for(String appLinkName: appLinkNamesForSummaryWidget) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule jobPlanToolsModule = moduleBean.getModule(FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
            FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
            FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
            FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
            FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", FacilioConstants.ContextNames.JOB_PLAN_TOOLS);

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

            pageWidget1.setName("jobPlanToolsWidget");
            pageWidget1.setDisplayName("Job Plan Tools Widget");
            pageWidget1.setModuleId(jobPlanToolsModule.getModuleId());
            pageWidget1.setAppId(ApplicationApi.getApplicationIdForLinkName(appLinkName));
            pageWidget1.setGroups(widgetGroupList);

            SummaryWidgetUtil.addPageWidget(pageWidget1);
        }
    }
}
