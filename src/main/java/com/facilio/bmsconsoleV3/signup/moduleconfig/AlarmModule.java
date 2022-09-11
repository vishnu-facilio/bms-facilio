package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class AlarmModule extends BaseModuleConfig{
    public AlarmModule(){
        setModuleName(FacilioConstants.ContextNames.ALARM);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> alarm = new ArrayList<FacilioView>();
        alarm.add(getSeverityAlarms("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++).setOrder(order++));
        alarm.add(getUnacknowledgedAlarms().setOrder(order++));
        alarm.add(getSeverityAlarms("critical", "Critical Alarms", "Critical", true).setOrder(order++));
        alarm.add(getSeverityAlarms("major", "Major Alarms", "Major", true).setOrder(order++));
        alarm.add(getSeverityAlarms("minor", "Minor Alarms", "Minor", true).setOrder(order++));
        alarm.add(getTypeAlarms("fire", "Fire Alarms", AlarmContext.AlarmType.FIRE).setOrder(order++));
        alarm.add(getTypeAlarms("energy", "Energy Alarms", AlarmContext.AlarmType.ENERGY).setOrder(order++));
        alarm.add(getTypeAlarms("hvac", "HVAC Alarms", AlarmContext.AlarmType.HVAC).setOrder(order++));
        alarm.add(getSeverityAlarms("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
        alarm.add(getSourceTypeAlarms("anomalies", "Anomalies", TicketContext.SourceType.ANOMALY_ALARM).setOrder(order++));
        alarm.add(getAllAlarms().setOrder(order++));
        alarm.add(getReportView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ALARM);
        groupDetails.put("views", alarm);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getSeverityAlarms(String name, String displayName, String severity, boolean equals) {

        Condition alarmCondition = getAlarmSeverityCondition(severity, equals);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(alarmCondition);
        criteria.andCriteria(getCommonAlarmCriteria());

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.DATE_TIME);
        modifiedTime.setModule(ModuleFactory.getAlarmsModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

        return view;
    }

    private static FacilioView getUnacknowledgedAlarms() {
        Criteria criteria = getUnacknowledgedAlarmCriteria();

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.DATE_TIME);
        modifiedTime.setModule(ModuleFactory.getAlarmsModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("unacknowledged");
        typeAlarms.setDisplayName("Unacknowledged");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

        return typeAlarms;
    }

    private static FacilioView getTypeAlarms(String name, String displayName, AlarmContext.AlarmType type) {
        Condition condition = new Condition();
        condition.setColumnName("Alarms.ALARM_TYPE");
        condition.setFieldName("alarmType");
        condition.setOperator(NumberOperators.EQUALS);
        condition.setValue(String.valueOf(type.getIntVal()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);
        criteria.andCriteria(getCommonAlarmCriteria());

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getAlarmsModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        activeAlarm.setOperator(LookupOperator.LOOKUP);
        activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

        criteria.addAndCondition(activeAlarm);

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.DATE_TIME);
        modifiedTime.setModule(ModuleFactory.getAlarmsModule());

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName(name);
        typeAlarms.setDisplayName(displayName);
        typeAlarms.setCriteria(criteria);
        typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));
        return typeAlarms;
    }

    private static FacilioView getSourceTypeAlarms(String name, String displayName,
                                                   TicketContext.SourceType sourceType) {

        FacilioModule module = ModuleFactory.getAlarmsModule();

        Criteria criteria = getSourceTypeCriteria(sourceType, true);

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.DATE_TIME);
        modifiedTime.setModule(module);

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

        return view;
    }

    private static FacilioView getAllAlarms() {

        FacilioField modifiedTime = new FacilioField();
        modifiedTime.setColumnName("MODIFIED_TIME");
        modifiedTime.setName("modifiedTime");
        modifiedTime.setDataType(FieldType.DATE_TIME);
        modifiedTime.setModule(ModuleFactory.getAlarmsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Alarms");
        allView.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

        return allView;
    }

    private static FacilioView getReportView() {

        FacilioView reportView = new FacilioView();
        reportView.setName("report");
        reportView.setHidden(true);

        return reportView;
    }

    public static Condition getAlarmSeverityCondition(String severity, boolean equals) {
        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getAlarmsModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition alarmCondition = new Condition();
        alarmCondition.setField(severityField);
        alarmCondition.setOperator(LookupOperator.LOOKUP);
        alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

        return alarmCondition;
    }

    public static Criteria getCommonAlarmCriteria() {
        return getSourceTypeCriteria(TicketContext.SourceType.ANOMALY_ALARM, false);
    }

    private static Criteria getSourceTypeCriteria(TicketContext.SourceType sourceType, boolean isEqual) {
        FacilioModule module = ModuleFactory.getTicketsModule();

        FacilioField sourceField = new FacilioField();
        sourceField.setName("sourceType");
        sourceField.setColumnName("SOURCE_TYPE");
        sourceField.setDataType(FieldType.NUMBER);
        sourceField.setModule(module);

        Condition sourceCondition = new Condition();
        sourceCondition.setField(sourceField);
        sourceCondition.setOperator(isEqual ? NumberOperators.EQUALS : NumberOperators.NOT_EQUALS);
        sourceCondition.setValue(String.valueOf(sourceType.getIndex()));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(sourceCondition);

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

    public static Criteria getUnacknowledgedAlarmCriteria() {
        Condition falseCondition = new Condition();
        falseCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
        falseCondition.setFieldName("isAcknowledged");
        falseCondition.setOperator(BooleanOperators.IS);
        falseCondition.setValue(String.valueOf(false));

        Condition emptyCondition = new Condition();
        emptyCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
        emptyCondition.setFieldName("isAcknowledged");
        emptyCondition.setOperator(CommonOperators.IS_EMPTY);

        Criteria criteria = new Criteria();
        criteria.addOrCondition(emptyCondition);
        criteria.addOrCondition(falseCondition);
        criteria.andCriteria(getCommonAlarmCriteria());

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getAlarmsModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        activeAlarm.setOperator(LookupOperator.LOOKUP);
        activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

        criteria.addAndCondition(activeAlarm);
        return criteria;
    }
}
