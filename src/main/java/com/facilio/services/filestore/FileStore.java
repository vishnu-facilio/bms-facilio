package com.facilio.services.filestore;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.imageio.ImageIO;

import com.beust.ah.A;
import com.facilio.bmsconsole.context.ApplicationContext;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.FileJWTUtil;
import com.facilio.bmsconsole.util.ImageScaleUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.util.FacilioUtil;

public abstract class FileStore {

	public static class NamespaceConfig {
		private static final int DEFAULT_DATA_RETENTION_PERIOD = 5; //In days
		private NamespaceConfig (String name,String tableName,String resizedTableName, String service,Integer dataRetention,Boolean dailyDirectoryNeeded ) {
			Objects.requireNonNull(name, "Name cannot be null in namespace");
			Objects.requireNonNull(tableName, "File Table Name cannot be null in namespace");
			Objects.requireNonNull(resizedTableName, " Resized File Table Name cannot be null in namespace");
			if(service == null || service.trim().isEmpty()) {
				throw new IllegalArgumentException("service cannot be null or empty in namespace");
			}
			this.name = name;
			this.tableName = tableName;
			this.resizedTableName = resizedTableName;
			this.dataRetention = dataRetention == null || dataRetention <= 0 ? DEFAULT_DATA_RETENTION_PERIOD : dataRetention;
			this.dailyDirectoryNeeded = Boolean.TRUE.equals(dailyDirectoryNeeded);
			this.service = service;
		}

		public String getName() {
			return name;
		}
		private String name;

		public String getTableName() {
			return tableName;
		}
		private String tableName;

		public String getResizedTableName() {
			return resizedTableName;
		}
		private String resizedTableName;

		public int getDataRetention() {
			return dataRetention;
		}
		private int dataRetention;

		public boolean isDailyDirectoryNeeded() {
			return dailyDirectoryNeeded;
		}
		private boolean dailyDirectoryNeeded = false;
		@Getter
		private String service;
	}

	private static Map<String, NamespaceConfig> initNamespaces() {
		try {
			Map<String, Object> namespaceConf = FacilioUtil.loadYaml(NAMESPACE_CONF_PATH);
			List<Map<String, Object>> namespaceConfList = (List<Map<String, Object>>) namespaceConf.get("namespaces");
			Map<String, NamespaceConfig> namespaces = new HashMap<>();
			for (Map<String, Object> conf : namespaceConfList) {
				NamespaceConfig namespace = new NamespaceConfig( (String) conf.get("name"), (String) conf.get("tableName"), (String) conf.get("resizedTableName"), (String)conf.get("service"),(Integer) conf.get("dataRetention"), (Boolean) conf.get("dailyDirectoryNeeded"));
				namespaces.put(namespace.getName(), namespace);
			}
			return namespaces;
		} catch (Exception e) {
			String msg = "Error occurred while parsing namespace conf : "+NAMESPACE_CONF_PATH;
			LOGGER.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	public static NamespaceConfig getNamespace (String namespace) {
		return NAMESPACES.get(namespace);
	}

	public static Set<String> getAllNamespaces() {
		return NAMESPACES.keySet();
	}

	private static final Logger LOGGER = LogManager.getLogger(FileStore.class.getName());
	private static final String NAMESPACE_CONF_PATH = FacilioUtil.normalizePath("conf/filestorenamespaces.yml");
	private static final Map<String, NamespaceConfig> NAMESPACES = Collections.unmodifiableMap(initNamespaces());
	private static final int DEFAULT_FILE_URL_EXPIRY = 300000;

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	public static final String DEFAULT_NAMESPACE = "default";
	public static final String DEFAULT_NAMESPACE_ORPHAN = "formOrphanFile";

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

	protected long addDummyFileEntry(String namespace, String fileName, boolean isOrphan) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while adding file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("INSERT INTO {0} (ORGID, FILE_NAME, UPLOADED_BY, UPLOADED_TIME, IS_ORPHAN) VALUES (?, ?, ?, ?, ?)", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, fileName);
			pstmt.setLong(3, getUserId());
			pstmt.setLong(4, System.currentTimeMillis());
			pstmt.setBoolean(5, isOrphan);

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

	protected boolean updateFileEntry(String namespace, long fileId, String compressedFilePath, long fileSize) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while updating file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("UPDATE {0} SET COMPRESSED_FILE_PATH=?, COMPRESSED_FILE_SIZE=? WHERE FILE_ID=? AND ORGID=?", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, compressedFilePath);
			pstmt.setLong(2, fileSize);
			pstmt.setLong(3, fileId);
			pstmt.setLong(4, getOrgId());

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

	protected boolean updateFileEntry(String namespace, long fileId, String fileName, String filePath, long fileSize, String contentType) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while updating file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("UPDATE {0} SET FILE_NAME=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=? WHERE FILE_ID=? AND ORGID=?", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql);

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

	protected boolean deleteFileEntry(String namespace, long fileId) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while deleting file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("DELETE FROM {0} WHERE FILE_ID=? AND ORGID=?", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql);

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

	protected boolean deleteFileEntries(String namespace, List<Long> fileId) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while deleting file entries");
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("DELETE FROM {0} WHERE FILE_ID=? AND ORGID=?", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql);

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


	public String encodeFileToBase64Binary(long fileId) throws Exception {
		return encodeFileToBase64Binary(DEFAULT_NAMESPACE, fileId);
	}
	public String encodeFileToBase64Binary(String namespace, long fileId) throws Exception {


		FileInfo fileInfo = getFileInfo(namespace, fileId);

		InputStream inputStream = null;
		try {
			inputStream = readFile(fileInfo);
			byte[] bytes = new byte[(int) fileInfo.getFileSize()];
			inputStream.read(bytes);
			String encodedfile = Base64.encodeBase64String(bytes);
			return encodedfile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		finally {
			if(inputStream != null) {
				inputStream.close();
			}
		}
		return null;
	}

	protected boolean addResizedFileEntry(String namespace, long fileId, int width, int height, String filePath, long fileSize, String contentType) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while adding resized file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("INSERT INTO {0} set FILE_ID=?, ORGID=?, WIDTH=?, HEIGHT=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=?, GENERATED_TIME=?", namespaceConfig.getResizedTableName());
			pstmt = conn.prepareStatement(sql);
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


	protected boolean addResizedFileEntry(String namespace, ResizedFileInfo fileInfo) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while adding resized file entry");
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("INSERT INTO {0} set FILE_ID=?, ORGID=?, WIDTH=?, HEIGHT=?, FILE_PATH=?, FILE_SIZE=?, CONTENT_TYPE=?, GENERATED_TIME=?, URL=?, EXPIRY_TIME=?", namespaceConfig.getResizedTableName());
			pstmt = conn.prepareStatement(sql);

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

	protected abstract String getRootPath(String namespace);

	public long addFile (String fileName, File file, String contentType) throws Exception {
		return addFile(DEFAULT_NAMESPACE,fileName, file, contentType);
	}
	public abstract long addFile(String namespace, String fileName, File file, String contentType) throws Exception;

	public long addFile(String fileName, File file, String contentType, int[] resize) throws Exception {
		return addFile(DEFAULT_NAMESPACE, fileName, file, contentType, resize);
	}
	public abstract long addFile(String namespace, String fileName, File file, String contentType, int[] resize) throws Exception;

	public long addOrphanedFile(String fileName, File file, String contentType) throws Exception {
		return addOrphanedFile(DEFAULT_NAMESPACE, fileName, file, contentType);
	}
	public abstract long addOrphanedFile(String namespace, String fileName, File file, String contentType) throws Exception;

	public long addFile(String fileName, String content, String contentType) throws Exception {
		return addFile(DEFAULT_NAMESPACE, fileName, content, contentType);
	}
	public abstract long addFile(String namespace, String fileName, String content, String contentType) throws Exception;

	public long addOrphanedFileForFormFields(String fileName, byte[] content, String contentType) throws Exception{
		return addFile(DEFAULT_NAMESPACE_ORPHAN, fileName, content, contentType,true);
	}
	public long addOrphanedFile(String fileName, byte[] content, String contentType) throws Exception {
		return addFile(DEFAULT_NAMESPACE, fileName, content, contentType,true);
	}
	public abstract long addFile(String namespace, String fileName, byte[] content, String contentType,boolean isOrphan) throws Exception;
	public abstract void addCompressedFile(String namespace, long fileId, FileInfo fileInfo) throws Exception;

	public FileInfo getFileInfo(long fileId) throws Exception {
		return getFileInfo(DEFAULT_NAMESPACE, fileId);
	}
	public FileInfo getFileInfo(String namespace, long fileId) throws Exception {
		return getFileInfo(namespace, fileId, false);
	}

	public FileInfo getFileInfo(long fileId, boolean fetchOriginal) throws Exception {
		return getFileInfo(DEFAULT_NAMESPACE, fileId, fetchOriginal);
	}
	public FileInfo getFileInfo(String namespace, long fileId, boolean fetchOriginal) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while fetching file info");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("SELECT * FROM {0} WHERE FILE_ID=? AND ORGID=? ORDER BY FILE_NAME", namespaceConfig.getTableName());
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());

			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(namespace, rs, fetchOriginal);
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
		return getResizedFileInfo(DEFAULT_NAMESPACE, fileId, width, height);
	}
	public FileInfo getResizedFileInfo(String namespace, long fileId, int width, int height) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while fetching resized file info");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			String sql = MessageFormat.format("SELECT {0}.FILE_ID, {0}.ORGID, {0}.FILE_NAME, {0}.UPLOADED_BY, {0}.UPLOADED_TIME, {1}.FILE_PATH, {1}.FILE_SIZE, {1}.CONTENT_TYPE, {1}.EXPIRY_TIME, {1}.URL FROM {1} join {0} on {1}.FILE_ID = {0}.FILE_ID WHERE {1}.FILE_ID=? AND {1}.ORGID=? AND {1}.WIDTH=? AND {1}.HEIGHT=?", namespaceConfig.getTableName(), namespaceConfig.getResizedTableName());
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, fileId);
			pstmt.setLong(2, getOrgId());
			pstmt.setInt(3, width);
			pstmt.setInt(4, height);

			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(namespace, rs);
				return fileInfo;
			}
			LOGGER.debug(pstmt);
		}
		catch(SQLException e) {
			LOGGER.error(e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return null;
	}

	public Map<Long, FileInfo> getFileInfoAsMap(List<Long> fileId, Connection conn) throws Exception {
		return getFileInfoAsMap(DEFAULT_NAMESPACE, fileId, conn);
	}
	public Map<Long, FileInfo> getFileInfoAsMap(String namespace, List<Long> fileId, Connection conn) throws Exception {

		// TODO return compressed file by default

		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while fetching file info as map");
		Map<Long, FileInfo> fileMap = new HashMap<Long, FileInfo>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isExternalConnection = true;
		try {
			if (conn == null) {
				isExternalConnection = false;
				conn = FacilioConnectionPool.INSTANCE.getConnection();
			}

			String sql = MessageFormat.format("SELECT * FROM {0} WHERE FILE_ID IN ({1})  AND ORGID=? ORDER BY FILE_NAME", namespaceConfig.getTableName(), StringUtils.join(fileId, ","));
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, getOrgId());

			rs = pstmt.executeQuery();
			while(rs.next()) {
				FileInfo fileInfo = getFileInfoFromRS(namespace, rs, true);
				fileMap.put(fileInfo.getFileId(), fileInfo);
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			if (isExternalConnection) {
				DBUtil.closeAll(pstmt, rs);
			}
			else {
				DBUtil.closeAll(conn, pstmt, rs);
			}
		}

		return fileMap;
	}

	protected List<String> getFilePathList(String namespace, List<Long> fileId) throws Exception {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while fetching file path lisr");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();

			String sql = MessageFormat.format("SELECT FILE_PATH FROM {0} WHERE FILE_ID IN ({1})  AND ORGID=? ORDER BY FILE_NAME", namespaceConfig.getTableName(), StringUtils.join(fileId, ","));
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

	private FileInfo getFileInfoFromRS(String namespace, ResultSet rs) throws Exception {
		return getFileInfoFromRS(namespace, rs, false);
	}

	private FileInfo getFileInfoFromRS(String namespace, ResultSet rs, boolean fetchOriginal) throws Exception {

		ResizedFileInfo fileInfo = new ResizedFileInfo();
		fileInfo.setOrgId(rs.getLong("ORGID"));
		fileInfo.setNamespace(namespace);
		fileInfo.setFileId(rs.getLong("FILE_ID"));
		if (rs.getString("FILE_NAME") != null) {
			fileInfo.setFileName(rs.getString("FILE_NAME"));
		}
		String compressedFilePath = null;
		try {
			compressedFilePath = rs.getString("COMPRESSED_FILE_PATH");
		}
		catch(SQLException e) {
		}
		if (compressedFilePath != null && !fetchOriginal) {
			fileInfo.setFilePath(compressedFilePath);
			fileInfo.setFileSize(rs.getLong("COMPRESSED_FILE_SIZE"));
		}
		else {
			if (rs.getString("FILE_PATH") != null) {
				fileInfo.setFilePath(rs.getString("FILE_PATH"));
			}
			fileInfo.setFileSize(rs.getLong("FILE_SIZE"));
		}
		fileInfo.setContentType(rs.getString("CONTENT_TYPE"));
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("id", rs.getLong("UPLOADED_BY"));
		fileInfo.setUploadedBy(userMap);
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

	//
	// Namespaces are handled in V3 file url
	private String getUrl (long fileId, boolean isDownload, int width, int height) throws Exception {
		StringBuilder url = new StringBuilder();

		if (FacilioProperties.isDevelopment()) {
			url.append(FacilioProperties.getClientAppUrl());
		}
		url.append("/api/v2/files/");

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
		return getUrl(fileId, false, -1, -1);
	}

	public String getPrivateUrl(long fileId, int width) throws Exception {
		return getUrl(fileId, false, width, -1);
	}

	public String getDownloadUrl(long fileId) throws Exception {
		if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {
			return getUrl (-1, -1, fileId, true, -1, -1,false);
		}
		return getUrl(fileId, true, -1, -1);
	}

	public int unOrphan(List<Long> fileIds) throws SQLException {
		return unOrphan(DEFAULT_NAMESPACE, fileIds);
	}
	public int unOrphan(String namespace, List<Long> fileIds) throws SQLException {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while making files unorphan");
		List<FacilioField> fields = FieldFactory.getFileFields(namespaceConfig.getTableName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		fields = Collections.singletonList(fieldMap.get("isOrphan"));
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(namespaceConfig.getTableName())
				;
		Map<String, Object> props = Collections.singletonMap("isOrphan", false);
		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
		for (long fileId : fileIds) {
			GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateContext();
			batchUpdate.setUpdateValue(props);
			batchUpdate.setWhereValue(Collections.singletonMap("fileId", fileId));
			batchUpdateList.add(batchUpdate);
		}
		return builder.batchUpdate(Collections.singletonList(fieldMap.get("fileId")), batchUpdateList);
	}

	public int markAsDeleted(List<Long> fileIds) throws SQLException {
		return markAsDeleted(DEFAULT_NAMESPACE, fileIds);
	}
	public int markAsDeleted(String namespace, List<Long> fileIds) throws SQLException {
		NamespaceConfig namespaceConfig = NAMESPACES.get(namespace);
		Objects.requireNonNull(namespaceConfig, "Invalid namespace while marking files as deleted");
		List<FacilioField> fields = FieldFactory.getFileFields(namespaceConfig.getTableName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		fields = new ArrayList<>(3);
		fields.add(fieldMap.get("isDeleted"));
		fields.add(fieldMap.get("deletedTime"));
		fields.add(fieldMap.get("deletedBy"));
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(namespaceConfig.getTableName())
				;

		Map<String, Object> props = new HashMap<>();
		props.put("isDeleted", true);
		props.put("deletedBy", AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1);
		props.put("deletedTime", System.currentTimeMillis());
		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
		for (long fileId : fileIds) {
			GenericUpdateRecordBuilder.BatchUpdateContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateContext();
			batchUpdate.setUpdateValue(props);
			batchUpdate.setWhereValue(Collections.singletonMap("fileId", fileId));
			batchUpdateList.add(batchUpdate);
		}
		return builder.batchUpdate(Collections.singletonList(fieldMap.get("fileId")), batchUpdateList);
	}

	public String orginalFileUrl (long fileId) throws Exception {
		return orginalFileUrl(DEFAULT_NAMESPACE, fileId);
	}
	public String orginalFileUrl (String namespace, long fileId) throws Exception {
		return getOrgiDownloadUrl(namespace, fileId);
	}

	public String newPreviewFileUrl (String moduleName, long fileId) throws Exception {
		return newPreviewFileUrl(moduleName, DEFAULT_NAMESPACE, fileId);
	}
	public String newPreviewFileUrl (String moduleName, String namespace, long fileId) throws Exception {
		return newPreviewFileUrl(moduleName, namespace, fileId, System.currentTimeMillis() + DEFAULT_FILE_URL_EXPIRY);
	}

	public String newPreviewFileUrl (String moduleName, long fileId, long expiryTime) throws Exception {
		return newPreviewFileUrl(moduleName, DEFAULT_NAMESPACE, fileId, expiryTime);
	}
	public String newPreviewFileUrl (String moduleName, String namespace, long fileId, long expiryTime) throws Exception {
		String url = getUrl(moduleName, namespace, fileId, expiryTime, false);
		return url;
	}
	public String newPreviewFileUrl (String moduleName, String namespace, long fileId, long expiryTime,long publicFileId,long orgId){
		String url = getPublicUrl(moduleName, namespace, fileId, expiryTime, false,publicFileId);
		return url;
	}

	public String newDownloadFileUrl (String moduleName, long fileId) throws Exception {
		return newDownloadFileUrl(moduleName, DEFAULT_NAMESPACE, fileId);
	}
	public String newDownloadFileUrl (String moduleName, String namespace, long fileId) throws Exception {
		return newDownloadFileUrl(moduleName, namespace, fileId,  System.currentTimeMillis() + DEFAULT_FILE_URL_EXPIRY);
	}

	public String newDownloadFileUrl (String moduleName, long fileId, long expiryTime) throws Exception {
		return newDownloadFileUrl(moduleName, DEFAULT_NAMESPACE, fileId, expiryTime);
	}
	public String newDownloadFileUrl (String moduleName, String namespace, long fileId, long expiryTime) throws Exception {
		String url = getUrl(moduleName, namespace, fileId, expiryTime, true);
		return url;
	}
	private String getToken(String moduleName, String namespace, long fileId, long expiryTime,long publicFileId){
		Objects.requireNonNull(moduleName, "Module Name cannot be null for url generation");
		Objects.requireNonNull(namespace, "Namespace cannot be null for url generation");
		Map<String, String> claims = new HashMap<>();
		claims.put("moduleName", moduleName);
		claims.put("namespace", namespace);
		claims.put("fileId", String.valueOf(fileId));
		claims.put("expiresAt", String.valueOf(expiryTime));
		if(publicFileId>0){
			claims.put("publicFileId",String.valueOf(publicFileId));
		}
		return FileJWTUtil.generateFileJWT(claims);
	}

	private String getPublicUrl(String moduleName, String namespace, long fileId, long expiryTime, boolean isDownload,long publicFileId){
		String token = getToken(moduleName,namespace,fileId,expiryTime,publicFileId);
		StringBuilder url = new StringBuilder();
		if (FacilioProperties.isDevelopment()) {
			url.append(FacilioProperties.getServerName());
		}
		url.append("/api/v3/file/");
		if(AccountUtil.getCurrentOrg() !=null) {
			url.append("app/");
		} else {
			url.append("public/");
		}
		url.append(token);
		if(isDownload) {
			url.append("/download");
		} else {
			url.append("/preview");
		}
		return url.toString();
	}
	private String getUrl (String moduleName, String namespace, long fileId, long expiryTime, boolean isDownload) {
		String token = getToken(moduleName,namespace,fileId,expiryTime,-1);
		StringBuilder url = new StringBuilder();
		if (FacilioProperties.isDevelopment()) {
			url.append(FacilioProperties.getServerName());
		}
		url.append("/api/v3/files/");
		if(isDownload) {
			url.append("download/");
		} else {
			url.append("preview/");
		}
		if(AccountUtil.getCurrentOrg() !=null) {
			url.append("app");
		} else {
			url.append("public");
		}
		url.append("?q=");
		return url.toString()+token;
	}

	public InputStream readFile(long fileId) throws Exception {
		return readFile(DEFAULT_NAMESPACE, fileId);
	}
	public abstract InputStream readFile(String namespace, long fileId) throws Exception;

	public InputStream readFile(long fileId, boolean fetchOriginal) throws Exception {
		return readFile(DEFAULT_NAMESPACE, fileId, fetchOriginal);
	}
	public abstract InputStream readFile(String namespace, long fileId, boolean fetchOriginal) throws Exception;

	public abstract InputStream readFile(FileInfo fileInfo) throws Exception;

	public boolean deleteFile(long fileId) throws Exception {    // Mark As Deleted
		return deleteFile(DEFAULT_NAMESPACE, fileId);
	}
	public abstract boolean deleteFile(String namespace, long fileId) throws Exception;	// Mark As Deleted

	public boolean deleteFiles(List<Long> fileId) throws Exception {    // Mark As Deleted
		return deleteFiles(DEFAULT_NAMESPACE, fileId);
	}
	public abstract boolean deleteFiles(String namespace, List<Long> fileId) throws Exception; 	// Mark As Deleted

	public boolean deleteFilePermanently(long fileId) throws Exception {
		return deleteFilePermanently(DEFAULT_NAMESPACE, fileId);
	}
	public abstract boolean deleteFilePermanently(String namespace, long fileId) throws Exception;

	public boolean deleteFilesPermanently(List<Long> fileIds) throws Exception {
		return deleteFilesPermanently(DEFAULT_NAMESPACE, fileIds);
	}
	public abstract boolean deleteFilesPermanently(String namespace, List<Long> fileIds) throws Exception;

	public boolean renameFile(long fileId, String newName) throws Exception {
		return renameFile(DEFAULT_NAMESPACE, fileId, newName);
	}
	public abstract boolean renameFile(String namespace, long fileId, String newName) throws Exception;

	public String getOrgiFileUrl(long fileId) throws Exception {
		return getOrgiFileUrl(DEFAULT_NAMESPACE, fileId);
	}
	public abstract String getOrgiFileUrl(String namespace, long fileId) throws Exception;

	public String getOrgiDownloadUrl(long fileId) throws Exception {
		return getOrgiDownloadUrl(DEFAULT_NAMESPACE, fileId);
	}
	public abstract String getOrgiDownloadUrl(String namespace, long fileId) throws Exception;

	public abstract boolean isFileExists(String newVersion);

	public long addDummySecretFileEntry(String filename) throws Exception {
		System.out.println("addDummy called");
		Map<String, Object> record = new HashMap<>();
		record.put("fileName",filename);
		record.put("uploadedTime", System.currentTimeMillis());
		long id =new GenericInsertRecordBuilder().table(ModuleFactory.getSecretFileModule().getTableName())
				.fields((List<FacilioField>) FieldFactory.getSecretFileFields())
				.insert(record);
		return id;

	}

	public void updateSecretFileEntry(long fileId, String fileName, String filePath, long fileSize, String contentType) throws SQLException {
		//TODO

		List <FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("fileName","FILE_NAME", FieldType.STRING));
		fields.add(FieldFactory.getField("filePath","FILE_PATH", FieldType.STRING));
		fields.add(FieldFactory.getField("fileSize","FILE_SIZE", FieldType.NUMBER));
		fields.add(FieldFactory.getField("contentType","CONTENT_TYPE", FieldType.STRING));
		Map<String, Object> fieldsToUpdate = new HashMap<>();
		fieldsToUpdate.put("fileName", fileName);
		fieldsToUpdate.put("filePath", filePath);
		fieldsToUpdate.put("fileSize", fileSize);
		fieldsToUpdate.put("contentType", contentType);
		System.out.println("testing update secretFileEntry");
		System.out.println(fileId + " : "+fileName + " : "+filePath + " : "+fileSize + " : " + contentType) ;
		new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getSecretFileModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSecretFileIdField(), Collections.singleton(fileId), NumberOperators.EQUALS))
				.fields(fields)
				.update(fieldsToUpdate);
	}
	void deleteSecretFileEntry(long fieldId) {
		//todo
		//delete entry
	}
	public List<FileInfo> listSecretFiles() throws Exception {

		List<FileInfo> list = new ArrayList<>();
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getSecretFileModule().getTableName())
				.select(FieldFactory.getSecretFileFields()).limit(100);
		List<Map<String, Object>> res = genericSelectRecordBuilder.get();
		for (Map<String,Object> row:
				res) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setContentType(row.get("contentType").toString());
			fileInfo.setFileId(Long.parseLong(row.get("fileId").toString()));
			fileInfo.setFileName(row.get("fileName").toString());
			fileInfo.setFilePath(row.get("filePath").toString());
			fileInfo.setFileSize(Long.parseLong(row.get("fileSize").toString()));
			fileInfo.setUploadedTime(Long.parseLong(row.get("uploadedTime").toString()));
			list.add(fileInfo);
		}
		return list;
	}

	public boolean markSecretFileAsDeleted (String fileName) throws SQLException {
		Map<String, Object> toUpdate = new HashMap<>();
		toUpdate.put("isDeleted",true);
		return new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getSecretFileModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSecretFileIdField(), fileName, StringOperators.IS))
				.update(toUpdate) >0;
	}

	public abstract long addSecretFile(String fileName,File file,String contentType) throws Exception;

	public abstract InputStream getSecretFile(String tag) throws Exception;

	public abstract boolean removeSecretFile(String tag) throws Exception;

	public abstract boolean isSecretFileExists(String fileName);
	
	protected void scheduleCompressJob(String namespace, long fileId, String contentType) throws Exception {
		if (contentType.contains("image/")) {
			FacilioContext context = new FacilioContext();
			context.put(ContextNames.FILE_NAME_SPACE, namespace);
			context.put(ContextNames.FILE_ID, fileId);
			FacilioTimer.scheduleInstantJob("CompressImageJob", context);
		}
	}

	public byte[] writeCompressedFile(String namespace, long fileId, FileInfo fileInfo, ByteArrayOutputStream baos, String compressedFilePath) throws Exception {
		try (InputStream inputStream = this.readFile(namespace, fileId, true)){
			BufferedImage imBuff = ImageIO.read(inputStream);
			ImageScaleUtil.compressImage(imBuff, baos, fileInfo.getContentType());

			byte[] imageInByte = baos.toByteArray();
			if (imageInByte.length >= fileInfo.getFileSize()) {	// Small files may be compressed to more size bcz of metadata
				return null;
			}
			updateFileEntry(namespace, fileId, compressedFilePath, imageInByte.length);

			baos.flush();
			imBuff.flush();

			return imageInByte;
		}
		catch (Exception e) {
			LOGGER.error("Error while compressing", e);
		}
		return null;
	}

	public long addFileFromURL(String urlString) throws Exception {
		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();
		String fileName = FilenameUtils.getName(url.getPath());
		File file = File.createTempFile(fileName, "");
		FileUtils.copyURLToFile(url, file);
		String mimeType = uc.getContentType();
		return addFile(fileName, file, mimeType);
	}

	public long addFileFromStream(String fileName, String contentType, InputStream inputStream) throws Exception {
		File file = File.createTempFile(fileName, "");
		FileUtils.copyInputStreamToFile(inputStream, file);
		return addFile(fileName, file, contentType);
	}

	public String getFileUrlForOrg(ApplicationContext app, long moduleId, long recordId, String namespace, long fileId, long expiryTime, boolean isDownload, boolean isModuleFile){
		String token = getToken(moduleId,recordId,namespace,fileId,expiryTime,-1,isModuleFile);
		StringBuilder url = new StringBuilder();
		if(!(AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().isFromMobile())) {
			url.append("/");
			url.append(app.getLinkName());
		}
		url.append("/api/v3/files/");
		if(isDownload) {
			url.append("download/");
		} else {
			url.append("preview/");
		}
		url.append("module/");
		url.append(token);
		url.append("/token");
		return url.toString();
	}


	private String getToken(long moduleId, long recordId, String namespace, long fileId, long expiryTime,long publicFileId,boolean isModuleFile){
		Objects.requireNonNull(moduleId, "Module Id cannot be null for url generation");
		Objects.requireNonNull(namespace, "Namespace cannot be null for url generation");
		Map<String, String> claims = new HashMap<>();
		claims.put("moduleId", String.valueOf(moduleId));
		claims.put("namespace", namespace);
		claims.put("fileId", String.valueOf(fileId));
		claims.put("expiresAt", String.valueOf(expiryTime));
		claims.put("recordId",String.valueOf(recordId));
		claims.put("moduleFile",String.valueOf(isModuleFile));
		if(publicFileId>0){
			claims.put("publicFileId",String.valueOf(publicFileId));
		}
		return FileJWTUtil.generateFileJWT(claims);
	}


	public String getPrivateUrl(long moduleId, long recordId, long fileId,boolean isModuleFile) throws Exception {
		return getUrl(moduleId, recordId, fileId, false, -1, -1,isModuleFile);
	}

	public String getDownloadUrl(Long moduleId, long recordId, long fileId, boolean isModuleFile) throws Exception {
		return getUrl(moduleId, recordId, fileId, true, -1, -1,isModuleFile);
	}


	private String getUrl (long moduleId, long recordId, long fileId, boolean isDownload, int width, int height,boolean isModuleFile) throws Exception {
		StringBuilder url = new StringBuilder();
		if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {
			if(FacilioProperties.isDevelopment()) {
				url.append(FacilioProperties.getClientAppUrl());
			}
			url.append(PublicFileUtil.createFileUrlForOrg(moduleId, recordId, fileId, isDownload, isModuleFile));
			return url.toString();
		}
		return null;
	}
}