package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.ZoneAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateZoneCommand implements Command {

	@SuppressWarnings("unchecked")
	public boolean execute(Context context) throws Exception {
		

		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		List<Long> children = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(zone != null && children != null && !children.isEmpty()) 
		{
			if (context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE) != null) {
				boolean isTenantZone =(Boolean) context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE);
				if(isTenantZone) {
					ZoneAPI.validateZoneChildren(children, siteId);
					ZoneAPI.checkChildrenOccupancy(children,zone.getId());
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
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	
	

}
