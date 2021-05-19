package com.facilio.bmsconsoleV3.context.inspection;

import java.util.List;

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
	List<InspectionTriggerIncludeExcludeResourceContext> resInclExclList;
	
	
	public void setType(int type) {
		this.type = TriggerType.valueOf(type);
	}
	
	public int getType() {
		if(this.type != null) {
			return this.type.getVal();
		}
		return -1;
	}
	
	public long getParentId() {
		return parent == null ? -1 : parent.getId();
	}
	
	public enum TriggerType {
		SCHEDULE("Schedule"),
		USER("Manual"),
		;
		
		String name;
		
		public String getName() {
			return name;
		}
		
		TriggerType (String name) {
			this.name = name;
		}
		
		
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
