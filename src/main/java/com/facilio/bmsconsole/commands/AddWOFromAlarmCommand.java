package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddWOFromAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		AlarmContext oldalarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(oldalarm.isWoCreated() && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			SelectRecordsBuilder<AlarmContext> builder = new SelectRecordsBuilder<AlarmContext>()
																	.module(module)
																	.beanClass(AlarmContext.class)
																	.select(fields)
																	.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
																	.orderBy("ID");
			
			List<AlarmContext> alarms = builder.get();
			List<WorkOrderContext> workorders = new ArrayList<>();
			if(alarms != null && !alarms.isEmpty()) {
				for(AlarmContext alarm : alarms) {
					workorders.add(addWorkOrder(alarm));
				}
				context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workorders);
			}
		}
		return false;
	}
	
	private WorkOrderContext addWorkOrder(AlarmContext alarm) throws Exception {
		WorkOrderContext wo = getWorkOrderFromTicketId(alarm.getId()); 
		
		if( wo == null) {
			wo = new WorkOrderContext();
			BeanUtils.copyProperties(wo, alarm);
			wo.setCreatedTime(System.currentTimeMillis());
			return wo;
		}
		return null;
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
