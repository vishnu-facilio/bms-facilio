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
    private Long scheduledWorkStart;
    private Long scheduledWorkEnd;
    private Long actualWorkStart;
    private Long actualWorkEnd;
    
    public void setStatus(int status) {
    	this.status = Status.valueOf(status);
    }
    
    public int getStatus() {
    	if(status != null) {
    		return status.getIndex();
    	}
    	return -1;
    }
    
    public int getSourceType() {
    	if(sourceType != null) {
    		return sourceType.getIndex();
    	}
    	return -1;
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
    
    public static enum SourceType implements FacilioEnum<SourceType>  {
		
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
    
    public enum Status implements FacilioEnum<Status> {
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

	public void setParentWithProps(InspectionTemplateContext template) {
		
		setParent(template);
    	setSite(template.getSite());
    	setSiteId(template.getSiteId());
    	setVendor(template.getVendor());
    	setTenant(template.getTenant());
    	setCategory(template.getCategory());
    	setPriority(template.getPriority());
    	setAssignedTo(template.getAssignedTo());
    	setAssignmentGroup(template.getAssignmentGroup());
	}
}
