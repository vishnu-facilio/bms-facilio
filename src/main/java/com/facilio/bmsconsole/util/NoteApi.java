package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class NoteApi {
	public static long addNote(NoteContext context) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Notes (ORGID, OWNERID, CREATION_TIME, TITLE, BODY) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, context.getOrgId());
			pstmt.setLong(2, context.getOwnerId());
			pstmt.setLong(3,  context.getCreationTime());
			
			if(context.getTitle() != null && !context.getTitle().isEmpty()) {
				pstmt.setString(4, context.getTitle());
			}
			else {
				pstmt.setNull(4, Types.VARCHAR);
			}
			
			pstmt.setString(5, context.getBody());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add Note");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long noteId = rs.getLong(1);
				System.out.println("Added Note with id : "+noteId);
				return noteId;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static final List<NoteContext> getNotesOfModule(String moduleRelTable, String moduleIdColumn, long moduleId, long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			StringBuilder sql = new StringBuilder();
			
			sql.append("SELECT * FROM Notes INNER JOIN ")
				.append(moduleRelTable)
				.append(" ON Notes.NOTEID = ")
				.append(moduleRelTable)
				.append(".NOTEID WHERE ORGID = ? AND ")
				.append(moduleRelTable)
				.append(".")
				.append(moduleIdColumn)
				.append(" = ? ORDER BY CREATION_TIME");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, moduleId);
			
			List<NoteContext> notes = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				notes.add(getNoteContextFromRS(rs));
			}
			
			return notes;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static final NoteContext getNoteContextFromRS(ResultSet rs) throws SQLException {
		NoteContext context = new NoteContext();
		
		context.setNoteId(rs.getLong("NOTEID"));
		context.setOrgId(rs.getLong("ORGID"));
		context.setOwnerId(rs.getLong("OWNERID"));
		context.setCreationTime(rs.getLong("CREATION_TIME"));
		context.setTitle(rs.getString("TITLE"));
		context.setBody(rs.getString("BODY"));
		
		return context;
	}
}
