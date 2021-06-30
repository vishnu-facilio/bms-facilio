package com.facilio.bmsconsole.context;

import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext.FacilityType;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext.FloorPlanType;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndoorFloorPlanContext extends ModuleBaseWithCustomFields {
	
	public List<IndoorFloorPlanObjectContext> getFloorPlanObjects() {
		return floorPlanObjects;
	}

	public void setFloorPlanObjects(List<IndoorFloorPlanObjectContext> floorPlanObjects) {
		this.floorPlanObjects = floorPlanObjects;
	}

	List<IndoorFloorPlanObjectContext> floorPlanObjects;

	
	private long id;
	private long floorId;
	private long siteId;
	private long orgId;
	private long fileId;
	private String name;
	private String description;
	private FileSource fileSource;
	private int width;
	private int height;
	
	private Float iconSize;
	private Float fontSize;
	private int minZoom;
	private int maxZoom;

	public Float getIconSize() {
		return iconSize;
	}

	public void setIconSize(Float iconSize) {
		this.iconSize = iconSize;
	}

	public Float getFontSize() {
		return fontSize;
	}

	public void setFontSize(Float fontSize) {
		this.fontSize = fontSize;
	}

	public int getMinZoom() {
		return minZoom;
	}

	public void setMinZoom(int minZoom) {
		this.minZoom = minZoom;
	}

	public int getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}

	
	
	
	
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFloorId() {
		return floorId;
	}

	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}

	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

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

		
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}



	public int getFileSource() {
		if(fileSource != null) {
			return fileSource.getIntVal();
		}
		else {
			return -1;
		}
	}
	public String getFileSourceVal() {
		if(fileSource != null) {
			return fileSource.getStringVal();
		}
		return null;
	}
	public void setFileSource(int type) {
		this.fileSource = fileSource.typeMap.get(type);
	}
	public void setFileSource(FileSource type) {
		this.fileSource = type;
	}
	public FileSource getFileSourceEnum() {
		return fileSource;
	}
	public static enum FileSource {
		
		IMAGE(1, "Image"),
		AUTO_CAD(2, "AUTO CAD");

		private int intVal;
		private String strVal;
		
		private FileSource(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, FileSource> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, FileSource> initTypeMap() {
			Map<Integer, FileSource> typeMap = new HashMap<>();
			
			for(FileSource type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, FileSource> getAllTypes() {
			return typeMap;
		}
	}
	
	private FloorPlanType floorPlanType;
	public Integer getFloorPlanType() {
        if (floorPlanType != null) {
            return floorPlanType.getIndex();
        }
        return null;
    }
    public void setFloorPlanType(Integer floorPlanType) {
        if(floorPlanType != null) {
            this.floorPlanType = FloorPlanType.valueOf(floorPlanType);
        }
    }
    public FloorPlanType getFloorPlanTypeEnum() {
        return floorPlanType;
    }

    public static enum FloorPlanType implements FacilioIntEnum {
        WORKSTATION("Workstation"),
        LOCKERS("Lockers"),
        PARKING("Parking");

        private String name;

        FloorPlanType(String name) {
            this.name = name;
        }

        public static FloorPlanType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
        
        private static final Map<Integer, FloorPlanType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, FloorPlanType> initTypeMap() {
			Map<Integer, FloorPlanType> typeMap = new HashMap<>();
			
			for(FloorPlanType type : values()) {
				typeMap.put(type.getIndex(), type);
			}
			return typeMap;
		}
		public Map<Integer, FloorPlanType> getAllTypes() {
			return typeMap;
		}
    }


}
