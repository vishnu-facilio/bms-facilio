package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class MlAnomalyAlarmModule extends BaseModuleConfig{
    public MlAnomalyAlarmModule(){
        setModuleName(FacilioConstants.ContextNames.ML_ANOMALY_ALARM);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> mlAnomalyAlarm = new ArrayList<FacilioView>();
        mlAnomalyAlarm.add(getMLAnomalyViews().setOrder(order++));
        mlAnomalyAlarm.add(getAnomalyAlarmSeverity("activeAnomaly", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
        mlAnomalyAlarm.add(getAnomalyAlarmSeverity("criticalAnomaly", "Critical", "Critical", true).setOrder(order++));
        mlAnomalyAlarm.add(getAnomalyAlarmSeverity("majorAnomaly", "Major", "Major", true).setOrder(order++));
        mlAnomalyAlarm.add(getAnomalyAlarmSeverity("minorAnomaly", "Minor", "Minor", true).setOrder(order++));
        mlAnomalyAlarm.add(getAnomalyAlarmSeverity("clearedAnomaly", "Cleared", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ML_ANOMALY_ALARM);
        groupDetails.put("views", mlAnomalyAlarm);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getMLAnomalyViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        return allView;
    }

    private static FacilioView getAnomalyAlarmSeverity(String name, String displayName, String severity, boolean equals) {

        Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(alarmCondition);

        FacilioField lastOccurredTime = new FacilioField();
        lastOccurredTime.setName("lastOccurredTime");
        lastOccurredTime.setDataType(FieldType.DATE_TIME);
        lastOccurredTime.setColumnName("LAST_OCCURRED_TIME");
        lastOccurredTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setModuleName("mlAnomalyAlarm");
        view.setSortFields(Arrays.asList(new SortField(lastOccurredTime, false)));

        return view;
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
}
