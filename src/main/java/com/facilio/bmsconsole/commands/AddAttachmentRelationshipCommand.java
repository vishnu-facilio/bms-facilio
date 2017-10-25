package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;

public class AddAttachmentRelationshipCommand implements Command {

	private static Logger LOGGER = Logger.getLogger(AddAttachmentRelationshipCommand.class.getName());
	
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
		Connection conn = null;
		try {
			conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement("INSERT INTO "+ticketAttachmentTable+" ("+pkColumn+", FILE_ID) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
			
			for (long attachmentId : attachmentIdList) {
				pstmt.setLong(1, recordId);
				pstmt.setLong(2, attachmentId);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, getAttachments(conn, ticketAttachmentTable, pkColumn, recordId, attachmentIdList));
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, null);
		}
		
		return false;
	}

	private List<AttachmentContext> getAttachments(Connection conn, String ticketAttachmentTable, String pkColumn, long recordId, List<Long> fileIds) throws Exception {
		
		List<AttachmentContext> attachments = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			String sql = "SELECT * FROM File INNER JOIN "+ticketAttachmentTable+" ON File.FILE_ID = "+ticketAttachmentTable+".FILE_ID WHERE "+ticketAttachmentTable+"."+pkColumn+" = ? AND "+ticketAttachmentTable+".FILE_ID IN (";
			for (int i=0; i< fileIds.size(); i++) {
				if (i != 0) {
					sql += ",";
				}
				sql += "?";
			}
			sql += ")";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, recordId);
			for (int i=0; i< fileIds.size(); i++) {
				pstmt.setLong((i + 2), fileIds.get(i));
			}
			
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				AttachmentContext ac = new AttachmentContext();
				ac.setAttachmentId(rs.getLong("TICKET_ATTACHMENT_ID"));
				ac.setFileId(rs.getLong("FILE_ID"));
				ac.setOrgId(rs.getLong("ORGID"));
				ac.setFileName(rs.getString("FILE_NAME"));
				ac.setFileSize(rs.getLong("FILE_SIZE"));
				ac.setContentType(rs.getString("CONTENT_TYPE"));
				ac.setUploadedBy(rs.getLong("UPLOADED_BY"));
				ac.setUploadedTime(rs.getLong("UPLOADED_TIME"));
				attachments.add(ac);
			}
		}
		catch (SQLException e) 
		{
			LOGGER.log(Level.SEVERE, "Exception while getting related attachments." +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return attachments;
	}
}
