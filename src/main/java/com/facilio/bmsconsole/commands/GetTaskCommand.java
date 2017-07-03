package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class GetTaskCommand implements Command {

	private static final String[] DEFAULT_SELECT_TASK_FIELDS = new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNMENT_GROUP_ID", "ASSIGNED_TO_ID", "SCHEDULE_ID"};
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(FacilioConstants.ContextNames.TASK_ID);
		
		if(taskId > 0) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			String objectTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_OBJECTS_TABLE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				List<FacilioCustomField> customFields = (List<FacilioCustomField>) context.get(FacilioConstants.ContextNames.CUSTOM_FIELDS);
				String sql = CFUtil.constructSelectStatement(objectTableName, dataTableName, DEFAULT_SELECT_TASK_FIELDS, customFields, new String[] {"ORGID", "TASKID"});
				
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, taskId);
				
				rs = pstmt.executeQuery();
				TaskContext tc = null;
				while(rs.next()) {
					tc = CommonCommandUtil.getTaskObjectFromRS(rs, customFields);
					break;
				}
				
				if(tc != null) {
					context.put(FacilioConstants.ContextNames.TASK, tc);
					context.put(FacilioConstants.ContextNames.SCHEDULE_ID, tc.getScheduleId());
					
					context.put(GetNotesCommand.NOTES_REL_TABLE, "Tasks_Notes");
					context.put(GetNotesCommand.MODULEID_COLUMN, "TASKID");
					context.put(GetNotesCommand.MODULE_ID, taskId);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				DBUtil.closeAll(pstmt, rs);
			}
;		}
		else {
			throw new IllegalArgumentException("Invalid Task ID : "+taskId);
		}
		
		return false;
	}

}
