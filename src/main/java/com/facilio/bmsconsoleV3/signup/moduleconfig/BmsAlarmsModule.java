package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

import static com.facilio.connected.CommonConnectedSummaryAPI.getBMSAlarmSystemPage;

public class BmsAlarmsModule extends BaseModuleConfig {
    public BmsAlarmsModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.BMS_ALARM);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtonsForBMSAlarm();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> bmsAlarms = new ArrayList<FacilioView>();
        bmsAlarms.add(getBmsAlarm("bmsAlarm", "All Alarms", true).setOrder(order++));
        bmsAlarms.add(getBmsAlarmSeverity("bmsActive", "Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
        bmsAlarms.add(getBmsAlarmUnacknowledged().setOrder(order++));
        bmsAlarms.add(getBmsAlarmSeverity("bmsMajor", "Major Alarms", "Major", true).setOrder(order++));
        bmsAlarms.add(getBmsAlarmSeverity("bmsMinor", "Minor Alarms", "Minor", true).setOrder(order++));
        bmsAlarms.add(getBmsAlarmSeverity("bmsCritical", "Critical Alarms", "Critical", true).setOrder(order++));
        bmsAlarms.add(getBmsAlarmSeverity("bmsCleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "bmsAlarmsViews");
        groupDetails.put("displayName", "BMS Alarms");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BMS_ALARM);
        groupDetails.put("views", bmsAlarms);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getBmsAlarm(String name, String displayName, boolean equals) {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setModuleName("bmsalarm");
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        view.setDefault(true);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getBmsAlarmSeverity(String name, String displayName, String severity, boolean equals) {

        Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(alarmCondition);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setModuleName("bmsalarm");
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getBmsAlarmUnacknowledged() {
        Criteria criteria = getReadingAlarmUnacknowledgedCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBmsAlarmModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("unacknowledgedbmsalarm");
        typeAlarms.setDisplayName("Unacknowledged");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setModuleName("bmsalarm");
        typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        typeAlarms.setAppLinkNames(appLinkNames);

        return typeAlarms;
    }

    public static Criteria getReadingAlarmUnacknowledgedCriteria() {
        Condition falseCondition = new Condition();
        falseCondition.setColumnName("ACKNOWLEDGED");
        falseCondition.setFieldName("acknowledged");
        falseCondition.setOperator(BooleanOperators.IS);
        falseCondition.setValue(String.valueOf(false));

        Condition emptyCondition = new Condition();
        emptyCondition.setColumnName("ACKNOWLEDGED");
        emptyCondition.setFieldName("acknowledged");
        emptyCondition.setOperator(CommonOperators.IS_EMPTY);

        Criteria criteria = new Criteria();
        criteria.addOrCondition(emptyCondition);
        criteria.addOrCondition(falseCondition);

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getBaseAlarmModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        activeAlarm.setOperator(LookupOperator.LOOKUP);
        activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

        criteria.addAndCondition(activeAlarm);
        return criteria;
    }

    private static Criteria getSeverityAlarmCriteria(String severity, boolean equals) {
        FacilioField severityField = new FacilioField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.STRING);
        severityField.setModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        if (equals) {
            activeAlarm.setOperator(StringOperators.IS);
        } else {
            activeAlarm.setOperator(StringOperators.ISN_T);
        }
        activeAlarm.setValue(severity);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(activeAlarm);

        return criteria;
    }

    public static Condition getReadingAlarmSeverityCondition(String severity, boolean equals) {
        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getBaseAlarmModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition alarmCondition = new Condition();
        alarmCondition.setField(severityField);
        alarmCondition.setOperator(LookupOperator.LOOKUP);
        alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

        return alarmCondition;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();

        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            if (app != null) {
                appNameVsPage.put(appName, getBMSAlarmSystemPage(app, false, true));
            }
        }
        return appNameVsPage;
    }

    public static void addSystemButtonsForBMSAlarm() throws Exception {
        addAcknowledgeButton();
        addCreateWorkOrderButton();
        addViewWorkOrderButton();
        addExportCsvButton();
        addExportExcelButton();
    }

    public static void addAcknowledgeButton() throws Exception {
        //BMS Alarm Acknowledge Button
        SystemButtonRuleContext acknowledgeAlarmButton = NewAlarmAPI.getAcknowledgeAlarmSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BMS_ALARM, acknowledgeAlarmButton);
    }

    public static void addCreateWorkOrderButton() throws Exception {
        //BMS Alarm Create Workorder Button
        SystemButtonRuleContext createWorkorderSystemButton = NewAlarmAPI.getCreateWoSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BMS_ALARM, createWorkorderSystemButton);
    }

    public static void addViewWorkOrderButton() throws Exception {
        //BMS Alarm View Workorder Button
        SystemButtonRuleContext viewWorkorderSystemButton = NewAlarmAPI.getViewWoSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BMS_ALARM, viewWorkorderSystemButton);
    }

    public static void addExportCsvButton() throws Exception {
        //BMS Alarm Export CSV Button
        SystemButtonRuleContext exportCsvSystemButton = new SystemButtonRuleContext();
        exportCsvSystemButton.setIdentifier("bms_alarm_export_csv");
        exportCsvSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        exportCsvSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportCsvSystemButton.setName("Export CSV");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BMS_ALARM, exportCsvSystemButton);
    }

    public static void addExportExcelButton() throws Exception {
        //BMS Alarm Export CSV Button
        SystemButtonRuleContext exportExcelSystemButton = new SystemButtonRuleContext();
        exportExcelSystemButton.setIdentifier("bms_alarm_export_excel");
        exportExcelSystemButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        exportExcelSystemButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportExcelSystemButton.setName("Export Excel");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.BMS_ALARM, exportExcelSystemButton);
    }
}