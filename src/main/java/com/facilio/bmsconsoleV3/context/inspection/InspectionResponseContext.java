package com.facilio.bmsconsoleV3.context.inspection;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.qa.context.ResponseContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class InspectionResponseContext extends ResponseContext {
	
    private SiteContext site;
    private InspectionTemplateContext parent;
    private Long createdTime;
    private Status status;    
    
    public void setStatus(int status) {
    	this.status = Status.valueOf(status);
    }
    
    public int getStatus() {
    	return status.getVal();
    }
    
    public enum Status {
		PRE_OPEN,
		OPEN,
		;
		
		public int getVal() {
			return ordinal() + 1;
		}
		
		private static final Status[] TRIGGER_EXECUTION_SOURCE = Status.values();
		public static Status valueOf(int type) {
			if (type > 0 && type <= TRIGGER_EXECUTION_SOURCE.length) {
				return TRIGGER_EXECUTION_SOURCE[type - 1];
			}
			return null;
		}
	}
}
