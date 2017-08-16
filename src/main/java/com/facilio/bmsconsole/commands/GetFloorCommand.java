package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetFloorCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long floorId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(floorId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<FloorContext> builder = new SelectRecordsBuilder<FloorContext>()
					.connection(conn)
					.dataTableName(dataTableName)
					.moduleName(moduleName)
					.beanClass(FloorContext.class)
					.select(fields)
					.where("ID = ?", floorId)
					.orderBy("ID");

			List<FloorContext> floors = builder.getAsBean();	
			if(floors.size() > 0) {
				FloorContext floor = floors.get(0);
				context.put(FacilioConstants.ContextNames.FLOOR, floor);
				
				context.put(GetNotesCommand.MODULEID_COLUMN, "FLOOR_ID");
				context.put(GetNotesCommand.MODULE_ID, floorId);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Floor ID : "+floorId);
		}
		
		return false;
	}

}
