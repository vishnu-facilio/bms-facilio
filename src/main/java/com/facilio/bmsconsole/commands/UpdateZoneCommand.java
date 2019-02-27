package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
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
				throw new IllegalArgumentException("Parent Building " + child.getName() + "is already chosen");
			}
			else if(child.getId() != child.getFloorId() && childrenIds.contains(child.getFloorId())) {
				throw new IllegalArgumentException("Parent Floor " + child.getName() + "is already chosen");
		    }
			else if(child.getId() != child.getSpaceId() && childrenIds.contains(child.getSpaceId())) {
				throw new IllegalArgumentException("Parent Space " + child.getName() + "is already chosen");
			}
		}
	}
	
	private void checkChildrenOccupancy(List<Long> childrenIds,long zoneId) throws Exception {
		List<BaseSpaceContext> childrenObj = SpaceAPI.getBaseSpaces(childrenIds);
		List<ZoneContext> tenantZones = SpaceAPI.getTenantZones();
		Map<Long,BaseSpaceContext> spacesMap = new HashMap<Long, BaseSpaceContext>();
		for(ZoneContext zone:tenantZones) {
			if(zone.getId() == zoneId)
			{
				continue;
			}
			List<BaseSpaceContext> children = SpaceAPI.getZoneChildren(zone.getId());
			for(int i=0;i<children.size();i++)
			{
				spacesMap.put(children.get(i).getId(), children.get(i));
			}
		}
		for(BaseSpaceContext newTenantChild: childrenObj) {
			if (spacesMap.containsKey(newTenantChild.getId())) {
				throw new IllegalArgumentException(newTenantChild.getName()+" occupied by another tenant");
			}
		}
		
	
	}
	


}
