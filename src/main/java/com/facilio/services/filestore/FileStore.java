package com.facilio.services.filestore;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

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
			pstmt = conn.prepareStatement("INSERT INTO FacilioFile (ORGID, FILE_NAME, UPLOADED_BY, UPLOADED_TIME) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
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
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE FacilioFile SET FILE_NAME=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=? WHERE FILE_ID=? AND ORGID=?");
			
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
			DBUtil.closeAll(conn, pstmt);
		}
	}
	
	
	protected boolean updateResizedFileEntry(ResizedFileInfo fileInfo) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean olderCommit = false;
		try {
			conn = FacilioConnectionPool.INSTANCE.getDirectConnection(); //Getting connection from pool since this has to be done outside transaction
			olderCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			
			pstmt = conn.prepareStatement("UPDATE ResizedFile SET URL=?, EXPIRY_TIME=? WHERE FILE_ID=? AND ORGID=? AND WIDTH=? AND HEIGHT=?");
			pstmt.setString(1, fileInfo.getUrl());
			pstmt.setLong(2, fileInfo.getExpiryTime());
			pstmt.setLong(3, fileInfo.getFileId());
			pstmt.setLong(4, getOrgId());
			pstmt.setInt(5, fileInfo.getWidth());
			pstmt.setInt(6, fileInfo.getHeight());
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update file");
			}
			
			conn.commit();
			return true;
		}
		catch(SQLException | RuntimeException e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		}
		finally {
			if (conn != null) {
				conn.setAutoCommit(olderCommit);
			}
			DBUtil.closeAll(conn, pstmt);
		}
	}
	
	protected boolean deleteFileEntry(long fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM FacilioFile WHERE FILE_ID=? AND ORGID=?");
			
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
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM FacilioFile WHERE FILE_ID=? AND ORGID=?");
			
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
			DBUtil.closeAll(conn, pstmt);
		}
	}
	
	protected boolean addResizedFileEntry(long fileId, int width, int height, String filePath, long fileSize, String contentType) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();

			pstmt = conn.prepareStatement("INSERT INTO ResizedFile set FILE_ID=?, ORGID=?, WIDTH=?, HEIGHT=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=?, GENERATED_TIME=?");
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());
			pstmt.setInt(3, width);
			pstmt.setInt(4, height);
			pstmt.setString(5, filePath);
			pstmt.setLong(6, fileSize);
			pstmt.setString(7, contentType);
			pstmt.setLong(8, System.currentTimeMillis());
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add resized file");
			}
			
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
	}
	
	
	protected boolean addResizedFileEntry(ResizedFileInfo fileInfo) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO ResizedFile set FILE_ID=?, ORGID=?, WIDTH=?, HEIGHT=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=?, GENERATED_TIME=?, URL=?, EXPIRY_TIME=?");
			
			pstmt.setLong(1, fileInfo.getFileId());
			pstmt.setLong(2, getOrgId());
			pstmt.setInt(3, fileInfo.getWidth());
			pstmt.setInt(4, fileInfo.getHeight());
			pstmt.setString(5, fileInfo.getFilePath());
			pstmt.setLong(6, fileInfo.getFileSize());
			pstmt.setString(7, fileInfo.getContentType());
			pstmt.setLong(8, System.currentTimeMillis());
			pstmt.setString(9, fileInfo.getUrl());
			pstmt.setLong(10, fileInfo.getExpiryTime());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add resized file");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
	}
	
	protected abstract String getRootPath();
	
	public abstract long addFile(String fileName, File file, String contentType) throws Exception;
	
	public abstract long addFile(String fileName, File file, String contentType, int[] resize) throws Exception;
	
	public abstract long addFile(String fileName, String content, String contentType) throws Exception;
	
	public FileInfo getFileInfo(long fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM FacilioFile WHERE FILE_ID=? AND ORGID=? ORDER BY FILE_NAME");
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
	
	public FileInfo getResizedFileInfo(long fileId, int width, int height) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT FacilioFile.FILE_ID, FacilioFile.ORGID, FacilioFile.FILE_NAME, FacilioFile.UPLOADED_BY, FacilioFile.UPLOADED_TIME, ResizedFile.FILE_PATH, ResizedFile.FILE_SIZE, ResizedFile.CONTENT_TYPE,ResizedFile.EXPIRY_TIME, ResizedFile.URL FROM ResizedFile join FacilioFile on ResizedFile.FILE_ID = FacilioFile.FILE_ID WHERE ResizedFile.FILE_ID=? AND ResizedFile.ORGID=? AND ResizedFile.WIDTH=? AND ResizedFile.HEIGHT=?");
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());
			pstmt.setInt(3, width);
			pstmt.setInt(4, height);
			
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
			
			String sql = "SELECT * FROM FacilioFile WHERE FILE_ID IN (";
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
	
	public Map<Long, FileInfo> getFileInfoAsMap(List<Long> fileId) throws Exception {
		Map<Long, FileInfo> fileMap = new HashMap<Long, FileInfo>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "SELECT * FROM FacilioFile WHERE FILE_ID IN (";
			for (int i=0; i< fileId.size(); i++) {
				if (i != 0) {
					sql += ", ";
				}
				sql += fileId.get(i);
			}
			sql += ") AND ORGID=? ORDER BY FILE_NAME";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, getOrgId());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(rs);
				fileMap.put(fileInfo.getFileId(), fileInfo);
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}

		return fileMap;
	}
	
	protected List<String> getFilePathList(List<Long> fileId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			
			String sql = "SELECT FILE_PATH FROM FacilioFile WHERE FILE_ID IN (";
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
		
		ResizedFileInfo fileInfo = new ResizedFileInfo();
		fileInfo.setOrgId(rs.getLong("ORGID"));
		fileInfo.setFileId(rs.getLong("FILE_ID"));
		if (rs.getString("FILE_NAME") != null) {
			fileInfo.setFileName(rs.getString("FILE_NAME").trim());
		}
		if (rs.getString("FILE_PATH") != null) {
			fileInfo.setFilePath(rs.getString("FILE_PATH").trim());
		}
		fileInfo.setFileSize(rs.getLong("FILE_SIZE"));
		fileInfo.setContentType(rs.getString("CONTENT_TYPE"));
		fileInfo.setUploadedBy(rs.getLong("UPLOADED_BY"));
		fileInfo.setUploadedTime(rs.getLong("UPLOADED_TIME"));
		try {
			fileInfo.setUrl(rs.getString("URL"));
			fileInfo.setExpiryTime(rs.getLong("EXPIRY_TIME"));
		}
		catch(SQLException e) {
//			System.err.println("No such column :"+e.getMessage());
		}
		
		return fileInfo;
	}
	
	private String getUrl (long fileId, boolean isDownload, boolean isPortal, int width, int height) throws Exception {
		StringBuilder url = new StringBuilder();
//		if (AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().isFromMobile()) {
//			FileStore fs = FacilioFactory.getFileStore();
//			if (isDownload) {
//				return fs.getOrgiDownloadUrl(fileId);
//			} else {
//				return fs.getOrgiFileUrl(fileId);
//			}
//		}
		if (FacilioProperties.isDevelopment()) {
			url.append(FacilioProperties.getServerName());
		}
		url.append("/api/v2/");
		
		if (isPortal) {
			url.append("service/");
		}
		
		url.append("files/");
		if (isDownload) {
			url.append("download/");
		}
		else {
			url.append("preview/");
		}
		url.append(fileId);
		
		if (width != -1) {
			url.append("?width=").append(width);
			
			if (height != -1) {
				url.append("&height=").append(height);
			}
		}
		return url.toString();
	}

	public String getPrivateUrl(long fileId) throws Exception {
		User currentUser = AccountUtil.getCurrentAccount() == null ? null : AccountUtil.getCurrentAccount().getUser();
		
		if (currentUser != null) {
			return getPrivateUrl(fileId, currentUser.isPortalUser());
		}
		else {
			return getPrivateUrl(fileId, false);
		}
	}
	
	public String getPrivateUrl(long fileId, boolean isPortalUser) throws Exception {
		return getUrl(fileId, false, isPortalUser, -1, -1);
	}
	
	public String getPrivateUrl(long fileId, int width) throws Exception {
		User currentUser = AccountUtil.getCurrentAccount() == null ? null : AccountUtil.getCurrentAccount().getUser();
		
		if (currentUser != null) {
			return getPrivateUrl(fileId, width, currentUser.isPortalUser());
		}
		else {
			return getPrivateUrl(fileId, width, false);
		}
	}
	
	public String getPrivateUrl(long fileId, int width,boolean isPortalUser) throws Exception {
		return getUrl(fileId, false, isPortalUser, width, -1);
	}
	
	public String getDownloadUrl(long fileId) throws Exception {
		User currentUser = AccountUtil.getCurrentAccount() == null ? null : AccountUtil.getCurrentAccount().getUser();
		if (currentUser != null) {
			return getDownloadUrl(fileId, currentUser.isPortalUser());
		}
		else {
			return getDownloadUrl(fileId, false);
		}
	}
	
	public String getDownloadUrl(long fileId, boolean isPortalUser) throws Exception {
		return getUrl(fileId, true, isPortalUser, -1, -1);
	}
	
	public int markAsDeleted(List<Long> fileIds) throws SQLException {
		List<FacilioField> fields = FieldFactory.getFileFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(ModuleFactory.getFilesModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fileId"), fileIds, NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("isDeleted", true);
		return builder.update(props);
	}
	public String orginalFileUrl (long fileId) throws Exception {
		return getOrgiDownloadUrl(fileId);
	}
	
	public abstract InputStream readFile(long fileId) throws Exception;
	
	public abstract InputStream readFile(FileInfo fileInfo) throws Exception;
		
	public abstract boolean deleteFile(long fileId) throws Exception;
	
	public abstract boolean deleteFiles(List<Long> fileId) throws Exception;
	
	public abstract boolean renameFile(long fileId, String newName) throws Exception;

	public abstract String getOrgiFileUrl(long fileId) throws Exception;
	
	public abstract String getOrgiDownloadUrl(long fileId) throws Exception;

	public abstract boolean isFileExists(String newVersion);
}