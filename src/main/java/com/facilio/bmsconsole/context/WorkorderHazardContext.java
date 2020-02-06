package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class WorkorderHazardContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorkOrderContext workorder;
	private HazardContext hazard;
	public HazardContext getHazard() {
		return hazard;
	}
	public void setHazard(HazardContext hazard) {
		this.hazard = hazard;
	}
	public WorkOrderContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(WorkOrderContext workorder) {
		this.workorder = workorder;
	}
	
}
