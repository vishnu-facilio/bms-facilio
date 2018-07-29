package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllZoneCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		SelectRecordsBuilder<ZoneContext> builder = new SelectRecordsBuilder<ZoneContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(ZoneContext.class)
				.select(fields)
				.orderBy("ID");
		List<Long> spaceId = new ArrayList<Long>();
		List<ZoneContext> zones = builder.get();
		for (ZoneContext zoeCont : zones) {
			if (zoeCont.getFloorId() > 0) {
				spaceId.add(zoeCont.getFloorId());
			}
			else if (zoeCont.getBuildingId()> 0) {
				spaceId.add(zoeCont.getBuildingId());

			}
			else if (zoeCont.getSiteId() > 0) {
				spaceId.add(zoeCont.getSiteId());
			}
		}
		Map<Long, BaseSpaceContext> mapSpaces = SpaceAPI.getBaseSpaceMap(spaceId);
		for (ZoneContext zoeCont : zones) {
			if (zoeCont.getFloorId() > 0) {
				zoeCont.setBaseSpaceContext(mapSpaces.get(zoeCont.getFloorId()));
			}
			else if (zoeCont.getBuildingId()> 0) {
				zoeCont.setBaseSpaceContext(mapSpaces.get(zoeCont.getBuildingId()));

			}
			else if (zoeCont.getSiteId() > 0) {
				zoeCont.setBaseSpaceContext(mapSpaces.get(zoeCont.getSiteId()));
			}
		}
		
		context.put(FacilioConstants.ContextNames.ZONE_LIST, zones);
		
		return false;
	}

}
