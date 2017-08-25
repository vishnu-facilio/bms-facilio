package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class AddNoteCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		NoteContext note = (NoteContext) context.get(FacilioConstants.ContextNames.NOTE);
		//long noteId = NoteApi.addNote(noteContext, ((FacilioContext) context).getConnectionWithoutTransaction());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement("INSERT INTO Notes (ORGID, OWNERID, CREATION_TIME, TITLE, BODY) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, OrgInfo.getCurrentOrgInfo().getOrgid());
			if(note.getOwnerId() != null) 
			{
				pstmt.setLong(2, note.getOwnerId());
			}
			else
			{
				pstmt.setNull(2, Types.BIGINT);
			}
			if(note.getOwnerId() != null) 
			{
				pstmt.setLong(3,  note.getCreationTime());
			}
			else
			{
				pstmt.setNull(3, Types.BIGINT);
			}
			if(note.getTitle() != null && !note.getTitle().isEmpty()) {
				pstmt.setString(4, note.getTitle());
			}
			else {
				pstmt.setNull(4, Types.VARCHAR);
			}
			
			pstmt.setString(5, note.getBody());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add Note");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long noteId = rs.getLong(1);
				System.out.println("Added Note with id : "+noteId);
				note.setNoteId(noteId);
				
				if(note.getParentModuleLinkName() != null && note.getParentModuleLinkName().equals("workorder"))
				{
					WorkOrderAPI.addWorkOrderNote(note.getParentId(), noteId, conn);
					if(note.getNotifyRequester())
					{
						JSONObject mailJson = new JSONObject();
						mailJson.put("sender", "support@thingscient.com");
						mailJson.put("to", "shivaraj@thingscient.com");
						mailJson.put("subject", "New note added by ");
						mailJson.put("message", note.getBody());
						AwsUtil.sendEmail(mailJson);
					}
				}
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
