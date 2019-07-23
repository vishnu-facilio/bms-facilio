package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ZoneContext;

public class ZoneAPI {
	private static final Logger LOGGER = Logger.getLogger(ZoneAPI.class.getName());
	
	public static void validateZoneChildren(List<Long> childrenIds, long siteId) throws Exception {
		List<BaseSpaceContext> children = SpaceAPI.getBaseSpaces(childrenIds);
		
		for(BaseSpaceContext child : children) {
			if(child.getSiteId() != siteId) {
				throw new IllegalArgumentException("The selected space "+ child.getName()+" doesnt belong to the site chosen ");
			}
			if(child.getId() != child.getBuildingId() && childrenIds.contains(child.getBuildingId())) {
				throw new IllegalArgumentException("Parent Building of " + child.getName() + " is already chosen");
			}
			else if(child.getId() != child.getFloorId() && childrenIds.contains(child.getFloorId())) {
				throw new IllegalArgumentException("Parent Floor of " + child.getName() + " is already chosen");
		    }
			else if(child.getId() != child.getSpaceId1() && childrenIds.contains(child.getSpaceId1())) {
				throw new IllegalArgumentException("Parent Space of " + child.getName() + " is already chosen");
			}
			else if(child.getId() != child.getSpaceId2() && childrenIds.contains(child.getSpaceId2())) {
				throw new IllegalArgumentException("Parent Space of " + child.getName() + " is already chosen");
			}
			else if(child.getId() != child.getSpaceId3() && childrenIds.contains(child.getSpaceId3())) {
				throw new IllegalArgumentException("Parent Space of " + child.getName() + " is already chosen");
			}
			else if(child.getId() != child.getSpaceId4() && childrenIds.contains(child.getSpaceId4())) {
				throw new IllegalArgumentException("Parent Space of " + child.getName() + " is already chosen");
			}
		}
	}
	
	public static void checkChildrenOccupancy(List<Long> childrenIds,long zoneId) throws Exception {
		List<BaseSpaceContext> childrenObj = SpaceAPI.getBaseSpaces(childrenIds);
		List<ZoneContext> tenantZones = SpaceAPI.getTenantZones();
		List<Long> zoneIds = new ArrayList<Long>();
		for(int j=0;j<tenantZones.size();j++) {
			if(zoneId == -1 || tenantZones.get(j).getId() != zoneId) {
			  zoneIds.add(tenantZones.get(j).getId());
			}
		}
		Map<Long,BaseSpaceContext> spacesMap = new HashMap<Long, BaseSpaceContext>();
		Map<Long,BaseSpaceContext> parentMap = new HashMap<Long, BaseSpaceContext>();
		
		List<BaseSpaceContext> children = SpaceAPI.getZoneChildren(zoneIds);
		for(int i=0;i<children.size();i++)
		{
			spacesMap.put(children.get(i).getId(), children.get(i));
		}
		for(int i=0;i<children.size();i++)
		{
			Long id = children.get(i).getBuildingId() != -1 ? children.get(i).getBuildingId() : children.get(i).getFloorId() != -1 ? children.get(i).getFloorId() : children.get(i).getSpaceId1() != -1 ? children.get(i).getSpaceId1() : children.get(i).getSpaceId2() != -1 ? children.get(i).getSpaceId2() : children.get(i).getSpaceId3() != -1 ? children.get(i).getSpaceId3() : children.get(i).getSpaceId4() != -1 ? children.get(i).getSpaceId4() : -1;
			if(id != -1) {
			parentMap.put(id, children.get(i));
			}
		}
		for(BaseSpaceContext newTenantChild: childrenObj) {
			if (spacesMap.containsKey(newTenantChild.getId())) {
				throw new IllegalArgumentException(newTenantChild.getName()+" occupied by another tenant");
			}
			else if(spacesMap.containsKey(newTenantChild.getBuildingId()) || spacesMap.containsKey(newTenantChild.getFloorId()) || spacesMap.containsKey(newTenantChild.getSpaceId1()) || spacesMap.containsKey(newTenantChild.getSpaceId2()) || spacesMap.containsKey(newTenantChild.getSpaceId3()) || spacesMap.containsKey(newTenantChild.getSpaceId4())) {
				throw new IllegalArgumentException("The Parent of "+newTenantChild.getName()+" is already occupied by another tenant");
			}
			else if(parentMap.containsKey(newTenantChild.getId())) {
				throw new IllegalArgumentException("A part of "+newTenantChild.getName()+" is already occupied by another tenant");
			}
		}
	}

}
