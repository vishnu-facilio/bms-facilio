package com.facilio.bmsconsoleV3.context.inspection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioEnum;
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
    private SourceType sourceType;
    private ResourceContext resource;
    private VendorContext vendor;
    private TenantContext tenant;
    private InspectionCategoryContext category;
    private InspectionPriorityContext priority;
    private Group assignmentGroup;
    private User assignedTo;
    
    public void setStatus(int status) {
    	this.status = Status.valueOf(status);
    }
    
    public int getStatus() {
    	return status.getVal();
    }
    
    public int getSourceType() {
    	return sourceType.getIndex();
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
    
    public static enum SourceType implements FacilioEnum<SourceType>  {
		
		PLANNED(1, "Planned"),
		MANNUAL(2, "Mannual"),
		;
		
		private int intVal;
		private String strVal;
		
		private SourceType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}

		@Override
		public int getIndex() {
			return intVal;
		}

		public String getValue() {
			return strVal;
		}
		
		public static SourceType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, SourceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SourceType> initTypeMap() {
			Map<Integer, SourceType> typeMap = new HashMap<>();
			
			for(SourceType type : values()) {
				typeMap.put(type.getIndex(), type);
			}
			return typeMap;
		}
		public Map<Integer, SourceType> getAllTypes() {
			return typeMap;
		}
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
