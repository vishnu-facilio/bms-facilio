package com.facilio.beans;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;

public interface ModuleCRUDBean {
	public long addWorkOrder(WorkOrderContext workorder) throws Exception;
	
	public long addAlarm(AlarmContext alarm) throws Exception;
}
