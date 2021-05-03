package com.facilio.bmsconsoleV3.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext.SourceType;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext.Status;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailFromAddress extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String displayName;
	String name;
	String email;
	boolean verificationStatus;
	SourceType sourceType;
	boolean activeStatus;
	CreationType creationType;
	
	public void setCreationType(Integer status) {
    	this.creationType = status == null ? null : CreationType.typeMap.get(status);
    }
    
    public Integer getCreationType() {
    	return creationType == null ? null : creationType.getIndex();
    }
    
    public Integer getSourceType() {
    	return sourceType == null ? null : sourceType.getIndex();
	}
	public void setSourceType(Integer type) {
		this.sourceType = type == null ? null : SourceType.typeMap.get(type);
	}
	
	public static enum SourceType implements FacilioIntEnum {
		
		NOTIFICATION,
		SUPPORT,
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
	
	
	public static enum CreationType implements FacilioIntEnum {
		
		DEFAULT,
		CUSTOM,
		;
		
		private static final Map<Integer, CreationType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, CreationType> initTypeMap() {
			Map<Integer, CreationType> typeMap = new HashMap<>();
			
			for(CreationType type : values()) {
				typeMap.put(type.getIndex(), type);
			}
			return typeMap;
		}
		public Map<Integer, CreationType> getAllTypes() {
			return typeMap;
		}
	}
}
