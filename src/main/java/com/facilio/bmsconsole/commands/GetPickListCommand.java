package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;

public class GetPickListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		
		if(dataTableName != null && !dataTableName.isEmpty() && defaultField != null) {
			List<FacilioField> fields = new ArrayList<>();
			fields.add(defaultField);
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																.connection(conn)
																.dataTableName(dataTableName)
																.moduleName(moduleName)
																.select(fields)
																.orderBy("ID");
			
			List<Map<String, Object>> records = builder.getAsProps();
			
			if(records != null && records.size() > 0) {
				Map<Long, String> pickList = new HashMap<>();
				
				for(Map<String, Object> record : records) {
					pickList.put((Long) record.get("id"), record.get(defaultField.getName()).toString());
				}
				
				context.put(FacilioConstants.ContextNames.PICKLIST, pickList);
			}
		}
		
		return false;
	}

}
