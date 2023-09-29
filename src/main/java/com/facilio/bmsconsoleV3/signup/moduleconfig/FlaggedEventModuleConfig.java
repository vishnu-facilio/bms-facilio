package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.signup.*;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FlaggedEventModuleConfig extends BaseModuleConfig {
    public FlaggedEventModuleConfig(){
        setModuleName(FlaggedEventModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> filteredAlarmViews = new ArrayList<FacilioView>();
        filteredAlarmViews.add(getAllAlarms().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FlaggedEventModule.MODULE_NAME);
        groupDetails.put("views", filteredAlarmViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Flagged Event");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        allView.setModuleName(FlaggedEventModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField("flaggedEventRule", "Flagged Event Rule"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields remoteMonitorApp = new ScopeVariableModulesFields();
        remoteMonitorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_remotemonitor_client"));
        remoteMonitorApp.setModuleId(module.getModuleId());
        remoteMonitorApp.setFieldName("client");

        scopeConfigList = Arrays.asList(remoteMonitorApp);
        return scopeConfigList;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        return getSystemPagesMap(getModuleName());
    }


    public static Map<String, List<PagesContext>> getSystemPagesMap(String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY);

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        FacilioModule module = modBean.getModule(moduleName);

        Map<String, List<PagesContext>> pageMap = new HashMap<>();

        pageMap.put(app.getLinkName(), Arrays.asList(new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Filtered Alarm Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("evaluationTeams", "Evaluation Teams", "List of Evaluation Teams")
                .addWidget("evaluationTeamsRelated", "Evaluation Teams", PageWidget.WidgetType.RELATED_LIST, "relatedlistwidgetViewWidget-28*12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), FlaggedEventBureauActionModule.MODULE_NAME,"flaggedEvent"))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("filteredAlarms", "FILTERED ALARMS", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("filteredAlarms", "Filtered Alarms", "List of filtered alarms")
                .addWidget("filteredAlarmsRelated", "Filtered Alarms", PageWidget.WidgetType.RELATED_LIST, "relatedlistwidgetViewWidget-28*12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), FilteredAlarmModule.MODULE_NAME,"flaggedEvent"))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()));
        return pageMap;
    }

    public static JSONObject getSingleRelatedListForModule(FacilioModule module, String subModuleName, String fieldName) throws Exception {
        List<RelatedListWidgetContext> relatedLists = RelatedListWidgetUtil.fetchAllRelatedList(module, false, null, null);
        if(CollectionUtils.isNotEmpty(relatedLists)) {
            relatedLists.removeIf(relList -> !(subModuleName.equalsIgnoreCase(relList.getSubModuleName()) && fieldName.equalsIgnoreCase(relList.getFieldName())));
            RelatedListWidgetContext relList = CollectionUtils.isNotEmpty(relatedLists) ? relatedLists.get(0) : null;
            return FieldUtil.getAsJSON(relList);
        }
        return null;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name",moduleName);
        FacilioField clientField = moduleBean.getField("client",moduleName);
        FacilioField controllerField = moduleBean.getField("controller",moduleName);
        FacilioField workorderField = moduleBean.getField("workorder",moduleName);
        FacilioField team = moduleBean.getField("team",moduleName);
        FacilioField people = moduleBean.getField("assignedPeople",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, controllerField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, workorderField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, team, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, people, 2, 2, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);



        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemInformationGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedTimeField, 1, 4, 1);


        systemInformationGroup.setName("systemInformation");
        systemInformationGroup.setDisplayName("System Information");
        systemInformationGroup.setColumns(4);
        widgetGroupList.add(systemInformationGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }


    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }


    private static JSONObject getWidgetGroup() throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
