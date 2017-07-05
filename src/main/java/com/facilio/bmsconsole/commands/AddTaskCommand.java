package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ScheduleContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.fields.FieldUtil;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddTaskCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		TaskContext task = (TaskContext) context.get(FacilioConstants.ContextNames.TASK);
		if(task != null) {
			
			ScheduleContext scheduleObj = (ScheduleContext) context.get(FacilioConstants.ContextNames.SCHEDULE_OBJECT);
			if(scheduleObj != null) {
				task.setScheduleId(scheduleObj.getScheduleId());
			}
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.CUSTOM_FIELDS);
				fields.remove(0);
				
				String sql = FieldUtil.constuctInsertStatement(moduleName, dataTableName, fields);
				
				Connection conn = ((FacilioContext) context).getConnectionWithTransaction(); 
				pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				
				pstmt.setLong(1, task.getParent());
				pstmt.setString(2, task.getSubject());
				pstmt.setString(3, task.getDescription());
				
				if(task.getAssignmentGroupId() != 0) {
					pstmt.setLong(4, task.getAssignmentGroupId());
				}
				else {
					pstmt.setNull(4, Types.BIGINT);
				}
				
				if(task.getAssignedToId() != 0) {
					pstmt.setLong(5, task.getAssignedToId());
				}
				else {
					pstmt.setNull(5, Types.BIGINT);
				}
				
				if(task.getScheduleId() != 0) {
					pstmt.setLong(6, task.getScheduleId());
				}
				else {
					pstmt.setNull(6, Types.BIGINT);
				}
				
				FieldUtil.appendCustomFieldValues(fields, TaskContext.DEFAULT_TASK_FIELDS.length-1, task.getCustomProps(), pstmt);
				
				if(pstmt.executeUpdate() < 1) {
					throw new RuntimeException("Unable to add task");
				}
				else {
					rs = pstmt.getGeneratedKeys();
					rs.next();
					long taskId = rs.getLong(1);
					System.out.println("Added task with task id : "+taskId);
					task.setTaskId(taskId);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				 DBUtil.closeAll(pstmt, rs);
			}
		}
		else {
			throw new IllegalArgumentException("Task Object cannot be null");
		}
		
		return false;
	}
}
