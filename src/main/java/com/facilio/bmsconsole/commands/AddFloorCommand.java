package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddFloorCommand extends FacilioCommand {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		FloorContext floor = (FloorContext) context.get(FacilioConstants.ContextNames.FLOOR);
		if(floor != null) 
		{
			floor.setSpaceType(SpaceType.FLOOR);
			updateSiteId(floor);
			//Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<FloorContext> builder = new InsertRecordBuilder<FloorContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				builder.withChangeSet();
			}
															
			long id = builder.insert(floor);
			floor.setId(id);
			SpaceAPI.updateHelperFields(floor);
			
			if (withChangeSet != null && withChangeSet) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, builder.getChangeSet());
			}
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
			context.put(FacilioConstants.ContextNames.PARENT_ID, id);

		}
		else 
		{
			throw new IllegalArgumentException("Floor Object cannot be null");
		}
		return false;
	}
	
	private void updateSiteId(FloorContext floor) throws Exception {
		if(floor.getBuilding() != null) {
			long buildingId = floor.getBuilding().getId();
			BuildingContext building = SpaceAPI.getBuildingSpace(buildingId);
			floor.setSiteId(building.getSiteId());
		}
	}
}
