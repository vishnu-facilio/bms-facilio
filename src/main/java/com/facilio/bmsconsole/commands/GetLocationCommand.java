package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetLocationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long locationId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(locationId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<LocationContext> builder = new SelectRecordsBuilder<LocationContext>()
					.table(dataTableName)
					.moduleName(moduleName)
					.beanClass(LocationContext.class)
					.select(fields)
					.andCustomWhere("ID = ?", locationId)
					.orderBy("ID");

			List<LocationContext> locations = builder.get();	
			if(locations.size() > 0) {
				LocationContext location = locations.get(0);
				context.put(FacilioConstants.ContextNames.LOCATION, location);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Location ID : "+locationId);
		}
		
		return false;
	}

}
