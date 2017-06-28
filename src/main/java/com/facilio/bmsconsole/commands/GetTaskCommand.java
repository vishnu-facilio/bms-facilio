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

	public static final String TASK_ID = "taskId";
	private static final String[] DEFAULT_SELECT_TASK_FIELDS = new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNMENT_GROUP_ID", "ASSIGNED_TO_ID", "SCHEDULE_ID"};
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(TASK_ID);
		
		if(taskId > 0) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
//			TaskContext tc = TaskAPI.getTaskDetails(taskId, orgId, ((FacilioContext) context).getConnectionWithoutTransaction());
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
				String sql = CFUtil.constructSelectStatement("Tasks_Objects", "Tasks_Data", DEFAULT_SELECT_TASK_FIELDS, customFields, new String[] {"ORGID", "TASKID"});
				
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
					context.put(GetScheduleObjectCommand.SCHEDULE_ID, tc.getScheduleId());
					
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
		
		return false;
	}

}
