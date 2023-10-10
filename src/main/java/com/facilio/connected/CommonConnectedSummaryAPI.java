package com.facilio.connected;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facilio.modules.FieldFactory.getStringField;

public class CommonConnectedSummaryAPI {


    public static List<PagesContext> getNewReadingRuleSystemPage(ApplicationContext app, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule ruleModule = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);


        String pageName, pageDisplayName;
        pageName = ruleModule.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default "+ ruleModule.getDisplayName()+" Page ";

        List<PagesContext> rulePages = new ArrayList<>();

        PagesContext ruleTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleDetails", null, null)
                .addWidget("ruleDetails", "Rule Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_4", 0, 0, null, getReadingRuleSummaryWidgetDetails(FacilioConstants.ReadingRules.NEW_READING_RULE, app))
                .widgetDone()
                .sectionDone()
                .addSection("faultDetails", null, null)
                .addWidget("assetsAndAlarm", null, PageWidget.WidgetType.RULE_ASSETS_AND_ALARM, "webRuleAssetsAndAlarm_4_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("rootCauseAndImpact", null, PageWidget.WidgetType.ROOT_CAUSE_AND_IMPACT, "webRootCauseAndImpact_4_3", 4, 0, null, null)
                .widgetDone()
                .addWidget("ruleAlarmInsights", null, PageWidget.WidgetType.RULE_ALARM_INSIGHT, "webRuleAlarmInsights_4_5", 7, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("workOrderDetails", null, null)
                .addWidget("associatedWorkOrders", "Associated Work Orders", PageWidget.WidgetType.RULE_ASSOCIATED_WORK_ORDERS, "webRuleAssociatedWorkOrders_3_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("ruleWorkOrderDuration", "Associated Work Orders", PageWidget.WidgetType.RULE_WORK_ORDER_DURATION, "webRuleWorkOrderDuration_3_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("ruleInsight", "Rule Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleInsight", null, null)
                .addWidget("ruleInsight", "Rule Insight", PageWidget.WidgetType.RULE_INSIGHT, "webRuleInsight_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("rootCauses", "Root Causes", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("rootCauses", null, null)
                .addWidget("rootCauses", "Root Causes", PageWidget.WidgetType.ROOT_CAUSES, "webRootCauses_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("logs", "Logs", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("ruleLogs", null, null)
                .addWidget("ruleLogs", "Activity Log", PageWidget.WidgetType.RULE_LOGS, "webRuleLogs_12_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        rulePages.add(ruleTemplatePage);


        return rulePages;
    }

    private static JSONObject getReadingRuleSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField description = moduleBean.getField("description", moduleName);
        FacilioField assetCategory = moduleBean.getField("assetCategory", moduleName);

        FacilioField messageField = getStringField("message", "MESSAGE", module);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, description, 1, 1, 4);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, messageField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, assetCategory, 2, 2, 1);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);


        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("otherDetails");
        allFieldsWidgetGroup.setDisplayName("Other Details");
        allFieldsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

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

    //newreadingalarm
    public static List<PagesContext> getReadingAlarmSystemPage(ApplicationContext app, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);


        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default "+ module.getDisplayName()+" Page ";

        List<PagesContext> rulePages = new ArrayList<>();

        PagesContext readingAlarmTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("alarmDetails", null, null)
                .addWidget("readingAlarmDetails", "Faults Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "websummaryfieldswidget_5_9", 0, 0, null, getReadingAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.NEW_READING_ALARM, app))
                .widgetDone()
                .addWidget("readingAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_13_3",9,0,null,null)
                .widgetDone()
                .addWidget("readingAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_13_3", 9,3, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("insight", "Insight", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("impactDetails", null, null)
                .addWidget("meanTimeBetweenOccurrence","Mean time between occurrences", PageWidget.WidgetType.MEAN_TIME_BETWEEN_OCCURRENCE, "webMeanTimeBetweenOccurrence_14_6",0,0, null, null)
                .widgetDone()
                .addWidget("meanTimeToClear", "Mean time to clear", PageWidget.WidgetType.MEAN_TIME_TO_CLEAR, "webMeanTimeToClear_14_6", 6, 0, null, null)
                .widgetDone()
                .addWidget("noOfOccurrences", "No. of occurrences", PageWidget.WidgetType.NO_OF_OCCURRENCES, "webNoOfOccurrences_12_6", 0, 14, null, null)
                .widgetDone()
                .addWidget("impactInfo", "Impact template", PageWidget.WidgetType.IMPACT_INFO, "webImpactInfo_12_6", 6, 14, null, null)
                .widgetDone()
                .addWidget("costImpact", "Cost impact", PageWidget.WidgetType.COST_IMPACT, "webCostImpact_14_6", 0,26, null, null)
                .widgetDone()
                .addWidget("energyImpact", "Energy impact (KWH)", PageWidget.WidgetType.ENERGY_IMPACT, "webEnergyImpact_14_6", 6,26, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("impactChart", null, null)
                .addWidget("impactReport", "Impact report", PageWidget.WidgetType.IMPACT_REPORT, "webImpactReport_51", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("readingAlarmOccurrenceHistory", "Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_58_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("rootCauses", "Root Causes", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("rootCauses", null, null)
                .addWidget("alarmRca", "Root Causes", PageWidget.WidgetType.ALARM_RCA, "webAlarmRca_58_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        rulePages.add(readingAlarmTemplatePage);


        return rulePages;
    }

    private static JSONObject getReadingAlarmSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField lastOccurredTime = moduleBean.getField("lastOccurredTime", moduleName);
        FacilioField lastCreatedTime = moduleBean.getField("lastCreatedTime", moduleName);
        FacilioField rule = moduleBean.getField("rule", moduleName);
        FacilioField resource = moduleBean.getField("resource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 1, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 2, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, rule, 1, 3, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 1, 4, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 1, 1, true);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addAlarmSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1, false);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("otherDetails");
        allFieldsWidgetGroup.setDisplayName("Other Details");
        allFieldsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    //bmsalarm
    public static List<PagesContext> getBMSAlarmSystemPage(ApplicationContext app, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BMS_ALARM);


        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";

        List<PagesContext> sensorAlarmPages = new ArrayList<>();

        PagesContext sensorAlarmTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("alarmDetails", null, null)
                .addWidget("bmsAlarmDetails", "BMS Alarm Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "websummaryfieldswidget_5_9", 0, 0, null, getBMSAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.BMS_ALARM, app))
                .widgetDone()
                .addWidget("bmsAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_13_3",9,0,null,null)
                .widgetDone()
                .addWidget("bmsAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_13_3", 9,3, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("bmsOccurrenceHistory", "BMS Alarm", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_58_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        sensorAlarmPages.add(sensorAlarmTemplatePage);
        return sensorAlarmPages;
    }

    private static JSONObject getBMSAlarmSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField lastOccurredTime = moduleBean.getField("lastOccurredTime", moduleName);
        FacilioField lastCreatedTime = moduleBean.getField("lastCreatedTime", moduleName);
        FacilioField condition = moduleBean.getField("condition", moduleName);
        FacilioField source = moduleBean.getField("source", moduleName);
        FacilioField controller = moduleBean.getField("controller", moduleName);
        FacilioField resource = moduleBean.getField("resource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 1, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 2, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, condition, 1, 3, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, source, 1, 4, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, controller, 2, 1, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 2, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 3, 1, true);


        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addAlarmSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1, false);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("otherDetails");
        allFieldsWidgetGroup.setDisplayName("Other Details");
        allFieldsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    //sensorrollupalarm

    public static List<PagesContext> getSensorAlarmSystemPage(ApplicationContext app, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);


        String pageName, pageDisplayName;
        pageName = module.getName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "") + "defaultpage";
        pageDisplayName = "Default " + module.getDisplayName() + " Page ";

        List<PagesContext> sensorAlarmPages = new ArrayList<>();

        PagesContext sensorAlarmTemplatePage = new PagesContext(pageName, pageDisplayName, "", null, isTemplate, isDefault, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("alarmDetails", null, null)
                .addWidget("sensorAlarmDetails", "Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "websummaryfieldswidget_5_9", 0, 0, null, getSensorAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, app))
                .widgetDone()
                .addWidget("sensorAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_13_3",9,0,null,null)
                .widgetDone()
                .addWidget("sensorAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_13_3", 9,3, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("sensorAlarmOccurrenceHistory", "Sensor Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_58_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        sensorAlarmPages.add(sensorAlarmTemplatePage);
        return sensorAlarmPages;
    }

    private static JSONObject getSensorAlarmSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField lastOccurredTime = moduleBean.getField("lastOccurredTime", moduleName);
        FacilioField lastCreatedTime = moduleBean.getField("lastCreatedTime", moduleName);
        FacilioField readingField = moduleBean.getField("readingFieldId", moduleName);
        FacilioField resource = moduleBean.getField("resource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 1, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 2, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, readingField, 1, 3, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 1, 4, 1, false);
        addAlarmSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 1, 1, true);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addAlarmSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1, false);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("otherDetails");
        allFieldsWidgetGroup.setDisplayName("Other Details");
        allFieldsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addAlarmSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, Boolean isOneLevelField) throws Exception {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if(isOneLevelField){
                summaryField.setName("category");
                summaryField.setDisplayName("Category");
                summaryField.setParentLookupFieldId(field.getFieldId());
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField assetCategory = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
                summaryField.setFieldId(assetCategory.getFieldId());
            }
            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.BASE_ALARM_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.BASE_ALARM_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
