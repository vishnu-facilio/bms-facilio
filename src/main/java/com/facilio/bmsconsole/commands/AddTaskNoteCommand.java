package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddTaskNoteCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long taskId = (long) context.get(FacilioConstants.ContextNames.TASK_ID);
		if(taskId <= 0) {
			throw new IllegalArgumentException("Invalid Task Id");
		}
		
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		if(note == null || note.getNoteId() <= 0) {
			throw new IllegalArgumentException("Invalid Note Id");
		}
		
//		long taskNoteId = TaskAPI.addTaskNotes(taskId, noteContext.getNoteId(), ((FacilioContext) context).getConnectionWithoutTransaction());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement("INSERT INTO Tasks_Notes (TASKID, NOTEID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, taskId);
			pstmt.setLong(2, note.getNoteId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add Task Note");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long taskNoteId = rs.getLong(1);
				System.out.println("Added Task Note with id : "+taskNoteId);
			}
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
