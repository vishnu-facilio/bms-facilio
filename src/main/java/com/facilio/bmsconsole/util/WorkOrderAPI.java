package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class WorkOrderAPI {
	
	private static Logger logger = Logger.getLogger(WorkOrderAPI.class.getName());
	
	public static Long addWorkOrderNote(Long workOrderId, Long noteId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO WorkOrder_Note (WORK_ORDER_ID, NOTE_ID) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, workOrderId);
			pstmt.setLong(2, noteId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add Work Order note");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				areaId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding work order note" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}

	public static List<TaskContext> getRelatedTasks(long workOrderId, Connection conn) throws Exception 
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
		List<FacilioField> fields = modBean.getAllFields("task");
		
		SelectRecordsBuilder<TaskContext> builder = new SelectRecordsBuilder<TaskContext>()
				.connection(conn)
				.dataTableName("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(fields)
				.where("PARENT_WORK_ORDER_ID = ?", workOrderId)
				.orderBy("ID");

		List<TaskContext> tasks = builder.getAsBean();	
		return tasks;
	}
	
	public static List<NoteContext> getRelatedNotes(long workOrderId, Connection conn) throws SQLException 
	{
		List<NoteContext> notes = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT * FROM Notes "
					+ " INNER JOIN WorkOrder_Note ON Notes.NOTEID = WorkOrder_Note.NOTE_ID"
					+ " WHERE WorkOrder_Note.WORK_ORDER_ID = ?");
			pstmt.setLong(1, workOrderId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				NoteContext nc = new NoteContext();
				nc.setNoteId(rs.getLong("NOTE_ID"));
				nc.setBody(rs.getString("BODY"));
				notes.add(nc);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all tasks" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return notes;
	}
}
