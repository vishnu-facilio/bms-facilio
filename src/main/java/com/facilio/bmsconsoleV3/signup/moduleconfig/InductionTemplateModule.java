package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
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

public class InductionTemplateModule extends BaseModuleConfig{
    public InductionTemplateModule(){
        setModuleName(FacilioConstants.Induction.INDUCTION_TEMPLATE);
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module = Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE);
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
            appNameVsPage.put(appName,createInductionTemplatePage(app, module, false,true));
        }
        return appNameVsPage;
    }
    public List<PagesContext> createInductionTemplatePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceRequest = modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE);

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Inspection.INSPECTION_RESPONSE_ACTIVITY);

        return new ModulePages()
                .addPage("inductionTemplatedefaultpage", "Default Induction Template Page", "", null, isTemplate, isDefault, true)
                .addLayout(PagesContext.PageLayoutType.WEB)

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("inductionSummaryFieldWidget", "Induction Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getInductionTemplateSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("inductionquestionspagestriggers", "", null)
                .addWidget("inductionQuestionsCountWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_QUESTION_COUNT, "fixedquestioncountwidget_2_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("inductionPagesCountWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_PAGE_COUNT, "fixedpagecountwidget_2_4", 4, 0, null, null)
                .widgetDone()
                .addWidget("inductionLastTriggeredWidget", "", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_LAST_TRIGGERED, "fixedlasttriggeredwidget_2_4", 8, 0, null, null)
                .widgetDone()
                .addWidget("inductionQuestionWidget", "Questions", PageWidget.WidgetType.INDUCTION_TEMPLATE_SUMMARY_QUESTIONS, "flexibleinductiontemplatequestionswidget_6", 0, 1, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trigger", "Trigger", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontriggers", null, null)
                .addWidget("inductionTriggers", "Induction Triggers", PageWidget.WidgetType.INDUCTION_TEMPLATE_TRIGGERS, "flexibleinductiontemplatetriggerwidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("insight", "Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontemplateinsight", null, null)
                .addWidget("inductionTemplateInsightGraph", "Induction By Status", PageWidget.WidgetType.INDUCTION_TEMPLATE_INSIGHT_GRAPH, "flexibleinductiontemplateinsightgraphwidget_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("inductionTemplateInsightSummary", "Induction Summary", PageWidget.WidgetType.INDUCTION_TEMPLATE_INSIGHT_SUMMARY, "flexibleinductiontemplateinsightsummarywidget_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("inductiontemplatehistory", null, null)
                .addWidget("inductionTemplateHistoryWidget", "Induction", PageWidget.WidgetType.RELATED_LIST, "flexiblewebrelatedlist_6", 0, 0, null , RelatedListWidgetUtil.getSingleRelatedListForModule(Constants.getModBean().getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE),FacilioConstants.Induction.INDUCTION_RESPONSE,"parent"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone().getCustomPages();
    }

    public JSONObject getInductionTemplateSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField applyToField = moduleBean.getField("siteApplyTo", moduleName);
        FacilioField scopeField = moduleBean.getField("creationType", moduleName);
        FacilioField siteIdField = moduleBean.getField("siteId", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, applyToField, 1, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, scopeField, 1, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, siteIdField, 1, 3, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 1, 4, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 2, 1, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField, 2, 2, 1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 2, 3, 1);


        widgetGroup.setName("configurationinformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        widgetGroup.setSequenceNumber(1);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("Induction Details");
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

        SystemButtonRuleContext qAndABuilderSystemButton = new SystemButtonRuleContext();
        qAndABuilderSystemButton.setName(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_TEXT);
        qAndABuilderSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        qAndABuilderSystemButton.setIdentifier(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_IDENTIFIER);
        qAndABuilderSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        qAndABuilderSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        qAndABuilderSystemButton.setPermissionRequired(true);

        SystemButtonRuleContext editSystemButton = new SystemButtonRuleContext();
        editSystemButton.setName(FacilioConstants.Inspection.EDIT_BUTTON_TEXT);
        editSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editSystemButton.setIdentifier(FacilioConstants.Inspection.EDIT_BUTTON_IDENTIFIER);
        editSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editSystemButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editSystemButton.setPermissionRequired(true);

        SystemButtonRuleContext executeNowSystemButton = new SystemButtonRuleContext();
        executeNowSystemButton.setName(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_TEXT);
        executeNowSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        executeNowSystemButton.setIdentifier(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_IDENTIFIER);
        executeNowSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        executeNowSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
        executeNowSystemButton.setPermissionRequired(true);

        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,qAndABuilderSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,editSystemButton);
        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,executeNowSystemButton);

    }

    // will enable this when publish and clone inspection is completed
//    public static void addSystemButtons() throws Exception {
//
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioField publishField = modBean.getField("isPublished", FacilioConstants.Induction.INDUCTION_TEMPLATE);
//        Criteria isNotPublishedCriteria = new Criteria();
//        isNotPublishedCriteria.addAndCondition(CriteriaAPI.getCondition(publishField,String.valueOf(false), BooleanOperators.IS));
//        Criteria isPublishedCriteria = new Criteria();
//        isPublishedCriteria.addAndCondition(CriteriaAPI.getCondition(publishField,String.valueOf(true), BooleanOperators.IS));
//
//        SystemButtonRuleContext qAndABuilderSystemButton = new SystemButtonRuleContext();
//        qAndABuilderSystemButton.setName(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_TEXT);
//        qAndABuilderSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        qAndABuilderSystemButton.setIdentifier(FacilioConstants.Inspection.Q_AND_A_BUILDER_BUTTON_IDENTIFIER);
//        qAndABuilderSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        qAndABuilderSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
//        qAndABuilderSystemButton.setPermissionRequired(true);
//        qAndABuilderSystemButton.setCriteria(isNotPublishedCriteria);
//
//        SystemButtonRuleContext editSystemButton = new SystemButtonRuleContext();
//        editSystemButton.setName(FacilioConstants.Inspection.EDIT_BUTTON_TEXT);
//        editSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
//        editSystemButton.setIdentifier(FacilioConstants.Inspection.EDIT_BUTTON_IDENTIFIER);
//        editSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        editSystemButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
//        editSystemButton.setPermissionRequired(true);
//        editSystemButton.setCriteria(isNotPublishedCriteria);
//
//        SystemButtonRuleContext executeNowSystemButton = new SystemButtonRuleContext();
//        executeNowSystemButton.setName(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_TEXT);
//        executeNowSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        executeNowSystemButton.setIdentifier(FacilioConstants.Inspection.EXECUTE_NOW_BUTTON_IDENTIFIER);
//        executeNowSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        executeNowSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
//        executeNowSystemButton.setPermissionRequired(true);
//        executeNowSystemButton.setCriteria(isPublishedCriteria);
//
//        SystemButtonRuleContext publishSystemButton = new SystemButtonRuleContext();
//        publishSystemButton.setName(FacilioConstants.Inspection.PUBLISH_BUTTON_TEXT);
//        publishSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        publishSystemButton.setIdentifier(FacilioConstants.Inspection.PUBLISH_BUTTON_IDENTIFIER);
//        publishSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        publishSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
//        publishSystemButton.setPermissionRequired(true);
//        publishSystemButton.setCriteria(isNotPublishedCriteria);
//
//        SystemButtonRuleContext cloneSystemButton = new SystemButtonRuleContext();
//        cloneSystemButton.setName(FacilioConstants.Inspection.CLONE_BUTTON_TEXT);
//        cloneSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
//        cloneSystemButton.setIdentifier(FacilioConstants.Inspection.CLONE_BUTTON_IDENTIFIER);
//        cloneSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        cloneSystemButton.setPermission(AccountConstants.ModulePermission.READ.name());
//        cloneSystemButton.setPermissionRequired(true);
//
//        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,qAndABuilderSystemButton);
//        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,editSystemButton);
//        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,executeNowSystemButton);
//        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,publishSystemButton);
//        SystemButtonApi.addSystemButton(FacilioConstants.Induction.INDUCTION_TEMPLATE,cloneSystemButton);
//
//    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inductionTemplate = new ArrayList<FacilioView>();
        inductionTemplate.add(getAllInductionTemplateViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Induction.INDUCTION_TEMPLATE);
        groupDetails.put("views", inductionTemplate);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInductionTemplateViews() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Templates.ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Induction Templates");
        allView.setModuleName(FacilioConstants.Induction.INDUCTION_TEMPLATE);
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
}
