package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InspectionTriggerContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	InspectionTemplateContext parent;
	TriggerType type;
	BaseScheduleContext schedule;
	Long scheduleId;
	
	
	public void setType(int type) {
		this.type = TriggerType.valueOf(type);
	}
	
	public int getType() {
		return this.type.getVal();
	}
	
	public enum TriggerType {
		SCHEDULE,
		USER,
		;
		
		public int getVal() {
			return ordinal() + 1;
		}
		
		private static final TriggerType[] TRIGGER_EXECUTION_SOURCE = TriggerType.values();
		public static TriggerType valueOf(int type) {
			if (type > 0 && type <= TRIGGER_EXECUTION_SOURCE.length) {
				return TRIGGER_EXECUTION_SOURCE[type - 1];
			}
			return null;
		}
	}
}
