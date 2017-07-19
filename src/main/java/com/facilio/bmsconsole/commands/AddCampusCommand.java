package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;

public class AddCampusCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		CampusContext campus = (CampusContext) context.get(FacilioConstants.ContextNames.CAMPUS);
		if(campus != null) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<CampusContext> builder = new InsertRecordBuilder<CampusContext>()
															.moduleName(moduleName)
															.dataTableName(dataTableName)
															.fields(fields)
															.connection(conn);
			long campusId = builder.insert(campus);
			campus.setCampusId(campusId);
		}
		else 
		{
			throw new IllegalArgumentException("Campus Object cannot be null");
		}
		return false;
	}
}
