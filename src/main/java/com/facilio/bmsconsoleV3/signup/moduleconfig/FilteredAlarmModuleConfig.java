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
import com.facilio.remotemonitoring.signup.AddSubModuleRelations;
import com.facilio.remotemonitoring.signup.FilteredAlarmModule;
import org.json.simple.JSONObject;

import java.util.*;

public class FilteredAlarmModuleConfig extends BaseModuleConfig {
    public FilteredAlarmModuleConfig(){
        setModuleName(FilteredAlarmModule.MODULE_NAME);
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
        groupDetails.put("moduleName", FilteredAlarmModule.MODULE_NAME);
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
        sysCreatedTime.setModule(modBean.getModule(FilteredAlarmModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Filtered Alarms");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        allView.setModuleName(FilteredAlarmModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("message", "Message"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("alarmCategory", "Alarm Category"));
        viewFields.add(new ViewField(FilteredAlarmModule.RAW_ALARM_FIELD_NAME, "Alarm"));
        viewFields.add(new ViewField("occurredTime", "Occurred Time"));
        viewFields.add(new ViewField("clearedTime", "Cleared Time"));

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
        historyWidgetParam.put("activityModuleName", AddSubModuleRelations.FILTER_ALARM_ACTIVITY);

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        FacilioModule module = modBean.getModule(moduleName);

        Map<String, List<PagesContext>> pageMap = new HashMap<>();

        pageMap.put(app.getLinkName(), Arrays.asList(new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Filtered Alarm Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 4, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "RELATED", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("relatedlist", "Related List", "List of related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 4, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()));
        return pageMap;
    }


    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField siteField = moduleBean.getField("site",moduleName);
        FacilioField clientField = moduleBean.getField("client",moduleName);
        FacilioField controllerField = moduleBean.getField("controller",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, controllerField, 1, 3, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(3);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        FacilioField occurredTime = moduleBean.getField("occurredTime",moduleName);
        FacilioField clearedTime = moduleBean.getField("clearedTime",moduleName);
        FacilioField alarmType = moduleBean.getField("alarmType",moduleName);
        FacilioField alarmCategory = moduleBean.getField("alarmCategory",moduleName);
        FacilioField message = moduleBean.getField("message",moduleName);
        FacilioField rawAlarm = moduleBean.getField(FilteredAlarmModule.RAW_ALARM_FIELD_NAME,moduleName);

        SummaryWidgetGroup alarmDetailsGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, message, 1, 1, 1);
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, alarmType, 1, 2, 1);
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, alarmCategory, 1, 3, 1);
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, occurredTime, 2, 1, 1);
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, clearedTime, 2, 2, 1);
        addSummaryFieldInWidgetGroup(alarmDetailsGroup, rawAlarm, 2, 3, 1);


        alarmDetailsGroup.setName("alarmInformation");
        alarmDetailsGroup.setDisplayName("Alarm Information");
        alarmDetailsGroup.setColumns(3);
        widgetGroupList.add(alarmDetailsGroup);



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
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_5", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_5", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
