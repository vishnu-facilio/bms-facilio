/**
 * 
 */
package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;

/**
 * @author facilio
 *
// */
public class ImportPointsDataAction extends FacilioAction{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ImportPointsDataAction.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportPointsDataAction.class.getName());
	
	public String upload() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_NAME, fileUploadFileName);
		context.put(FacilioConstants.ContextNames.FILE, fileUpload);
		context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileUploadContentType);
		context.put(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT, pointsProcessContext);
		context.put(FacilioConstants.ContextNames.IMPORT_MODE, importMode);
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		
		Chain uploadFile = TransactionChainFactory.UploadImportPointsFileChain();
		uploadFile.execute(context);
		
		PointsProcessContext imp = (PointsProcessContext) context.get(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT);
		setResult(FacilioConstants.ContextNames.POINTS_PROCESS_CONTEXT ,imp);
        
		return SUCCESS;
	}
	
	public String deleteFile() throws Exception{
		FileStore fs  = FileStoreFactory.getInstance().getFileStore();
		if(fs.deleteFile(fileId)) {
			System.out.println("File " + fileId + " has been deleted");
		}
		return SUCCESS;
	}
	long controllerId=-1;
	
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}

	String pastedRawString;
	String rawStringType;
	
	public String getPastedRawString() {
		return pastedRawString;
	}
	public void setPastedRawString(String pastedRawString) {
		this.pastedRawString = pastedRawString;
	}
	public String getRawStringType() {
		return rawStringType;
	}
	public void setRawStringType(String rawStringType) {
		this.rawStringType = rawStringType;
	}
	List<Map<Integer,String>> parsedData;
	public List<Map<Integer, String>> getParsedData() {
		return parsedData;
	}
	Map<Integer,String> columnMaping;
	public void setParsedData(List<Map<Integer, String>> parsedData) {
		this.parsedData = parsedData;
	}
	public String importPasteData() throws Exception {
		
		parsedData = ImportAPI.parseRawString(pastedRawString, rawStringType);
		
		return SUCCESS;
	}
	public String importParsedData() throws Exception {
		
		Map<Integer, String> test = new HashMap<Integer, String>();
		parsedData = new ArrayList<>();
		
		test.put(1, "testasset1");
		test.put(2, "testcat1");
		parsedData.add(test);
		test = new HashMap<Integer, String>();
		
		test.put(1, "testasset2");
		test.put(2, "testcat2");
		parsedData.add(test);
		
		columnMaping = new HashMap<>();
		
		columnMaping.put(1, "name");
		columnMaping.put(2, "category");
		
		
		ImportAPI.importPasteParsedData(parsedData,columnMaping,pointsProcessContext);
		
		return SUCCESS;
	}
	
	public String beginImportValidation() throws Exception{
		
		pointsProcessContext.setStatus(PointsProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(pointsProcessContext);
		FacilioContext context = new FacilioContext();
		context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, this.pointsProcessContext);
		FacilioTimer.scheduleInstantJob("ImportReadingLogJob", context);
		return SUCCESS;
	}
	
	public String processImport() throws Exception
	{	
		pointsProcessContext.setStatus(PointsProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
		ImportPointsAPI.updateImportPoints(pointsProcessContext);
		FacilioContext context = new FacilioContext();
		context.put(ImportPointsAPI.ImportPointsConstants.POINTS_PROCESS_CONTEXT, pointsProcessContext);
		FacilioTimer.scheduleInstantJob("ReadingImportPointsDataJob", context);
		
		return SUCCESS;
	}
	
	public String fetchAllUnvalidated() throws Exception{
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportPointsFields())
				.table(ModuleFactory.getImportPointsModule().getTableName())
				.andCustomWhere("STATUS", PointsProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());		
		records = selectRecordBuilder.get();
		
		return SUCCESS;		
	}
	
	public String finishValidation() throws Exception{
		
		for(ImportProcessLogContext row: validatedRecords) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getImportProcessLogModule().getTableName())
					.fields(FieldFactory.getImportProcessLogFields());
			updateBuilder.andCustomWhere("ID = ?", row.getId());
			JSONObject props = FieldUtil.getAsJSON(row);
			updateBuilder.update(props);
		}
		
		pointsProcessContext.setStatus(PointsProcessContext.ImportStatus.VALIDATION_COMPLETE.getValue());
		ImportAPI.updateImportProcess(pointsProcessContext);
		return SUCCESS;
	}
	
	
	public String fetchImportHistory() throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule importModule = modBean.getModule(ModuleFactory.getImportProcessModule().getName());
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.select(FieldFactory.getImportProcessFields())
				.andCustomWhere("ORGID = ?", orgId)
				.orderBy("IMPORT_TIME desc")
				.limit(10);
		
		List<Map<String, Object>> temp = selectRecordBuilder.get();
		importHistory = FieldUtil.getAsBeanListFromMapList(temp, PointsProcessContext.class);
		return SUCCESS;
	}
	
	public String fetchDataForValidation() throws Exception{
		List<FacilioField> fields = FieldFactory.getImportProcessLogFields();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportProcessLogFields())
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IMPORTID","importId", pointsProcessContext.getId().toString(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ERROR_RESOLVED", "error_resolved", PointsProcessContext.ImportLogErrorStatus.UNRESOLVED.getStringValue(), NumberOperators.EQUALS));
		
		records = selectRecordBuilder.get();
		if(pointsProcessContext.importMode == PointsProcessContext.ImportMode.NORMAL.getValue()) {
			if(pointsProcessContext.getModule().getParentModule().getName().equals("resource") 
				|| pointsProcessContext.getModule().getParentModule().getName().equals("asset") 
					) {
				unvalidatedRecords = ImportAPI.setAssetName(pointsProcessContext, pointsProcessContext.getModule().getParentModule(), FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class));
			}
			else {
				unvalidatedRecords = ImportAPI.setAssetName(pointsProcessContext, pointsProcessContext.getModule(), FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class));
			}
		}
		else {
			unvalidatedRecords = FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class);
		}
		return SUCCESS;
	}
	
	
	
	
	public String importData() throws Exception{
		if(assetId > 0) {
			pointsProcessContext.setAssetId(assetId);
			
			JSONObject scheduleInfo = new JSONObject();
			scheduleInfo.put("assetId", assetId);
			
			pointsProcessContext.setImportJobMeta(scheduleInfo.toJSONString());
		}
				
		pointsProcessContext.setStatus(PointsProcessContext.ImportStatus.IN_PROGRESS.getValue());
		updateImportPoints(pointsProcessContext, ImportStatus.IN_PROGRESS);
		FacilioTimer.scheduleOneTimeJobWithDelay(pointsProcessContext.getId(), "importData" , 10, "priority");
		
		return SUCCESS;
	}
	
	public static void updateImportPoints(ImportProcessContext importProcessContext,ImportStatus importStatus) throws Exception {
			
			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getImportPointsModule().getTableName());
			update.fields(FieldFactory.getImportPointsFields());
			update.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".ID = ?", importProcessContext.getId());
			
			Map<String, Object> props = new HashMap<>();
			
			props.put("status", importStatus.getValue());
			
			update.update(props);	
			
		}
	
	
	public String checkImportStatus() throws Exception {
		PointsProcessContext im = ImportPointsAPI.getImportProcessContext(pointsProcessContext.getId());
		Integer mail = pointsProcessContext.getMailSetting();
		Integer status = im.getStatus();
		JSONObject meta = im.getImportJobMetaJson();
		Integer setting = pointsProcessContext.getImportSetting();
		
		if(status == 8 && mail ==null) {
			if(setting == PointsProcessContext.ImportSetting.INSERT.getValue()) {
				String inserted = meta.get("Inserted").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				if(pointsProcessContext.getImportMode() == 2) {
					String Skipped = meta.get("Skipped").toString();
					im.setSkipEntries(Integer.parseInt(Skipped));
				}
			}
			else if(setting == PointsProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
				String skipped = meta.get("Skipped").toString();
				String inserted = meta.get("Inserted").toString();
				im.setSkipEntries(Integer.parseInt(skipped));
				im.setnewEntries(Integer.parseInt(inserted));
			}
			else if(setting == PointsProcessContext.ImportSetting.UPDATE.getValue()) {
				String updated = meta.get("Updated").toString();
				im.setupdateEntries(Integer.parseInt(updated));
			}
			else if(setting == PointsProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
				String updated = meta.get("Updated").toString();
				im.setupdateEntries(Integer.parseInt(updated));
			}
			
			else if(setting == PointsProcessContext.ImportSetting.BOTH.getValue()) {
				String inserted = meta.get("Inserted").toString();
				String updated = meta.get("Updated").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				im.setupdateEntries(Integer.parseInt(updated));
			}
			else if(setting == PointsProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
				String inserted = meta.get("Inserted").toString();
				String updated = meta.get("Updated").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				im.setupdateEntries(Integer.parseInt(updated));				
			}
		}
		else if(status == 7 && mail!=null) {
			ImportAPI.updateImportProcess(getPointsProcessContext());
		}
		pointsProcessContext = im;
		return SUCCESS;
	}
	
	
	public String showformupload()
	{
		LOGGER.severe("Displaying formupload");
		return SUCCESS;
	}

	private File fileUpload;
	public File getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}
	public String getFileUploadContentType() {
		return fileUploadContentType;
	}
	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}
	public String getFileUploadFileName() {
		return fileUploadFileName;
	}
	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}

	private List<PointsProcessContext> importHistory = new ArrayList<PointsProcessContext>();
	
	public List<PointsProcessContext> getImportHistory() {
		return importHistory;
	}
	public void setImportHistory(List<PointsProcessContext> importHistory) {
		this.importHistory = importHistory;
	}

	private String fileUploadContentType;
	private String fileUploadFileName;
	
	private List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
	private List<ImportProcessLogContext> validatedRecords = new ArrayList<ImportProcessLogContext>();
	
	public List<ImportProcessLogContext> getValidatedRecords() {
		return validatedRecords;
	}
	public void setValidatedRecords(List<ImportProcessLogContext> validatedRecords) {
		this.validatedRecords = validatedRecords;
	}
	public List<Map<String, Object>> getRecords() {
		return records;
	}
	
	public List<ImportProcessLogContext> unvalidatedRecords = new ArrayList<ImportProcessLogContext>();
	
	public List<ImportProcessLogContext> getUnvalidatedRecords() {
		return unvalidatedRecords;
	}
	public void setUnvalidatedRecords(List<ImportProcessLogContext> unvalidatedRecords) {
		this.unvalidatedRecords = unvalidatedRecords;
	}
	
	public void setRecords(List<Map<String, Object>> records) {
		this.records = records;
	}
	
	public long getImportprocessid() {
		return importprocessid;
	}
	public void setImportprocessid(long importprocessid) {
		LOGGER.severe("setting the id "+importprocessid);
		this.importprocessid = importprocessid;
	}

	private long importprocessid=0;

	private static String UPDATEQUERY = "update  ImportProcess set FIELD_MAPPING='#FIELD_MAPPING#' where IMPORTID_ID=#IMPORTID#";	

	Integer importMode;

	public Integer getImportMode() {
		return importMode;
	}
	public void setImportMode(Integer importMode) {
		this.importMode = importMode;
	}

    PointsProcessContext pointsProcessContext =new PointsProcessContext();
	
	public PointsProcessContext getPointsProcessContext() {
		return pointsProcessContext;
	}
	public void setPointsProcessContext(PointsProcessContext pointsProcessContext) {
		this.pointsProcessContext = pointsProcessContext;
	}

	Long assetCategory;
	
	public Long getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(Long assetCategory) {
		this.assetCategory = assetCategory;
	}
	
	public void setAssetId(long id) {
		this.assetId = id;
	}
	private long assetId;
	private Long fileId;
	private long orgId;
	
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}


}
