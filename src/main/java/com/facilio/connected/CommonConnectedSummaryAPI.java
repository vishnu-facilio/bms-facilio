package com.facilio.connected;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.facilio.modules.FieldFactory.getStringField;

public class CommonConnectedSummaryAPI {
    private static final List<String> ALARM_EXCLUDE_FIELDS = Arrays.asList("key", "type", "acknowledged", "noOfNotes");
    private static final List<String> RULE_EXCLUDE_FIELDS = Arrays.asList("categoryId", "readingModuleId", "readingFieldId", "resourceType", "autoClear", "linkName");

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
                .addWidget("assetsAndAlarm", null, PageWidget.WidgetType.RULE_ASSETS_AND_ALARM, "webRuleAssetsAndAlarm_5_4", 0, 0, null, null)
                .widgetDone()
                .addWidget("rootCauseAndImpact", null, PageWidget.WidgetType.ROOT_CAUSE_AND_IMPACT, "webRootCauseAndImpact_5_3", 4, 0, null, null)
                .widgetDone()
                .addWidget("ruleAlarmInsights", null, PageWidget.WidgetType.RULE_ALARM_INSIGHT, "webRuleAlarmInsights_5_5", 7, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("workOrderDetails", null, null)
                .addWidget("associatedWorkOrders", "Associated Work Orders", PageWidget.WidgetType.RULE_ASSOCIATED_WORK_ORDERS, "webRuleAssociatedWorkOrders_3_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("ruleWorkOrderDuration", "Response and Resolution Metrics", PageWidget.WidgetType.RULE_WORK_ORDER_DURATION, "webRuleWorkOrderDuration_3_6", 6, 0, null, null)
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

        FacilioField messageField = getStringField("Message", "MESSAGE", module);
        messageField.setDisplayName("Message");

        List<String> readingRuleFilterFields = new ArrayList<>(RULE_EXCLUDE_FIELDS);
        readingRuleFilterFields.addAll(Arrays.asList("description", "assetCategory"));

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
        List<FacilioField> filteredFields = fields.stream().filter(field -> !readingRuleFilterFields.contains(field.getName())).collect(Collectors.toList());

        int columnNo = 1, rowNo = 1;
        Stack<FacilioField> stack = new Stack<>();
        for (FacilioField field : filteredFields) {
            if("description".equals(field.getName()) ||(FieldType.BIG_STRING.equals(field.getDataTypeEnum())) || (FieldType.LARGE_TEXT.equals(field.getDataTypeEnum()))) {
                stack.push(field);
                continue;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        while (!stack.isEmpty()) {
            FacilioField bigStringField = stack.pop();
            if(columnNo > 1) {
                columnNo = 1;
                rowNo ++;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, bigStringField, rowNo, columnNo, 4);
            rowNo++;
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
                .addWidget("readingAlarmDetails", "Faults Details", PageWidget.WidgetType.FIXED_SUMMARY_FIELDS_WIDGET, "webfixedsummaryfieldswidget_6_9", 0, 0, null, getReadingAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.NEW_READING_ALARM, app))
                .widgetDone()
                .addWidget("readingAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_3_3",9,0,null,null)
                .widgetDone()
                .addWidget("readingAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_3_3", 9,3, null, null)
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
                .addWidget("meanTimeBetweenOccurrence","Mean Time between Occurrences", PageWidget.WidgetType.MEAN_TIME_BETWEEN_OCCURRENCE, "webMeanTimeBetweenOccurrence_3_6",0,0, null, null)
                .widgetDone()
                .addWidget("meanTimeToClear", "Mean Time to Clear", PageWidget.WidgetType.MEAN_TIME_TO_CLEAR, "webMeanTimeToClear_3_6", 6, 0, null, null)
                .widgetDone()
                .addWidget("noOfOccurrences", "No of Occurrences", PageWidget.WidgetType.NO_OF_OCCURRENCES, "webNoOfOccurrences_3_6", 0, 6, null, null)
                .widgetDone()
                .addWidget("impactInfo", "Impact template", PageWidget.WidgetType.IMPACT_INFO, "webImpactInfo_3_6", 6, 6, null, null)
                .widgetDone()
                .addWidget("costImpact", "Cost Impact", PageWidget.WidgetType.COST_IMPACT, "webCostImpact_3_6", 0,12, null, null)
                .widgetDone()
                .addWidget("energyImpact", "Energy Impact (KWH)", PageWidget.WidgetType.ENERGY_IMPACT, "webEnergyImpact_3_6", 6,12, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("readingAlarmOccurrenceHistory", "Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_11_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("rootCauses", "Root Causes", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("rootCauses", null, null)
                .addWidget("alarmRca", "Root Causes", PageWidget.WidgetType.ALARM_RCA, "webAlarmRca_11_12", 0, 0, null, null)
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

        List<String> readingAlarmFilterFields = new ArrayList<>(ALARM_EXCLUDE_FIELDS);
        readingAlarmFilterFields.addAll(Arrays.asList("lastOccurredTime", "lastCreatedTime", "rule", "resource", "subRule", "readingFieldId", "isNewReadingRule"));

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, rule, 1, 3, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 1, 4, 1);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        List<FacilioField> filteredFields = fields.stream().filter(field -> !readingAlarmFilterFields.contains(field.getName())).collect(Collectors.toList());
        int columnNo = 1, rowNo = 1;
        Stack<FacilioField> stack = new Stack<>();
        for (FacilioField field : filteredFields) {
            if((FieldType.BIG_STRING.equals(field.getDataTypeEnum())) || (FieldType.LARGE_TEXT.equals(field.getDataTypeEnum()))) {
                stack.push(field);
                continue;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        while (!stack.isEmpty()) {
            FacilioField bigStringField = stack.pop();
            if(columnNo > 1) {
                columnNo = 1;
                rowNo ++;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, bigStringField, rowNo, columnNo, 4);
            rowNo++;
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
                .addWidget("bmsAlarmDetails", "BMS Alarm Details", PageWidget.WidgetType.FIXED_SUMMARY_FIELDS_WIDGET, "webfixedsummaryfieldswidget_6_9", 0, 0, null, getBMSAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.BMS_ALARM, app))
                .widgetDone()
                .addWidget("bmsAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_3_3",9,0,null,null)
                .widgetDone()
                .addWidget("bmsAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_3_3", 9,3, null, null)
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
                .addWidget("bmsOccurrenceHistory", "BMS Alarm", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_11_12", 0, 0, null, null)
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

        List<String> bmsAlarmFilterFields = new ArrayList<>(ALARM_EXCLUDE_FIELDS);
        bmsAlarmFilterFields.addAll(Arrays.asList("lastOccurredTime", "lastCreatedTime", "condition", "source", "controller", "resource"));

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, condition, 1, 3, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, source, 1, 4, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, controller, 2, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 2, 1);


        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        List<FacilioField> filteredFields = fields.stream().filter(field -> !bmsAlarmFilterFields.contains(field.getName())).collect(Collectors.toList());

        int columnNo = 1, rowNo = 1;
        Stack<FacilioField> stack = new Stack<>();
        for (FacilioField field : filteredFields) {
            if((FieldType.BIG_STRING.equals(field.getDataTypeEnum())) || (FieldType.LARGE_TEXT.equals(field.getDataTypeEnum()))) {
                stack.push(field);
                continue;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        while (!stack.isEmpty()) {
            FacilioField bigStringField = stack.pop();
            if(columnNo > 1) {
                columnNo = 1;
                rowNo ++;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, bigStringField, rowNo, columnNo, 4);
            rowNo++;
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
                .addWidget("sensorAlarmDetails", "Details", PageWidget.WidgetType.FIXED_SUMMARY_FIELDS_WIDGET, "webfixedsummaryfieldswidget_6_9", 0, 0, null, getSensorAlarmSummaryWidgetDetails(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, app))
                .widgetDone()
                .addWidget("sensorAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_3_3",9,0,null,null)
                .widgetDone()
                .addWidget("sensorAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_3_3", 9,3, null, null)
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
                .addWidget("sensorAlarmOccurrenceHistory", "Sensor Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_11_12", 0, 0, null, null)
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

        List<String> sensorAlarmFilterFields = new ArrayList<>(ALARM_EXCLUDE_FIELDS);
        sensorAlarmFilterFields.addAll(Arrays.asList("lastOccurredTime", "lastCreatedTime", "readingFieldId", "resource"));

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, readingField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 1, 4, 1);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        List<FacilioField> filteredFields = fields.stream().filter(field -> !sensorAlarmFilterFields.contains(field.getName())).collect(Collectors.toList());

        int columnNo = 1, rowNo = 1;
        Stack<FacilioField> stack = new Stack<>();
        for (FacilioField field : filteredFields) {
            if((FieldType.BIG_STRING.equals(field.getDataTypeEnum())) || (FieldType.LARGE_TEXT.equals(field.getDataTypeEnum()))) {
                stack.push(field);
                continue;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        while (!stack.isEmpty()) {
            FacilioField bigStringField = stack.pop();
            if(columnNo > 1) {
                columnNo = 1;
                rowNo ++;
            }
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, bigStringField, rowNo, columnNo, 4);
            rowNo++;
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
