package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;

public class AddWOFromAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(EventType.ASSIGN_ALARM == eventType && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(dataTableName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																	.connection(((FacilioContext) context).getConnectionWithoutTransaction())
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.beanClass(AlarmContext.class)
																	.select(fields)
																	.orderBy("ID");
			
			List<AlarmContext> alarms = builder.get();
			List<Long> woIds = new ArrayList<>();
			if(alarms != null && !alarms.isEmpty()) {
				for(AlarmContext alarm : alarms) {
					woIds.add(addWorkOrder(alarm));
				}
			}
		}
		return false;
	}
	
	private long addWorkOrder(AlarmContext alarm) throws Exception {
		WorkOrderContext wo = new WorkOrderContext();
		wo.setTicket(alarm.getTicket());
		wo.setCreatedTime(System.currentTimeMillis());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		return wo.getId();
	}

}
