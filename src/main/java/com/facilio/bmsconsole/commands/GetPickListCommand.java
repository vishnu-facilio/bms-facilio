package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPickListCommand implements Command {

	private static org.apache.log4j.Logger log = LogManager.getLogger(GetPickListCommand.class.getName());


	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
		//Connection conn = FacilioConnectionPool.INSTANCE.getConnection();
		
		try {
			if(dataTableName != null && !dataTableName.isEmpty() && defaultField != null) {
				List<FacilioField> fields = new ArrayList<>();
				fields.add(defaultField);				
				SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
																	.table(dataTableName)
																	.moduleName(moduleName)
																	.select(fields)
																	.orderBy("ID");
				
				List<Map<String, Object>> records = builder.getAsProps();
				Map<Long, String> pickList = new HashMap<>();
				if(records != null && records.size() > 0) {
					for(Map<String, Object> record : records) {
						pickList.put((Long) record.get("id"), record.get(defaultField.getName()).toString());
					}
				}
				context.put(FacilioConstants.ContextNames.PICKLIST, pickList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception occurred during retrieval of pick list for "+moduleName);
			log.info("Exception occurred ", e);
		}
		finally
		{
			//conn.close();
		}
		
		return false;
	}

}
