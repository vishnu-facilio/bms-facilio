package com.facilio.events.actions;

import java.util.List;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;

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

	public String fetchAlarmSummary() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, getId());
		
		FacilioChain chain = ReadOnlyChainFactory.getV2AlarmDetailsChain();
		chain.execute(context);
		
		setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));
		setResult(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE, context.get(FacilioConstants.ContextNames.LATEST_ALARM_OCCURRENCE));
		
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
	
	public String alarmList() throws Exception {
 		FacilioContext context = constructListContext();
 		context.put(ContextNames.MODULE_NAME, alarmModule);
 		
 		FacilioChain alarmListChain = ReadOnlyChainFactory.getV2AlarmListChain();
		alarmListChain.execute(context);
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
			setResult(ContextNames.ALARM_LIST, context.get(ContextNames.RECORD_LIST));
		}
 		
		
		return SUCCESS;
	}
	
	public String occurrenceList() throws Exception {
		
		FacilioContext context = constructListContext();
 		context.put(ContextNames.MODULE_NAME, occurrenceModule != null ? occurrenceModule : FacilioConstants.ContextNames.ALARM_OCCURRENCE);
 		context.put(ContextNames.RECORD_ID, getId());
 		context.put(ContextNames.FETCH_LOOKUPS, occurrenceModule != null);
 		FacilioChain occurrenceListChain = ReadOnlyChainFactory.getV2OccurrenceListChain();
 		occurrenceListChain.execute(context);
		
		if (isFetchCount()) {
			setResult(ContextNames.COUNT, context.get(ContextNames.RECORD_COUNT));
		}
		else {
			setResult(ContextNames.RECORD_LIST, context.get(ContextNames.RECORD_LIST));
		}
 		
		return SUCCESS;
	}
	
	
	public String eventList () throws Exception {
		FacilioContext context = constructListContext();
 		context.put(ContextNames.MODULE_NAME, FacilioConstants.ContextNames.ALARM_OCCURRENCE);
 		context.put(ContextNames.RECORD_ID, getId());
 		FacilioChain occurrenceListChain = ReadOnlyChainFactory.getV2EventListChain();
 		occurrenceListChain.execute(context);
		
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
