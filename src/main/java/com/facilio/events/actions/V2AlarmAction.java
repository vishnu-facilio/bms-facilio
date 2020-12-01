package com.facilio.events.actions;

import java.util.List;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.sensor.SensorRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.time.DateRange;

public class V2AlarmAction extends FacilioAction {
	private static final long serialVersionUID = 1L;
	
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long moduleId = -1;

	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public String fetchAlarmSummary() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
		
		FacilioChain chain = ReadOnlyChainFactory.getV2AlarmDetailsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, context.get(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE));
		
		return SUCCESS;
	}
	
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	private long readingFieldId = -1;
	private long categoryId = -1;
	private List<SensorRuleContext> typesToInactive;


	public List<SensorRuleContext> getTypesToInactive() {
		return typesToInactive;
	}
	public void setTypesToInactive(List<SensorRuleContext> typesToInactive) {
		this.typesToInactive = typesToInactive;
	}

	private List<SensorRuleContext> sensorRules;
	
	public List<SensorRuleContext> getSensorRules() {
		return sensorRules;
	}
	public void setSensorRules(List<SensorRuleContext> sensorRules) {
		this.sensorRules = sensorRules;
	}
	public String fetchSensorRulesList() throws Exception {
		
		FacilioChain sensorRuleChain = ReadOnlyChainFactory.getSensorRulesListChain();
 		FacilioContext context = sensorRuleChain.getContext();
 		constructListContext(context);
 		context.put(ContextNames.READING_FIELD_ID, readingFieldId);
 		context.put(ContextNames.CATEGORY_ID, categoryId);
 		sensorRuleChain.execute();
 		setResult(FacilioConstants.ContextNames.SENSOR_RULE_TYPES, context.get(FacilioConstants.ContextNames.SENSOR_RULE_TYPES));
		
		return SUCCESS;
	}
	
	public String updateSensorRulesList() throws Exception {

		FacilioChain updateSensorRulesChain = TransactionChainFactory.updateSensorRulesChain();
 		FacilioContext context = updateSensorRulesChain.getContext();
 		constructListContext(context);
 		context.put(ContextNames.SENSOR_RULE_TYPES, sensorRules);
 		context.put(ContextNames.RULE_TYPES, typesToInactive);
 		context.put(ContextNames.READING_FIELD_ID, readingFieldId);
 		context.put(ContextNames.CATEGORY_ID, categoryId);
 		context.put(ContextNames.MODULE_ID, moduleId);
 		updateSensorRulesChain.execute();
		
		return SUCCESS;
	}
	
	private AlarmOccurrenceContext alarmOccurrence;
	public AlarmOccurrenceContext getAlarmOccurrence() {
		return alarmOccurrence;
	}
	public void setAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence) {
		this.alarmOccurrence = alarmOccurrence;
	}
	
	public String updateAlarmOccurrence() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getIds());
		context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE, getAlarmOccurrence());
		
		FacilioChain c = TransactionChainFactory.getV2UpdateAlarmChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	String alarmModule;
	public String getAlarmModule() {
		return alarmModule;
	}
	public void setAlarmModule(String alarmModule) {
		this.alarmModule = alarmModule;
	}
	
	private String occurrenceModule;
	public String getOccurrenceModule() {
		return occurrenceModule;
	}
	public void setOccurrenceModule(String occurrenceModule) {
		this.occurrenceModule = occurrenceModule;
	}

	private Boolean overrideViewOrderBy;

	public Boolean getOverrideViewOrderBy() {
		return overrideViewOrderBy;
	}

	public void setOverrideViewOrderBy(Boolean overrideViewOrderBy) {
		this.overrideViewOrderBy = overrideViewOrderBy;
	}
	
	public String alarmList() throws Exception {
		FacilioChain alarmListChain = ReadOnlyChainFactory.getV2AlarmListChain();
		
 		FacilioContext context = alarmListChain.getContext();
 		constructListContext(context);
 		context.put(ContextNames.MODULE_NAME, alarmModule);
 		
		alarmListChain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
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
		}
		else {
			setResult(ContextNames.RECORD_LIST, context.get(ContextNames.RECORD_LIST));
		}
 		
		return SUCCESS;
	}
	
	
	public String eventList () throws Exception {
		FacilioChain eventListChain = ReadOnlyChainFactory.getV2EventListChain();
		FacilioContext context = eventListChain.getContext();
		constructListContext(context);
 		context.put(ContextNames.MODULE_NAME, ContextNames.BASE_EVENT);
 		context.put(ContextNames.RECORD_ID, getId());
		context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, getOverrideViewOrderBy());

		eventListChain.execute();
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
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
		FacilioChain c = TransactionChainFactory.getRcaAlarmDetails();
		c.execute(context);
		setResult(FacilioConstants.ContextNames.RCA_ALARMS, context.get(FacilioConstants.ContextNames.RCA_ALARMS));
		return SUCCESS;
	}

	public long getRuleId() {
		return ruleId;
	}

	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	private long ruleId;

	private int dateOperator = -1;
	public int getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(int dateOperator) {
		this.dateOperator = dateOperator;
	}

	private String dateOperatorValue;
	public String getDateOperatorValue() {
		return dateOperatorValue;
	}
	public void setDateOperatorValue(String dateOperatorValue) {
		this.dateOperatorValue = dateOperatorValue;
	}

	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	private WorkOrderContext workorder;
	public WorkOrderContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(WorkOrderContext workorder) {
		this.workorder = workorder;
	}
	
	public String createWO() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
		context.put(FacilioConstants.ContextNames.WORK_ORDER, getWorkorder());
		
		FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
		c.execute(context);
		
		return SUCCESS;
	}
	
}
