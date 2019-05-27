package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.ZoneAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddZoneCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		List<Long> children = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(zone != null && children != null && !children.isEmpty()) 
		{
			if (context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE) != null) {
			boolean isTenantZone =(Boolean) context.get(FacilioConstants.ContextNames.IS_TENANT_ZONE);
			if(isTenantZone) {
				ZoneAPI.validateZoneChildren(children,siteId);
				ZoneAPI.checkChildrenOccupancy(children, -1);
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
			
			InsertRecordBuilder<ZoneContext> builder = new InsertRecordBuilder<ZoneContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
															
			long zoneId = builder.insert(zone);
			zone.setId(zoneId);
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
