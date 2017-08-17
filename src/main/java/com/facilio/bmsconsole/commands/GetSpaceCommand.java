package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetSpaceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(spaceId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
					.connection(conn)
					.dataTableName(dataTableName)
					.moduleName(moduleName)
					.beanClass(SpaceContext.class)
					.select(fields)
					.where("ID = ?", spaceId)
					.orderBy("ID");

			List<SpaceContext> spaces = builder.getAsBean();	
			if(spaces.size() > 0) {
				SpaceContext space = spaces.get(0);
				context.put(FacilioConstants.ContextNames.SPACE, space);
				
				context.put(GetNotesCommand.MODULEID_COLUMN, "SPACE_ID");
				context.put(GetNotesCommand.MODULE_ID, spaceId);
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Space ID : "+spaceId);
		}
		
		return false;
	}

}
