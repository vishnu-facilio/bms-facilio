package com.facilio.bmsconsoleV3.context.induction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.qa.context.ResponseContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
@NoArgsConstructor
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NONE
)
public class InductionResponseContext extends ResponseContext <InductionTemplateContext> {

	public InductionResponseContext (Long id) {
		super(id);
	}
	
    private SiteContext site;
    private InductionTemplateContext parent;
    private Long createdTime;
    private Status status;
    private SourceType sourceType;
    private ResourceContext resource;
    private Group assignmentGroup;
    private User assignedTo;
    private Long scheduledWorkStart;
    private Long scheduledWorkEnd;
    private Long actualWorkStart;
    private Long actualWorkEnd;
    
    public void setStatus(Integer status) { // Using wrapper object to avoid -1 being inserted
    	this.status = status == null ? null : Status.valueOf(status);
    }
    
    public Integer getStatus() { // Using wrapper object to avoid -1 being inserted
    	return status == null ? null : status.getIndex();
    }
    
    public Integer getSourceType() { // Using wrapper object to avoid -1 being inserted
    	return sourceType == null ? null : sourceType.getIndex();
	}
	public void setSourceType(Integer type) { // Using wrapper object to avoid -1 being inserted
		this.sourceType = type == null ? null : SourceType.typeMap.get(type);
	}
    
    public static enum SourceType implements FacilioIntEnum {
		
		PLANNED,
		MANNUAL,
		;
		
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
    
    public enum Status implements FacilioIntEnum {
		PRE_OPEN,
		OPEN,
		;
		
		private static final Status[] TRIGGER_EXECUTION_SOURCE = Status.values();
		public static Status valueOf(int type) {
			if (type > 0 && type <= TRIGGER_EXECUTION_SOURCE.length) {
				return TRIGGER_EXECUTION_SOURCE[type - 1];
			}
			return null;
		}
	}
}