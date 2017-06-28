package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class GetAllTasksCommand implements Command{

	private static final String[] DEFAULT_SELECT_TASK_FIELDS = new String[] {"TASKID",  "PARENT", "SUBJECT", "DESCRIPTION", "ASSIGNMENT_GROUP_ID", "ASSIGNED_TO_ID", "SCHEDULE_ID"};
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
//		List<TaskContext> tasks = TaskAPI.getAllTasksOfOrg(orgId, ((FacilioContext) context).getConnectionWithoutTransaction());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			List<FacilioCustomField> customFields = CFUtil.getCustomFields("Tasks_Objects", "Tasks_Fields", orgId);
			String sql = CFUtil.constructSelectStatement("Tasks_Objects", "Tasks_Data", DEFAULT_SELECT_TASK_FIELDS, customFields, new String[] {"ORGID"});
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			pstmt = conn.prepareStatement(sql+" ORDER BY SUBJECT");
			pstmt.setLong(1, orgId);
			
			List<TaskContext> tasks = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				TaskContext tc = CommonCommandUtil.getTaskObjectFromRS(rs, customFields);
				tasks.add(tc);
			}
			
			context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
	}

}
