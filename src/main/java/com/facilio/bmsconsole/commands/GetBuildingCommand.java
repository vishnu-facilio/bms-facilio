package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetBuildingCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long buildingId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(buildingId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
					.connection(conn)
					.dataTableName(dataTableName)
					.moduleName(moduleName)
					.beanClass(BuildingContext.class)
					.select(fields)
					.where("ID = ?", buildingId)
					.orderBy("ID");

			List<BuildingContext> buildings = builder.getAsBean();	
			if(buildings.size() > 0) {
				BuildingContext building = buildings.get(0);
				context.put(FacilioConstants.ContextNames.BUILDING, building);
				
				context.put(GetNotesCommand.MODULEID_COLUMN, "BUILDING_ID");
				context.put(GetNotesCommand.MODULE_ID, buildingId);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Building ID : "+buildingId);
		}
		
		return false;
	}

}
