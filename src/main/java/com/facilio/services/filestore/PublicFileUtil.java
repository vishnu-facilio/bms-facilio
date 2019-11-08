package com.facilio.services.filestore;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.CryptoUtils;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;

public class PublicFileUtil {

	private static final long PUBLIC_FILE_EXPIRY_IN_MILLIS = 300000; //5 mins
	
	
	public static long createFile(String content,String fileName,String fileType,String contentType) throws Exception {
		
		if(!fileName.contains(".")) {
			fileName = fileName + "."+ fileType;
		}
    	FileWriter writer = new FileWriter(fileName, false);
    	
    	writer.append(content);
    	
    	writer.flush();
    	writer.close();
    	
    	File file = new File(fileName);
    	file.createNewFile();
    	FileStore fs = FacilioFactory.getFileStore();
    	long fileId = fs.addFile(file.getPath(), file, contentType);
			
    	return fileId;
	}
	
	public static PublicFileContext createPublicFile(String content,String fileName,String fileType,String contentType) throws Exception {
		
		long fileID = createFile(content, fileName, fileType, contentType);
		
		PublicFileContext publicFileContext = new PublicFileContext();
		
		publicFileContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		publicFileContext.setExpiresOn(DateTimeUtil.getCurrenTime()+PUBLIC_FILE_EXPIRY_IN_MILLIS);
		
		String key = CryptoUtils.hash256(""+fileID+DateTimeUtil.getCurrenTime());
		
		publicFileContext.setKey(key);
		
		publicFileContext.setFileId(fileID);
		
		Map<String, Object> props = FieldUtil.getAsProperties(publicFileContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPublicFilesModule().getTableName())
				.fields(FieldFactory.getPublicFileFields())
				.addRecord(props);
		
		insert.save();
		
		publicFileContext.setId((Long)props.get("id"));
		
		return publicFileContext;
			
	}
	
	private static PublicFileContext getPublicFileInfoFromRS(ResultSet rs) throws Exception {
		
		PublicFileContext context = new PublicFileContext();
		
		context.setId(rs.getLong("ID"));
		context.setOrgId(rs.getLong("ORGID"));
		context.setFileId(rs.getLong("FILEID"));
		context.setKey(rs.getString("FILE_KEY"));
		context.setExpiresOn(rs.getLong("EXPIRES_ON"));
		
		return context;
	}
	
	public static PublicFileContext getPublicFileFromKey(String key) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Public_Files WHERE FILE_KEY=?");
			pstmt.setString(1, key);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				PublicFileContext context = getPublicFileInfoFromRS(rs);
				return context;
			}
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return null;
		
//		
//		List<FacilioField> fields = FieldFactory.getPublicFileFields();
//		
//		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
//		
//		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
//				.select(fields)
//				.table(ModuleFactory.getPublicFilesModule().getTableName())
//				.andCondition(CriteriaAPI.getCondition(fieldMap.get("key"), key, StringOperators.IS));
//		
//		Map<String, Object> props = selectRecordBuilder.fetchFirst();
//		
//		if(props != null) {
//			PublicFileContext publicFileContext = FieldUtil.getAsBeanFromMap(props, PublicFileContext.class);
//			return publicFileContext;
//		}
//		return null;
	}
}
