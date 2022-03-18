package com.facilio.bmsconsoleV3.context.floorplan;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.IndoorFloorPlanContext.FloorPlanType;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.V3Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class V3IndoorFloorPlanContext extends V3Context {


	public FloorContext getFloor() {
		return floor;
	}
	public void setFloor(FloorContext floor) {
		this.floor = floor;
	}
	
	private FloorContext floor;
	
	private BuildingContext building;
	
	public BuildingContext getBuilding() {
		return building;
	}
	public void setBuilding(BuildingContext building) {
		this.building = building;
	}
	

	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
	}

	private SiteContext site;

	
	private String name;
	private String description;
	public Long getWidth() {
		return width;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public Long getHeight() {
		return height;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	private Long width;
	private Long height;
	private Long startTime;
	private Long endTime;
	private Long fileId;
	private FileSource fileSource;
	private String geometry;
	private String properties;
	private Boolean isActive;
	private String customizationJSON;
	private String customizationBookingJSON;


	private V3FloorplanCustomizationContext customization;
	private V3FloorplanCustomizationContext customizationBooking;

	public V3FloorplanCustomizationContext getCustomizationBooking() {
		if (customizationBooking == null) {
			return new V3FloorplanCustomizationContext();
		}
		return customizationBooking;
	}

	public void setCustomizationBooking(V3FloorplanCustomizationContext customizationBooking) {
		this.customizationBooking = customizationBooking;
	}

	public void setCustomizationBookingJSON(String customizationString) throws Exception {
		if (customizationString != null && !customizationString.trim().isEmpty()) {
			JSONObject customizationJsonObj = (JSONObject) new JSONParser().parse(customizationString);
			this.customizationBooking = FieldUtil.getAsBeanFromJson(customizationJsonObj, V3FloorplanCustomizationContext.class);
			this.customizationBookingJSON=customizationString;
		}
	}

	public String getCustomizationBookingJSON() throws Exception {
		if (getCustomizationBooking() != null) {
			return FieldUtil.getAsJSON(getCustomizationBooking()).toJSONString();
		}
		return customizationBookingJSON;
	}

	public void setCustomization(V3FloorplanCustomizationContext customization) {
		this.customization = customization;
	}

	public V3FloorplanCustomizationContext getCustomization() {
		if (customization == null) {
			return new V3FloorplanCustomizationContext();
		}
		return customization;
	}

	public void setCustomizationJSON(String customizationString) throws Exception {
		if (customizationString != null && !customizationString.trim().isEmpty()) {
			JSONObject customizationJsonObj = (JSONObject) new JSONParser().parse(customizationString);
			this.customization = FieldUtil.getAsBeanFromJson(customizationJsonObj, V3FloorplanCustomizationContext.class);
			this.customizationJSON=customizationString ;
		}
	}

	public String getCustomizationJSON() throws Exception {
		if (getCustomization() != null) {
			return FieldUtil.getAsJSON(getCustomization()).toJSONString();
		}
		return customizationJSON;
	}

    public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileFileName() {
		return fileFileName;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	public String getFileContentType() {
		return fileContentType;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	private String fileUrl;
    private File file;
    private String fileFileName;
    private String fileContentType;


	public List<V3MarkerContext> getMarkers() {
		return markers;
	}
	public void setMarkers(List<V3MarkerContext> markers) {
		this.markers = markers;
	}

	List<V3MarkerContext> markers;
	
	public List<V3MarkerdZonesContext> getMarkedZones() {
		return markedZones;
	}
	public void setMarkedZones(List<V3MarkerdZonesContext> markedZones) {
		this.markedZones = markedZones;
	}

	List<V3MarkerdZonesContext> markedZones;

	
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
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
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
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
	public String getGeometry() {
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
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
