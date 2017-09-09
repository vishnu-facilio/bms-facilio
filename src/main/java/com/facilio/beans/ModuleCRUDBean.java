package com.facilio.beans;

import com.facilio.bmsconsole.context.WorkOrderContext;

public interface ModuleCRUDBean {
	public long addWorkOrder(WorkOrderContext workorder) throws Exception;
}
