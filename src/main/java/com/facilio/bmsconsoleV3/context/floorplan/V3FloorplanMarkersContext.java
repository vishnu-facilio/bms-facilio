

package com.facilio.bmsconsoleV3.context.floorplan;
import com.facilio.v3.context.V3Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext.DeskType;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.fasterxml.jackson.annotation.JsonIgnore;
public class V3FloorplanMarkersContext  extends V3Context {


	private String name;
	private String description;
	private Long fileId;
	private String avatarUrl;
		
	private Long recordModuleId;
	public Long getRecordModuleId() {
		return recordModuleId;
	}
	public void setRecordModuleId(Long recordModuleId) {
		this.recordModuleId = recordModuleId;
	}
	public Boolean getIsAutoCreate() {
		return isAutoCreate;
	}
	public void setIsAutoCreate(Boolean isAutoCreate) {
		this.isAutoCreate = isAutoCreate;
	}
	public Boolean getEnableNumbering() {
		return enableNumbering;
	}
	public void setEnableNumbering(Boolean enableNumbering) {
		this.enableNumbering = enableNumbering;
	}

	private Boolean isAutoCreate;
	private Boolean enableNumbering;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}


	@JsonIgnore
	public String getAvatarUrl() throws Exception {	
		if (avatarUrl == null && this.fileId > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			avatarUrl = fs.getPrivateUrl(this.fileId);
		}
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	
	
    private Type type;

    public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getTypeVal() {
		if(type != null) {
			return type.getStringVal();
		}
		return null;
	}
	public void setType(int type) {
		this.type = Type.typeMap.get(type);
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getFileSourceEnum() {
		return type;
	}
	public static enum Type {
		
		STATIC(1, "Static"),
		DYNAMIC(2, "Dynamic");

		private int intVal;
		private String strVal;
		
		private Type(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, Type> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();
			
			for(Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, Type> getAllTypes() {
			return typeMap;
		}
	}
}
