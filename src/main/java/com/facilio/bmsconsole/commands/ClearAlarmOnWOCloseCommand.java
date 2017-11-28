package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ClearAlarmOnWOCloseCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ActivityType eventType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(ActivityType.CLOSE_WORK_ORDER == eventType && workOrder != null) {
			if(workOrder.getSourceType() == TicketContext.SourceType.ALARM.getIntVal()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.ALARM);
				SelectRecordsBuilder<AlarmContext> alarmBuilder = new SelectRecordsBuilder<AlarmContext>()
																	.connection(((FacilioContext) context).getConnectionWithTransaction())
																	.moduleName(FacilioConstants.ContextNames.ALARM)
																	.table("Alarms")
																	.select(modBean.getAllFields(FacilioConstants.ContextNames.ALARM))
																	.beanClass(AlarmContext.class)
																	.andCustomWhere(alarmModule.getTableName()+".ID = ?", workOrder.getId());
				
				List<AlarmContext> alarms = alarmBuilder.get();
				if(alarms != null && !alarms.isEmpty()) {
					AlarmContext alarm = alarms.get(0);
					if(alarm.getAlarmStatus() != AlarmContext.AlarmStatus.CLEAR.getIntVal()) {
						AlarmContext updatedAlarm = new AlarmContext();
						updatedAlarm.setAlarmStatus(AlarmContext.AlarmStatus.CLEAR);
						
						//Following should be moved to scheduler
						List<Long> ids = new ArrayList<>();
						ids.add(alarm.getId());
						
						ModuleCRUDBean moduleCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
						moduleCrudBean.updateAlarm(updatedAlarm, ids);
					}
				}
			}
		}
		return false;
	}

}
