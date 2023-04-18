package com.facilio.services.filestore;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import static com.facilio.services.filestore.FileStore.DEFAULT_NAMESPACE;

public class PublicFileUtil {

	private static final long PUBLIC_FILE_EXPIRY_IN_MILLIS = 300000; //5 mins


	private static long createFile(String content,String fileName,String fileType,String contentType) throws Exception {
		if(!fileName.contains(".")) {
			fileName = fileName + "."+ fileType;
		}

		try(FileWriter writer = new FileWriter(fileName, false);) {
			writer.append(content);
			writer.flush();

			File file = new File(fileName);
			file.createNewFile();
			FileStore fs = FacilioFactory.getFileStore();
			long fileId = fs.addFile(file.getPath(), file, contentType);

			return fileId;
		}
	}

	public static String createPublicFile(String content,String fileName,String fileType,String contentType, long expiresOn) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> insertPublicFile(orgId, content, fileName, fileType, contentType, expiresOn));
	}

	public static String createPublicFile(String content,String fileName,String fileType,String contentType) throws Exception {
		return createPublicFile(content, fileName, fileType, contentType, System.currentTimeMillis() + PUBLIC_FILE_EXPIRY_IN_MILLIS);
	}

	public static String createPublicFile(File file,String fileName, String fileType,String contentType) throws Exception {
		return createPublicFile(file, fileName, fileType, contentType, System.currentTimeMillis() + PUBLIC_FILE_EXPIRY_IN_MILLIS);
	}

	public static String createPublicFileWithOrgId(File file, String fileName, String fileType, String contentType, long expiresOn) throws Exception {
		return insertPublicFile(file, fileName, fileType, contentType, expiresOn);
	}

	public static String createPublicFile(File file,String fileName, String fileType,String contentType, long expiresOn) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() -> insertPublicFile(orgId, file, fileName, fileType, contentType, expiresOn));
	}

	public static String createPublicFileUrl(long fileId) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> insertPublicFileWithNoExpiry(orgId, fileId));
	}

	private static String insertPublicFileWithNoExpiry(long orgId, long fileId) throws Exception {
		PublicFileContext publicFile = addPublicFile(orgId, fileId, -1, true);
		return FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", DEFAULT_NAMESPACE, publicFile.getFileId(), publicFile.getExpiresOn(), publicFile.getId(), orgId);
	}

	public static String createFileUrlForOrg(long moduleId, long recordId, long fileId, boolean isDownload, boolean isModuleFile) throws Exception {
		if(AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() > 0 ) {
			ApplicationContext currentApp = AccountUtil.getCurrentApp();
			if(currentApp != null) {
				return FileStoreFactory.getInstance().getFileStore().getFileUrlForOrg(currentApp, moduleId, recordId, DEFAULT_NAMESPACE, fileId, -1, isDownload,isModuleFile);
			}
		}
		return null;
	}

	private static String insertPublicFile(long orgId, File file, String fileName, String fileType, String contentType, long expiresOn) throws Exception {
		PublicFileContext publicFile = addPublicFile(orgId, FacilioFactory.getFileStore().addFile(fileName, file, contentType), expiresOn);
		return FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", publicFile.getFileId(), publicFile.getExpiresOn());
	}

	private static String insertPublicFile(File file, String fileName, String fileType, String contentType, long expiresOn) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		long fileId = FacilioFactory.getFileStore().addFile(fileName, file, contentType);
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> (addPublicFileReturnUrl(orgId, fileId, expiresOn)));
	}

	private static String addPublicFileReturnUrl(long orgId, long fileId, long expiresOn) throws Exception {
		PublicFileContext publicFile = addPublicFile(orgId, fileId, expiresOn);
		return FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", DEFAULT_NAMESPACE, publicFile.getFileId(), publicFile.getExpiresOn(), publicFile.getId(), orgId);
	}

	private static String insertPublicFile(long orgId, String content, String fileName, String fileType, String contentType, long expiresOn) throws Exception {
		long fileId = createFile(content, fileName, fileType, contentType);
		PublicFileContext publicFile = addPublicFile(orgId, fileId, expiresOn);
		return FileStoreFactory.getInstance().getFileStore().newPreviewFileUrl("public", publicFile.getFileId(), publicFile.getExpiresOn());
	}

	private static PublicFileContext addPublicFile(long orgId, long fileId, long expiresOn) throws Exception {
		return addPublicFile(orgId, fileId, expiresOn, false);
	}

	private static PublicFileContext addPublicFile(long orgId, long fileId, long expiresOn, boolean isFromFilesTable) throws Exception {
		PublicFileContext publicFileContext = new PublicFileContext();
		publicFileContext.setOrgId(orgId);
		publicFileContext.setExpiresOn(expiresOn);

//		String key = CryptoUtils.hash256(""+fileID+DateTimeUtil.getCurrenTime());
//
//		publicFileContext.setKey(key);
		publicFileContext.setIsFromFilesTable(isFromFilesTable);
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

	public static PublicFileContext getPublicFile(long publicFileId, long orgId) throws Exception {
		Map<String,Object> prop = getPublicFileObj(publicFileId,orgId);
		if(MapUtils.isNotEmpty(prop)) {
			PublicFileContext publicFileContext = FieldUtil.getAsBeanFromMap(prop,PublicFileContext.class);
			return publicFileContext;
		}
		return null;
	}
	public static Map<String, Object> getPublicFileObj(long publicFileId, long orgId) throws Exception {

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getPublicFilesModule().getTableName())
				.select(FieldFactory.getPublicFileFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(publicFileId), NumberOperators.EQUALS));
		if (orgId > 0) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}

		return selectRecordBuilder.fetchFirst();
	}
}
