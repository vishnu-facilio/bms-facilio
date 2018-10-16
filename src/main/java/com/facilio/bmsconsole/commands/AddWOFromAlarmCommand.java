package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddWOFromAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
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
					WorkOrderContext wo = WorkOrderAPI.getWorkOrder(alarm.getId()); 
					if( wo == null) {
						workorders.add(AlarmAPI.getNewWOForAlarm(alarm));
					}
				}
				context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workorders);
			}
		}
		return false;
	}
}
