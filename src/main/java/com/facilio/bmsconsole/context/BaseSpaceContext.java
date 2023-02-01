package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.classification.context.ClassificationContext;
import com.facilio.constants.FacilioConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class BaseSpaceContext extends ResourceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseSpaceContext (long id) {
		super(id);
	}

	private double area = -1;
	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}
	
	private SpaceType spaceType;
	public int getSpaceType() {
		if(spaceType != null) {
			return spaceType.getIntVal();
		}
		return -1;
	}
	public void setSpaceType(int type) {
		this.spaceType = SpaceType.typeMap.get(type);
	}
	public void setSpaceType(SpaceType type) {
		this.spaceType = type;
	}
	public String getSpaceTypeVal() {
		if(spaceType != null) {
			return spaceType.getStringVal();
		}
		return null;
	}
	public SpaceType getSpaceTypeEnum() {
		return spaceType;
	}
	
	private float operationHoursStart = -1;
	public float getOperationHoursStart() {
		return operationHoursStart;
	}
	public void setOperationHoursStart(float operationHoursStart) {
		this.operationHoursStart = operationHoursStart;
	}
	
	private float operationHoursEnd = -1;
	public float getOperationHoursEnd() {
		return operationHoursEnd;
	}
	public void setOperationHoursEnd(float operationHoursEnd) {
		this.operationHoursEnd = operationHoursEnd;
	}
	
	private int maxOccupancy = -1;
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	
	private int lastCurrentOccupancy = -1;
	public int getLastCurrentOccupancy() {
		return lastCurrentOccupancy;
	}
	public void setLastCurrentOccupancy(int lastCurrentOccupancy) {
		this.lastCurrentOccupancy = lastCurrentOccupancy;
	}
	
	private long lastCurrentOccupancyTime = -1;
	public long getLastCurrentOccupancyTime() {
		return lastCurrentOccupancyTime;
	}
	public void setLastCurrentOccupancyTime(long lastCurrentOccupancyTime) {
		this.lastCurrentOccupancyTime = lastCurrentOccupancyTime;
	}
	
	private int lastAssignedOccupancy = -1;
	public int getLastAssignedOccupancy() {
		return lastAssignedOccupancy;
	}
	public void setLastAssignedOccupancy(int lastAssignedOccupancy) {
		this.lastAssignedOccupancy = lastAssignedOccupancy;
	}
	
	private long lastAssignedOccupancyTime = -1;
	public long getLastAssignedOccupancyTime() {
		return lastAssignedOccupancyTime;
	}
	public void setLastAssignedOccupancyTime(long lastAssignedOccupancyTime) {
		this.lastAssignedOccupancyTime = lastAssignedOccupancyTime;
	}
	
	public long getLocalId() {
		return super.getLocalId();
	}
	public void setLocalId(long localId) {
		super.setLocalId(localId);
	}

	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
		if (siteId > -1) {
			if (site == null) {
				site = new SiteContext();
			}
			site.setId(siteId);
		}
		else {
			site = null;
		}
	}
	
	private SiteContext site;
	public SiteContext getSite() {
		return site;
	}
	public void setSite(SiteContext site) {
		this.site = site;
		this.siteId = site != null ? site.getId() : -1;
	}

	private long buildingId = -1;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
		if (buildingId > -1) {
			if (building == null) {
				building = new BuildingContext();
			}
			building.setId(buildingId);
		}
		else {
			building = null;
		}
	}
	
	private BuildingContext building;
	public BuildingContext getBuilding() {
		return building;
	}
	public void setBuilding(BuildingContext building) {
		this.building = building;
		this.buildingId = building != null ? building.getId() : -1;
	}
	
	private long floorId = -1;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
		if (floorId > -1) {
			if (floor == null) {
				floor = new FloorContext();
			}
			floor.setId(floorId);
		}
		else {
			floor = null;
		}
	}
	private FloorContext floor;
	public FloorContext getFloor() {
		return floor;
	}
	public void setFloor(FloorContext floor) {
		this.floor = floor;
		this.floorId = floor != null ? floor.getId() : -1;
	}

	private  long spaceId1 = -1;
	public long getSpaceId1() {
		return spaceId1;
	}
	public void setSpaceId1(long spaceId1) {
		this.spaceId1 = spaceId1;
		if (spaceId1 > -1) {
			if (space1 == null) {
				space1 = new SpaceContext();
			}
			space1.setId(spaceId1);
		}
		else {
			space1 = null;
		}
	}
	private SpaceContext space1;
	public SpaceContext getSpace1() {
		return space1;
	}
	public void setSpace1(SpaceContext space1) {
		this.space1 = space1;
		this.spaceId1 = space1 != null ? space1.getId() : -1;
	}

	private  long spaceId2 = -1;
	public long getSpaceId2() {
		return spaceId2;
	}
	public void setSpaceId2(long spaceId2) {
		this.spaceId2 = spaceId2;
		if (spaceId2 > -1) {
			if (space2 == null) {
				space2 = new SpaceContext();
			}
			space2.setId(spaceId2);
		}
		else {
			space2 = null;
		}
	}
	private SpaceContext space2;
	public SpaceContext getSpace2() {
		return space2;
	}
	public void setSpace2(SpaceContext space2) {
		this.space2 = space2;
		this.spaceId2 = space2 != null ? space2.getId() : -1;
	}

	private  long spaceId3 = -1;
	public long getSpaceId3() {
		return spaceId3;
	}
	public void setSpaceId3(long spaceId3) {
		this.spaceId3 = spaceId3;
		if (spaceId3 > -1) {
			if (space3 == null) {
				space3 = new SpaceContext();
			}
			space3.setId(spaceId3);
		}
		else {
			space3 = null;
		}
	}
	private SpaceContext space3;
	public SpaceContext getSpace3() {
		return space3;
	}
	public void setSpace3(SpaceContext space3) {
		this.space3 = space3;
		this.spaceId3 = space3 != null ? space3.getId() : -1;
	}
	
	private  long spaceId4 = -1;
	public long getSpaceId4() {
		return spaceId4;
	}
	public void setSpaceId4(long spaceId4) {
		this.spaceId4 = spaceId4;
		if (spaceId4 > -1) {
			if (space4 == null) {
				space4 = new SpaceContext();
			}
			space4.setId(spaceId4);
		}
		else {
			space4 = null;
		}
	}
	private SpaceContext space4;
	public SpaceContext getSpace4() {
		return space4;
	}
	public void setSpace4(SpaceContext space4) {
		this.space4 = space4;
		this.spaceId4 = space4 != null ? space4.getId() : -1;
	}

	private SpaceContext space5;

	public SpaceContext getSpace5(){return space5;}
	public void setSpace5(SpaceContext space5) {
		this.space5 = space5;
		this.spaceId5 = space5 != null ? space5.getId() : -1;
	}

	private long spaceId5 = -1;
	public long getSpaceId5(){
		return spaceId5;
	}
	public void setSpaceId5(long spaceId5){
		this.spaceId5 = spaceId5;

		if (spaceId5 > -1) {
			if (space5 == null) {
				space5 = new SpaceContext();
			}
			space5.setId(spaceId5);
		}else {
			space5 = null;
		}
	}

	@Override
	public ResourceType getResourceTypeEnum() {
		return ResourceType.SPACE;
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getResourceType() {
		return ResourceType.SPACE.getValue();
	}
	
	public enum SpaceType {
		SITE(1, "Site",FacilioConstants.ContextNames.SITE),
		BUILDING(2, "Building",FacilioConstants.ContextNames.BUILDING),
		FLOOR(3, "Floor",FacilioConstants.ContextNames.FLOOR),
		SPACE(4, "Space",FacilioConstants.ContextNames.SPACE),
		ZONE(5, "Zone",FacilioConstants.ContextNames.ZONE)
		;
		
		private int intVal;
		private String strVal;
		private String moduleName;
		
		private SpaceType(int intVal, String strVal,String moduleName) {
			this.intVal = intVal;
			this.strVal = strVal;
			this.moduleName = moduleName;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		public String getModuleName() {
			return moduleName;
		}
		
		public static SpaceType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, SpaceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SpaceType> initTypeMap() {
			Map<Integer, SpaceType> typeMap = new HashMap<>();
			
			for(SpaceType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SpaceType> getAllTypes() {
			return typeMap;
		}
		
		private static final Map<String, SpaceType> moduleMap = Collections.unmodifiableMap(initModuleMap());
		private static Map<String, SpaceType> initModuleMap() {
			Map<String, SpaceType> typeMap = new HashMap<>();
			
			for(SpaceType type : values()) {
				typeMap.put(type.getModuleName(), type);
			}
			return typeMap;
		}
		public static Map<String, SpaceType> getModuleMap() {
			return moduleMap;
		}
	}
	@Getter
	@Setter
	private ClassificationContext classification;
}
