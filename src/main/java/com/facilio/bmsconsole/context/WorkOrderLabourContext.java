package com.facilio.bmsconsole.context;

import com.facilio.bmsconsoleV3.context.CraftContext;
import com.facilio.bmsconsoleV3.context.SkillsContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class WorkOrderLabourContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private double cost = -1;
	private long startTime = -1;
	private long endTime = -1;
	private double duration = 0;
	private long parentId = -1;
	private LabourContext labour ;

	public void calculate() {
		if (this.duration == -1) {
			if (this.endTime == -1 || this.startTime == -1) {
				this.duration = 0;
			} else {
				this.duration = this.endTime - this.startTime;
			}
		}
		
		if (this.labour != null) {
			this.cost = (this.duration / 1000.0 / 60 / 60) * this.labour.getCost();
		}
	}

	private WorkOrderContext parent;
	private CraftContext craft;
	private SkillsContext skill;
	private Integer type;

}
