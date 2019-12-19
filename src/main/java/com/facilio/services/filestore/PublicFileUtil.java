package com.facilio.services.filestore;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;

public class PublicFileUtil {

	private static final long PUBLIC_FILE_EXPIRY_IN_MILLIS = 300000; //5 mins
	
	
	private static long createFile(String content,String fileName,String fileType,String contentType) throws Exception {
		
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
	
	public static String createPublicFile(String content,String fileName,String fileType,String contentType) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		PublicFileContext file = FacilioService.runAsServiceWihReturn(() -> insertPublicFile(orgId, content, fileName, fileType, contentType));
		return FacilioService.runAsServiceWihReturn(() ->FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", file.getFileId(), file.getExpiresOn()));
	}
	
	public static String createPublicFile(File file,String fileName, String fileType,String contentType) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		PublicFileContext publicFile = FacilioService.runAsServiceWihReturn(() -> insertPublicFile(orgId, file, fileName, fileType, contentType));
		return FacilioService.runAsServiceWihReturn(() ->FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", publicFile.getFileId(), publicFile.getExpiresOn()));
	}
	
	private static PublicFileContext insertPublicFile(long orgId, File file, String fileName, String fileType, String contentType) throws Exception {
		return addPublicFile(orgId, FacilioFactory.getFileStore().addFile(fileName, file, contentType));
	}
	
	private static PublicFileContext insertPublicFile(long orgId, String content, String fileName, String fileType, String contentType) throws Exception {
		long fileId = createFile(content, fileName, fileType, contentType);
		return addPublicFile(orgId, fileId);
	}
	
	private static PublicFileContext addPublicFile (long orgId, long fileId) throws Exception {
		PublicFileContext publicFileContext = new PublicFileContext();
		publicFileContext.setOrgId(orgId);
		publicFileContext.setExpiresOn(DateTimeUtil.getCurrenTime()+PUBLIC_FILE_EXPIRY_IN_MILLIS);
		
//		String key = CryptoUtils.hash256(""+fileID+DateTimeUtil.getCurrenTime());
//		
//		publicFileContext.setKey(key);
		
		publicFileContext.setFileId(fileId);
		
		addPublicFileContext(publicFileContext);
		
		return publicFileContext;
	}
	
	private static PublicFileContext addPublicFileContext(PublicFileContext publicFileContext) throws Exception {
		Map<String, Object> props = FieldUtil.getAsProperties(publicFileContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPublicFilesModule().getTableName())
				.fields(FieldFactory.getPublicFileFields())
				.addRecord(props);
		
		insert.save();
		
		publicFileContext.setId((Long)props.get("id"));
		return publicFileContext;
	}
}
