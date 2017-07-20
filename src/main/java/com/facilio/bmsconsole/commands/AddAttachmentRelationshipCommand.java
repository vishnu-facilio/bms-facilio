package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddAttachmentRelationshipCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(moduleName == null || recordId == null) {
			return false;
		}
		
		String ticketAttachmentTable = FacilioConstants.ContextNames.getAttachmentTableName(moduleName);
		String pkColumn = FacilioConstants.ContextNames.getPKColumn(moduleName);
		
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		if(attachmentIdList == null || attachmentIdList.isEmpty()) {
			return false;
		}
		
		PreparedStatement pstmt = null;
		try {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement("INSERT INTO "+ticketAttachmentTable+" ("+pkColumn+", FILE_ID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (long attachmentId : attachmentIdList) {
				pstmt.setLong(1, recordId);
				pstmt.setLong(2, attachmentId);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, null);
		}
		
		return false;
	}

}
