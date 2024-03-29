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
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

import static com.facilio.connected.CommonConnectedSummaryAPI.getSensorAlarmSystemPage;

public class SensorRollUpAlarmsModule extends BaseModuleConfig{
    public SensorRollUpAlarmsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtonsForSensorAlarm();
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> sensorAlarms = new ArrayList<FacilioView>();
        sensorAlarms.add(getSensorAlarm("sensorAlarm", "All Alarms", true).setOrder(order++));
        sensorAlarms.add(getSensorAlarmSeverity("sensorActive", "Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
        sensorAlarms.add(getSensorAlarmUnacknowledged().setOrder(order++));
        sensorAlarms.add(getSensorAlarmSeverity("sensorMajor", "Major Alarms", "Major", true).setOrder(order++));
        sensorAlarms.add(getSensorAlarmSeverity("sensorMinor", "Minor Alarms", "Minor", true).setOrder(order++));
        sensorAlarms.add(getSensorAlarmSeverity("sensorCritical", "Critical Alarms", "Critical", true).setOrder(order++));
        sensorAlarms.add(getSensorAlarmSeverity("sensorCleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
        sensorAlarms.add(getSensorMeterAlarm().setOrder(order++));
        sensorAlarms.add(getSensorNonMeterAlarm().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "sensorAlarmViews");
        groupDetails.put("displayName", "Sensor Alarms");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
        groupDetails.put("views", sensorAlarms);
        groupDetails.put("appLinkNames",Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getSensorAlarmSeverity(String name, String displayName, String severity, boolean equals) {

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
        view.setModuleName("sensorrollupalarm");
        view.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return view;
    }

    private static FacilioView getSensorAlarm(String name, String displayName, boolean equals) {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setModuleName("sensorrollupalarm");
        view.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        view.setDefault(true);

        return view;
    }

    private static FacilioView getSensorAlarmUnacknowledged() {
        Criteria criteria = getReadingAlarmUnacknowledgedCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("unacknowledgedSensorAlarm");
        typeAlarms.setDisplayName("Unacknowledged");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setModuleName("sensorrollupalarm");
        typeAlarms.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return typeAlarms;
    }

    private static FacilioView getSensorMeterAlarm() {
        Criteria criteria = getSensorMeterAlarmCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("sensorMeter");
        typeAlarms.setDisplayName("Meter Alarms");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setModuleName("sensorrollupalarm");
        typeAlarms.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return typeAlarms;
    }

    private static FacilioView getSensorNonMeterAlarm() {
        Criteria criteria = getSensorNonMeterAlarmCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("sensorNonMeter");
        typeAlarms.setDisplayName("Sensor Alarms");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setModuleName("sensorrollupalarm");
        typeAlarms.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return typeAlarms;
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

    public static Criteria getSensorMeterAlarmCriteria() {
        FacilioModule module = ModuleFactory.getSensorRollUpAlarmModule();
        FacilioField field = new FacilioField();
        field.setName("readingFieldId");
        field.setColumnName("READING_FIELD_ID");
        field.setDataType(FieldType.NUMBER);
        field.setModule(module);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(CommonOperators.IS_EMPTY);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);
        return criteria;

    }

    public static Criteria getSensorNonMeterAlarmCriteria() {
        FacilioModule module = ModuleFactory.getSensorRollUpAlarmModule();
        FacilioField field = new FacilioField();
        field.setName("readingFieldId");
        field.setColumnName("READING_FIELD_ID");
        field.setDataType(FieldType.NUMBER);
        field.setModule(module);

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperator(CommonOperators.IS_NOT_EMPTY);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);
        return criteria;
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

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();

        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            if(app != null){
                appNameVsPage.put(appName,getSensorAlarmSystemPage(app, false,  true));
            }
        }
        return appNameVsPage;
    }

    public static void addSystemButtonsForSensorAlarm() throws Exception {
        addAcknowledgeButton();
        addCreateWorkOrderButton();
        addViewWorkOrderButton();
        addCreateWorkOrderButtoninList();
        addViewWorkOrderButtoninList();
        SystemButtonApi.addListDeleteButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
    }

    public static void addCreateWorkOrderButtoninList() throws Exception {
        //BMS Alarm Create Workorder Button in List
        SystemButtonRuleContext createWorkorderSystemButton = NewAlarmAPI.getCreateWoSystemButton("alarm_create_workorder_list", CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, createWorkorderSystemButton);
    }

    public static void addViewWorkOrderButtoninList() throws Exception {
        //BMS Alarm View Workorder Button in List
        SystemButtonRuleContext viewWorkorderSystemButton = NewAlarmAPI.getViewWoSystemButton("alarm_view_workorder_list", CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, viewWorkorderSystemButton);
    }

    public static void addAcknowledgeButton() throws Exception {
        //Sensor Alarm Acknowledge Button
        SystemButtonRuleContext acknowledgeAlarmButton = NewAlarmAPI.getAcknowledgeAlarmSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, acknowledgeAlarmButton);
    }

    public static void addCreateWorkOrderButton() throws Exception {
        //Sensor Alarm Create Workorder Button
        SystemButtonRuleContext createWorkorderSystemButton = NewAlarmAPI.getCreateWoSystemButton("alarm_create_workorder", CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM,createWorkorderSystemButton);
    }
    public static void addViewWorkOrderButton() throws Exception {
        //Sensor Alarm View Workorder Button
        SystemButtonRuleContext viewWorkorderSystemButton = NewAlarmAPI.getViewWoSystemButton("alarm_view_workorder", CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM,viewWorkorderSystemButton);
    }
}
