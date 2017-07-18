package com.facilio.fs;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public abstract class FileStore {

	private long orgId;
	private long userId;
	public FileStore(long orgId, long userId) {
		this.orgId = orgId;
		this.userId = userId;
	}
	
	public long getOrgId() {
		return this.orgId;
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	protected long addDummyFileEntry(String fileName) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO File (ORGID, FILE_NAME, UPLOADED_BY, UPLOADED_TIME) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, fileName);
			pstmt.setLong(3, getUserId());
			pstmt.setLong(4, System.currentTimeMillis());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add file");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long fileId = rs.getLong(1);
				System.out.println("Added file with id : "+fileId);
				return fileId;
			}
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	protected boolean updateFileEntry(long fileId, String fileName, String filePath, long fileSize, String contentType) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE File SET FILE_NAME=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=? WHERE FILE_ID=? AND ORGID=?");
			
			pstmt.setString(1, fileName);
			pstmt.setString(2, filePath);
			pstmt.setLong(3, fileSize);
			pstmt.setString(4, contentType);
			pstmt.setLong(5, fileId);
			pstmt.setLong(6, getOrgId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update file");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	protected boolean deleteFileEntry(long fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM File WHERE FILE_ID=? AND ORGID=?");
			
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to delete file");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	protected boolean deleteFileEntries(List<Long> fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM File WHERE FILE_ID=? AND ORGID=?");
			
			for (long id : fileId) {
				pstmt.setLong(1, id);
				pstmt.setLong(2, getOrgId());
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	protected abstract String getRootPath();
	
	public abstract long addFile(String fileName, File file, String contentType) throws Exception;
	
	public FileInfo getFileInfo(long fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM File WHERE FILE_ID=? AND ORGID=? ORDER BY FILE_NAME");
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(rs);
				return fileInfo;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return null;
	}
	
	public List<FileInfo> getFileInfo(List<Long> fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "SELECT * FROM File WHERE FILE_ID IN (";
			for (int i=0; i< fileId.size(); i++) {
				if (i != 0) {
					sql += ", ";
				}
				sql += fileId.get(i);
			}
			sql += ") AND ORGID=? ORDER BY FILE_NAME";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, getOrgId());
			
			List<FileInfo> fileList = new ArrayList<FileInfo>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(rs);
				fileList.add(fileInfo);
			}
			return fileList;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	protected List<String> getFilePathList(List<Long> fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "SELECT FILE_PATH FROM File WHERE FILE_ID IN (";
			for (int i=0; i< fileId.size(); i++) {
				if (i != 0) {
					sql += ", ";
				}
				sql += fileId.get(i);
			}
			sql += ") AND ORGID=? ORDER BY FILE_NAME";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, getOrgId());
			
			List<String> filePathList = new ArrayList<String>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String filePath = rs.getString("FILE_PATH");
				filePathList.add(filePath);
			}
			return filePathList;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private FileInfo getFileInfoFromRS(ResultSet rs) throws Exception {
		
		FileInfo fileInfo = new FileInfo();
		fileInfo.setOrgId(rs.getLong("ORGID"));
		fileInfo.setFileId(rs.getLong("FILE_ID"));
		fileInfo.setFileName(rs.getString("FILE_NAME"));
		fileInfo.setFilePath(rs.getString("FILE_PATH"));
		fileInfo.setFileSize(rs.getLong("FILE_SIZE"));
		fileInfo.setContentType(rs.getString("CONTENT_TYPE"));
		fileInfo.setUploadedBy(rs.getLong("UPLOADED_BY"));
		fileInfo.setUploadedTime(rs.getLong("UPLOADED_TIME"));
		
		return fileInfo;
	}
	
	public abstract InputStream readFile(long fileId) throws Exception;
	
	public abstract boolean deleteFile(long fileId) throws Exception;
	
	public abstract boolean deleteFiles(List<Long> fileId) throws Exception;
	
	public abstract boolean renameFile(long fileId, String newName) throws Exception;
}