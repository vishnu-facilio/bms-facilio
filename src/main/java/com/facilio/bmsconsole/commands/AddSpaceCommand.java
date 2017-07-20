package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class AddSpaceCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SpaceContext space = (SpaceContext) context.get(FacilioConstants.ContextNames.SPACE);
		if(space != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<SpaceContext> builder = new InsertRecordBuilder<SpaceContext>()
															.moduleName(moduleName)
															.dataTableName(dataTableName)
															.fields(fields)
															.connection(conn);
			long spaceId = builder.insert(space);
			space.setSpaceId(spaceId);
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null");
		}
		return false;
	}
}
