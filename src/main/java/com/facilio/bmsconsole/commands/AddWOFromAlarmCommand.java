package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;

public class AddWOFromAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(EventType.ASSIGN_TICKET == eventType && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(dataTableName));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																	.connection(conn)
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.beanClass(AlarmContext.class)
																	.select(fields)
																	.andCondition(idCondition)
																	.orderBy("ID");
			
			List<AlarmContext> alarms = builder.get();
			List<Long> woIds = new ArrayList<>();
			if(alarms != null && !alarms.isEmpty()) {
				for(AlarmContext alarm : alarms) {
					woIds.add(addWorkOrder(alarm, conn));
				}
			}
		}
		return false;
	}
	
	private long addWorkOrder(AlarmContext alarm, Connection conn) throws Exception {
		WorkOrderContext wo = new WorkOrderContext();
		wo.setTicket(alarm.getTicket());
		wo.setCreatedTime(System.currentTimeMillis());
		
		//if(alarm.getType() == AlarmContext.AlarmType.LIFE_SAFETY.getIntVal()) 
		{
			TicketCategoryContext category = TicketAPI.getCategory(OrgInfo.getCurrentOrgInfo().getOrgid(), "Fire Safety");
			if(category != null) {
				wo.getTicket().setCategory(category);
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				UpdateRecordBuilder<TicketContext> updateBuilder = new UpdateRecordBuilder<TicketContext>()
						.connection(conn)
						.moduleName(FacilioConstants.ContextNames.TICKET)
						.table("Tickets")
						.fields(modBean.getAllFields(FacilioConstants.ContextNames.TICKET))
						.andCustomWhere("ID = ?", wo.getTicket().getId());
				
				updateBuilder.update(wo.getTicket());
			}
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		
		Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
		addWorkOrder.execute(context);
		
		return wo.getId();
	}

}
