package com.facilio.events.actions;

import java.util.List;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.connected.ConnectedCategoryContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateRange;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class V2AlarmAction extends FacilioAction {
    private static final long serialVersionUID = 1L;

    private List<Long> ids;

    private long id = -1;

    private long moduleId = -1;

    public String fetchAlarmSummary() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());

        FacilioChain chain = ReadOnlyChainFactory.getV2AlarmDetailsChain();
        chain.execute(context);

        setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
        setResult(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, context.get(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE));

        return SUCCESS;
    }

    private long readingFieldId = -1;
    private long categoryId = -1;
    private List<SensorRuleContext> typesToInactive;

    private List<SensorRuleContext> sensorRules;

    ConnectedCategoryContext connectedCategory;

    int nsType;

    public String fetchSensorRulesList() throws Exception {
        FacilioChain sensorRuleChain;
        sensorRuleChain = TransactionChainFactory.fetchSensorRuleChain();
        FacilioContext context = sensorRuleChain.getContext();
        constructListContext(context);
        context.put(ContextNames.READING_FIELD_ID, readingFieldId);
        context.put(ContextNames.CATEGORY_ID, categoryId);
        sensorRuleChain.execute();
        setResult(ContextNames.SENSOR_RULE_MODULE, context.get(ContextNames.SENSOR_RULE_MODULE));
        setResult("id",context.get(ContextNames.ID));

        return SUCCESS;
    }

    public String fetchAllSensorRules() throws Exception{
        FacilioChain fetchSensorRules=TransactionChainFactory.fetchAllSensorRules();
        FacilioContext context = fetchSensorRules.getContext();
        fetchSensorRules.execute();
        setResult(ContextNames.SENSOR_RULE_MODULE, context.get(ContextNames.SENSOR_RULE_MODULE));
        return SUCCESS;
    }
    public String fetchCategoryAlarmsDetails() throws Exception {
        FacilioChain facilioChain = TransactionChainFactoryV3.fetchConnectedCategoryStatusChain();
        FacilioContext context=facilioChain.getContext();
        context.put(ContextNames.CATEGORY_ID,categoryId);
        facilioChain.execute();
        setResult(ContextNames.CONNECTED_CATEGORY_DETAILS,context.get(ContextNames.CONNECTED_CATEGORY_DETAILS));
        setResult(ContextNames.COUNT,context.get(ContextNames.COUNT));
        return SUCCESS;
    }

    public String updateSensorRuleCategoryStatus() throws Exception {
        FacilioChain facilioChain = TransactionChainFactoryV3.updateConnectedCategoryStatusChain();
        FacilioContext context=facilioChain.getContext();
        context.put(ContextNames.CATEGORY_ID,categoryId);
        context.put(ContextNames.CONNECTED_CATEGORY_DETAILS,connectedCategory);
        context.put(ContextNames.TYPE,nsType);
        facilioChain.execute();
        setResult(ContextNames.CONNECTED_CATEGORY_DETAILS,context.get(ContextNames.CONNECTED_CATEGORY_DETAILS));
        return SUCCESS;
    }

    private AlarmOccurrenceContext alarmOccurrence;

    public String updateAlarmOccurrence() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getIds());
        context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE, getAlarmOccurrence());

        FacilioChain c = TransactionChainFactory.getV2UpdateAlarmChain();
        c.execute(context);

        return SUCCESS;
    }

    String alarmModule;

    private String occurrenceModule;

    private Boolean overrideViewOrderBy;

    public String alarmList() throws Exception {
        FacilioChain alarmListChain = ReadOnlyChainFactory.getV2AlarmListChain();

        FacilioContext context = alarmListChain.getContext();
        constructListContext(context);
        context.put(ContextNames.MODULE_NAME, alarmModule);

        alarmListChain.execute();

        if (isFetchCount()) {
            setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
        } else {
            setResult(ContextNames.ALARM_LIST, context.get(ContextNames.RECORD_LIST));
        }


        return SUCCESS;
    }

    public String occurrenceList() throws Exception {

        FacilioChain occurrenceListChain = ReadOnlyChainFactory.getV2OccurrenceListChain();
        FacilioContext context = occurrenceListChain.getContext();
        constructListContext(context);
        context.put(ContextNames.MODULE_NAME, occurrenceModule != null ? occurrenceModule : FacilioConstants.ContextNames.ALARM_OCCURRENCE);
        context.put(ContextNames.RECORD_ID, getId());
        context.put(ContextNames.FETCH_LOOKUPS, occurrenceModule != null);
        occurrenceListChain.execute();

        if (isFetchCount()) {
            setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
        } else {
            setResult(ContextNames.RECORD_LIST, context.get(ContextNames.RECORD_LIST));
        }

        return SUCCESS;
    }


    public String eventList() throws Exception {
        FacilioChain eventListChain = ReadOnlyChainFactory.getV2EventListChain();
        FacilioContext context = eventListChain.getContext();
        constructListContext(context);
        context.put(ContextNames.MODULE_NAME, ContextNames.BASE_EVENT);
        context.put(ContextNames.RECORD_ID, getId());
        context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, getOverrideViewOrderBy());

        eventListChain.execute();

        if (isFetchCount()) {
            setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
        } else {
            setResult(ContextNames.RECORD_LIST, context.get(ContextNames.RECORD_LIST));
        }

        return SUCCESS;
    }

    public String getAlarmOccurrenceList() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(ContextNames.RECORD_ID, getId());

        FacilioChain c = ReadOnlyChainFactory.getAlarmOccurrenceListChain();
        c.execute(context);

        setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));

        return SUCCESS;
    }

    public String getEventsList() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());

        FacilioChain c = ReadOnlyChainFactory.getEventListChain();
        c.execute(context);

        setResult(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));

        return SUCCESS;
    }

    public String deleteAlarm() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());

        FacilioChain c = TransactionChainFactory.getDeleteAlarmChain();
        c.execute(context);

        return SUCCESS;
    }

    public String deleteAlarmOccurrence() throws Exception {
        FacilioContext context = new FacilioContext();
        CommonCommandUtil.addEventType(EventType.DELETE, context);
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());

        FacilioChain c = TransactionChainFactory.getDeleteAlarmOccurrenceChain();
        c.execute(context);

        return SUCCESS;
    }

    public String rcaAlarms() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
        context.put(FacilioConstants.ContextNames.RULE_ID, getRuleId());
        context.put(FacilioConstants.ContextNames.DATE_RANGE, dateRange);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
        if ((Object) getPage() != null) {
            context.put(FacilioConstants.ContextNames.PAGE, getPage());
            context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        }
        FacilioChain c = TransactionChainFactory.getRcaAlarmDetails();
        c.execute(context);
        setResult(FacilioConstants.ContextNames.RCA_ALARMS, context.get(FacilioConstants.ContextNames.RCA_ALARMS));
        return SUCCESS;
    }

    private long ruleId;

    private int dateOperator = -1;

    private String dateOperatorValue;

    private DateRange dateRange;

    private WorkOrderContext workorder;

    public String createWO() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
        context.put(FacilioConstants.ContextNames.WORK_ORDER, getWorkorder());

        FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
        c.execute(context);

        WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        long woId = -1;
        if (wo != null) {
            woId = wo.getId();
        }
        setResult("woId", woId);

        return SUCCESS;
    }

}
