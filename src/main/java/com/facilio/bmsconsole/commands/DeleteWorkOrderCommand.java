package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteWorkOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			String condition = "ID IN (";
			for (int i=0; i< recordIds.size(); i++) {
				if (i != 0) {
					condition += ",";
				}
				condition += "?";
			}
			condition += ")";
			
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(dataTableName)
					.andCustomWhere(condition, recordIds.toArray());
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		}
		return false;
	}
	
}
