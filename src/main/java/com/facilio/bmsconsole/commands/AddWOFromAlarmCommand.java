package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddWOFromAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(ActivityType.ASSIGN_TICKET == eventType && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			String ids = StringUtils.join(recordIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(FieldFactory.getIdField(module));
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
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
					woIds.add(addWorkOrder(alarm));
				}
			}
		}
		return false;
	}
	
	private long addWorkOrder(AlarmContext alarm) throws Exception {
		WorkOrderContext wo = getWorkOrderFromTicketId(alarm.getId()); 
		
		if( wo == null) {
			wo = new WorkOrderContext();
			BeanUtils.copyProperties(wo, alarm);
			wo.setCreatedTime(System.currentTimeMillis());
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			context.put(FacilioConstants.ContextNames.INSERT_LEVEL, 2);
			
			Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(context);
			
			return wo.getId();
		}
		return -1;
	}
	
	private WorkOrderContext getWorkOrderFromTicketId(long ticketId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.table("WorkOrders")
														.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCustomWhere(module.getTableName()+".ID = ?", ticketId);
		
		List<WorkOrderContext> workOrders = builder.get();
		if(workOrders != null && !workOrders.isEmpty()) {
			return workOrders.get(0);
		}
		return null;
	}

}
