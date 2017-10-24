package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class AddZoneCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ZoneContext zone = (ZoneContext) context.get(FacilioConstants.ContextNames.ZONE);
		if(zone != null) 
		{
			zone.setSpaceType(SpaceType.ZONE);
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<ZoneContext> builder = new InsertRecordBuilder<ZoneContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields)
															.connection(conn);
			long zoneId = builder.insert(zone);
			zone.setId(zoneId);
		}
		else 
		{
			throw new IllegalArgumentException("Zone Object cannot be null");
		}
		return false;
	}
}
