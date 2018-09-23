package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteTaskCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		if (recordIds != null && !recordIds.isEmpty()) {
			
			String sql = "ID IN (";
			for (int i=0; i < recordIds.size(); i++) {
				if (i != 0) {
					sql += ",";
				}
				sql += "?";
			}
			sql += ")";
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(dataTableName)
					.andCustomWhere(sql, recordIds.toArray());
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		}
		return false;
	}
}