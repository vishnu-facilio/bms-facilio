package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetLocationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long locationId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(locationId > 0) {
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<LocationContext> builder = new SelectRecordsBuilder<LocationContext>()
					.connection(conn)
					.dataTableName(dataTableName)
					.beanClass(LocationContext.class)
					.select(fields)
					.where("ID = ?", locationId)
					.orderBy("ID");

			List<LocationContext> locations = builder.getAsBean();	
			if(locations.size() > 0) {
				LocationContext location = locations.get(0);
				context.put(FacilioConstants.ContextNames.LOCATION, location);

				context.put(GetNotesCommand.MODULE_ID, locationId);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Location ID : "+locationId);
		}
		
		return false;
	}

}
