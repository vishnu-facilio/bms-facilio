package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateZoneCommand implements Command {

	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		

		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		List<Long> children = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(zone != null && children != null && !children.isEmpty()) 
		{
			if (context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE) != null) {
				boolean isTenantZone =(Boolean) context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE);
				if(isTenantZone) {
					validateZoneChildren(children);
					checkChildrenOccupancy(children,zone.getId());
					zone.setTenantZone(true);
				}
				else {
					zone.setTenantZone(false);
				}
			}
			else {
				zone.setTenantZone(false);
			}
				
			zone.setSpaceType(SpaceType.ZONE);
			SpaceAPI.updateZoneInfo(zone, children);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			UpdateRecordBuilder<ZoneContext> updateBuilder = new UpdateRecordBuilder<ZoneContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getIdCondition(zone.getId(), module));
															
			updateBuilder.update(zone);
			SpaceAPI.deleteZoneChildren(zone.getId());
			SpaceAPI.updateHelperFields(zone);
			SpaceAPI.addZoneChildren(zone, children);
		}
		else 
		{
			throw new IllegalArgumentException("Zone Object cannot be null");
		}
		return false;
		
	}
	
	private void validateZoneChildren(List<Long> childrenIds) throws Exception {
		List<BaseSpaceContext> children = SpaceAPI.getBaseSpaces(childrenIds);
		for(BaseSpaceContext child : children) {
			if(child.getId() != child.getBuildingId() && childrenIds.contains(child.getBuildingId())) {
				throw new IllegalArgumentException("Parent Building of " + child.getName() + " is already chosen");
			}
			else if(child.getId() != child.getFloorId() && childrenIds.contains(child.getFloorId())) {
				throw new IllegalArgumentException("Parent Floor of " + child.getName() + " is already chosen");
		    }
			else if(child.getId() != child.getSpaceId() && childrenIds.contains(child.getSpaceId())) {
				throw new IllegalArgumentException("Parent Space of " + child.getName() + " is already chosen");
			}
		}
	}
	
	private void checkChildrenOccupancy(List<Long> childrenIds,long zoneId) throws Exception {
		List<BaseSpaceContext> childrenObj = SpaceAPI.getBaseSpaces(childrenIds);
		List<ZoneContext> tenantZones = SpaceAPI.getTenantZones();
		List<Long> zoneIds = new ArrayList<Long>();
		for(int j=0;j<tenantZones.size();j++) {
			if(tenantZones.get(j).getId() != zoneId) {
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
			Long id = children.get(i).getBuildingId() != -1 ? children.get(i).getBuildingId() : children.get(i).getFloorId() != -1 ? children.get(i).getFloorId() : children.get(i).getSpaceId() != -1 ? children.get(i).getSpaceId() : children.get(i).getSpaceId1() != -1 ? children.get(i).getSpaceId1() : children.get(i).getSpaceId2() != -1 ? children.get(i).getSpaceId2() : children.get(i).getSpaceId3() != -1 ? children.get(i).getSpaceId3() : children.get(i).getSpaceId4() != -1 ? children.get(i).getSpaceId4() : -1;
			if(id != -1) {
			parentMap.put(id, children.get(i));
			}
		}
		for(BaseSpaceContext newTenantChild: childrenObj) {
			if (spacesMap.containsKey(newTenantChild.getId())) {
				throw new IllegalArgumentException(newTenantChild.getName()+" occupied by another tenant");
			}
			else if(spacesMap.containsKey(newTenantChild.getBuildingId()) || spacesMap.containsKey(newTenantChild.getFloorId()) || spacesMap.containsKey(newTenantChild.getSpaceId()) || spacesMap.containsKey(newTenantChild.getSpaceId1()) || spacesMap.containsKey(newTenantChild.getSpaceId2()) || spacesMap.containsKey(newTenantChild.getSpaceId3()) || spacesMap.containsKey(newTenantChild.getSpaceId4())) {
				throw new IllegalArgumentException("The Parent of "+newTenantChild.getName()+" is already occupied by another tenant");
			}
			else if(parentMap.containsKey(newTenantChild.getId())) {
				throw new IllegalArgumentException("A part of "+newTenantChild.getName()+" is already occupied by another tenant");
			}
		}
	}


}
