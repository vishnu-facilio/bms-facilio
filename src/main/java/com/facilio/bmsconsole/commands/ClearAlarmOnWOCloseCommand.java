package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ClearAlarmOnWOCloseCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(EventType.CLOSE_WORK_ORDER == eventType && workOrder != null) {
			if(workOrder.getTicket().getSourceType() == TicketContext.SourceType.ALARM.getIntVal()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				SelectRecordsBuilder<AlarmContext> alarmBuilder = new SelectRecordsBuilder<AlarmContext>()
																	.connection(((FacilioContext) context).getConnectionWithTransaction())
																	.moduleName(FacilioConstants.ContextNames.ALARM)
																	.table("Alarms")
																	.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM))
																	.beanClass(AlarmContext.class)
																	.andCustomWhere("TICKET_ID = ?", workOrder.getTicket().getId());
				
				List<AlarmContext> alarms = alarmBuilder.get();
				if(alarms != null && !alarms.isEmpty()) {
					AlarmContext alarm = alarms.get(0);
					AlarmContext updatedAlarm = new AlarmContext();
					updatedAlarm.setStatus(AlarmContext.AlarmStatus.CLEAR);
					
					//Following should be moved to scheduler
					List<Long> ids = new ArrayList<>();
					ids.add(alarm.getId());
					
					ModuleCRUDBean moduleCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
					moduleCrudBean.updateAlarm(updatedAlarm, ids);
				}
			}
		}
		return false;
	}

}
