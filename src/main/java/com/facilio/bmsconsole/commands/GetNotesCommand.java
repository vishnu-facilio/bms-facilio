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
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class GetNotesCommand implements Command {

	public static final String NOTES_REL_TABLE = "notesRelTable";
	public static final String MODULEID_COLUMN = "notesModuleColumn";
	public static final String MODULE_ID = "notesModuleId";
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long moduleId = (long) context.get(MODULE_ID);
		
		if(moduleId > 0) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			
			String moduleRelTable = (String) context.get(NOTES_REL_TABLE);
			String moduleIdColumn = (String) context.get(MODULEID_COLUMN);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				
				StringBuilder sql = new StringBuilder();
				
				sql.append("SELECT * FROM Notes INNER JOIN ")
					.append(moduleRelTable)
					.append(" ON Notes.NOTEID = ")
					.append(moduleRelTable)
					.append(".NOTE_ID WHERE ORGID = ? AND ")
					.append(moduleRelTable)
					.append(".")
					.append(moduleIdColumn)
					.append(" = ? ORDER BY CREATION_TIME");
				
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, moduleId);
				
				List<NoteContext> notes = new ArrayList<>();
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					notes.add(CommonCommandUtil.getNoteContextFromRS(rs));
				}
				
				context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
			}
			catch(SQLException e) {
				e.printStackTrace();
				throw e;
			}
			finally {
				DBUtil.closeAll(pstmt, rs);
			}
		}
		
		return false;
	}

}
