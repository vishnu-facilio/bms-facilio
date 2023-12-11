package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;
import java.util.*;

public class InspectionTemplateModule extends BaseModuleConfig{
    public InspectionTemplateModule(){
        setModuleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,createInspectionTemplatePage(app, module, false,true));
        }
        return appNameVsPage;
    }

    public List<PagesContext> createInspectionTemplatePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequest = modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.SERVICE_REQUEST_ACTIVITY);
        return new ModulePages()
                .addPage("inspectionTemplatedefaultpage", "Default Inspection Template Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inspectionSummaryFieldWidget", "Inspection Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getInspectionTemplateSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("questionspagestriggers", "", null)
                .addWidget("questionsCountWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_QUESTION_COUNT, "fixedquestioncountwidget_2_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("pagesCountWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_PAGE_COUNT, "fixedpagecountwidget_2_4", 4, 0, null, null)
                .widgetDone()
                .addWidget("lastTriggeredWidget", "", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_LAST_TRIGGERED, "fixedlasttriggeredwidget_2_4", 8, 0, null, null)
                .widgetDone()
                .addWidget("questionWidget", "Questions", PageWidget.WidgetType.INSPECTION_TEMPLATE_SUMMARY_QUESTIONS, "flexibleinspectiontemplatequestionswidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trigger", "Trigger", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontriggers", null, null)
                .addWidget("inspectionTriggers", "Inspection Triggers", PageWidget.WidgetType.INSPECTION_TEMPLATE_TRIGGERS, "flexibleinspectiontemplatetriggerwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("insight", "Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontemplateinsight", null, null)
                .addWidget("inspectionTemplateInsightGraph", "Inspection By Status", PageWidget.WidgetType.INSPECTION_TEMPLATE_INSIGHT_GRAPH, "flexibleinspectiontemplateinsightgraphwidget_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("inspectionTemplateInsightSummary", "Inspection Summary", PageWidget.WidgetType.INSPECTION_TEMPLATE_INSIGHT_SUMMARY, "flexibleinspectiontemplateinsightsummarywidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inspectiontemplatehistory", null, null)
                .addWidget("inspectionTemplateHistoryWidget", "Inspection", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_6", 0, 0, null , RelatedListWidgetUtil.getSingleRelatedListForModule(Constants.getModBean().getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE),FacilioConstants.Inspection.INSPECTION_RESPONSE,"parent"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    public JSONObject getInspectionTemplateSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField scopeField = moduleBean.getField("creationType", moduleName);
        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField spaceAssetField = moduleBean.getField("resource", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scopeField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, siteIdField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, spaceAssetField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField, 2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 3, 1);


        widgetGroup.setName("configurationinformation");
        widgetGroup.setDisplayName("Configuration Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, categoryField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup2, priorityField, 1, 2, 1);


        widgetGroup2.setName("fieldinformation");
        widgetGroup2.setDisplayName("Field Information");
        widgetGroup2.setColumns(4);
        widgetGroup2.setSequenceNumber(2);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);

        pageWidget.setDisplayName("Inspection Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    @Override
    public void addData() throws Exception {
        addSystemButtons();

    }

    public static void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField publishField = modBean.getField("isPublished", FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        Criteria isNotPublishedCriteria = new Criteria();
        isNotPublishedCriteria.addAndCondition(CriteriaAPI.getCondition(publishField,String.valueOf(false), BooleanOperators.IS));
        Criteria isPublishedCriteria = new Criteria();
        isPublishedCriteria.addAndCondition(CriteriaAPI.getCondition(publishField,String.valueOf(true), BooleanOperators.IS));

        SystemButtonRuleContext qAndABuilderSystemButton = new SystemButtonRuleContext();
        qAndABuilderSystemButton.setName(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_TEXT);
        qAndABuilderSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        qAndABuilderSystemButton.setIdentifier(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_IDENTIFIER);
        qAndABuilderSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        qAndABuilderSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        qAndABuilderSystemButton.setPermissionRequired(true);
        qAndABuilderSystemButton.setCriteria(isNotPublishedCriteria);

        SystemButtonRuleContext editSystemButton = new SystemButtonRuleContext();
        editSystemButton.setName(FacilioConstants.Inspection.EDIT_BUTTON_TEXT);
        editSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editSystemButton.setIdentifier(FacilioConstants.Inspection.EDIT_BUTTON_IDENTIFIER);
        editSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editSystemButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editSystemButton.setPermissionRequired(true);
        editSystemButton.setCriteria(isNotPublishedCriteria);

        SystemButtonRuleContext executeNowSystemButton = new SystemButtonRuleContext();
        executeNowSystemButton.setName(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_TEXT);
        executeNowSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        executeNowSystemButton.setIdentifier(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_IDENTIFIER);
        executeNowSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        executeNowSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        executeNowSystemButton.setPermissionRequired(true);
        executeNowSystemButton.setCriteria(isPublishedCriteria);

        SystemButtonRuleContext publishSystemButton = new SystemButtonRuleContext();
        publishSystemButton.setName(FacilioConstants.Inspection.PUBLISH_BUTTON_TEXT);
        publishSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        publishSystemButton.setIdentifier(FacilioConstants.Inspection.PUBLISH_BUTTON_IDENTIFIER);
        publishSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        publishSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        publishSystemButton.setPermissionRequired(true);
        publishSystemButton.setCriteria(isNotPublishedCriteria);

        SystemButtonRuleContext cloneSystemButton = new SystemButtonRuleContext();
        cloneSystemButton.setName(FacilioConstants.Inspection.CLONE_BUTTON_TEXT);
        cloneSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        cloneSystemButton.setIdentifier(FacilioConstants.Inspection.CLONE_BUTTON_IDENTIFIER);
        cloneSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        cloneSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        cloneSystemButton.setPermissionRequired(true);

        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_TEMPLATE,qAndABuilderSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_TEMPLATE,editSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_TEMPLATE,executeNowSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_TEMPLATE,publishSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Inspection.INSPECTION_TEMPLATE,cloneSystemButton);

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inspectionTemplate = new ArrayList<FacilioView>();
        inspectionTemplate.add(getAllInspectionTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        groupDetails.put("views", inspectionTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInspectionTemplateViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Templates.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inspection Templates");
        allView.setModuleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
        allView.setSortFields(sortFields);
//
//        List<ViewField> allViewFields = new ArrayList<>();
//
//        allViewFields.add(new ViewField("name","Name"));
//        allViewFields.add(new ViewField("siteId","Site"));
//        allViewFields.add(new ViewField("resource","Space/Asset "));
//        allViewFields.add(new ViewField("category","Category"));
//        allViewFields.add(new ViewField("priority","Priority"));
//        allViewFields.add(new ViewField("assignedTo","Assigned To"));
//        allViewFields.add(new ViewField("totalPages","Total Pages"));
//        allViewFields.add(new ViewField("totalQuestions","Total Questions"));
//
//        allView.setFields(allViewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("creationType");
        fieldNames.add("resource");
        fieldNames.add("name");
        fieldNames.add("siteId");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
