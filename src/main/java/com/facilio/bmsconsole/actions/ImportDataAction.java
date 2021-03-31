package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

;

public class ImportDataAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ImportDataAction.class.getName());
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportDataAction.class.getName());
	public String upload() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_NAME, fileUploadFileName);
		context.put(FacilioConstants.ContextNames.FILE, fileUpload);
		context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileUploadContentType);
		context.put(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT, importProcessContext);
		context.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategory);
		context.put(FacilioConstants.ContextNames.IMPORT_MODE, importMode);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		context.put(FacilioConstants.ContextNames.TEMPLATE_ID, templateId);
		context.put(ImportAPI.ImportProcessConstants.MODULE_META, moduleMeta);
		context.put(FacilioConstants.ContextNames.SITE_ID, siteId);
		
		FacilioChain uploadFile = TransactionChainFactory.uploadImportFileChain();
		uploadFile.execute(context);

		ImportProcessContext imp = (ImportProcessContext) context.get(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT);
		setResult(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT ,imp);
        
		return SUCCESS;
	}
	
	public String checkModule() throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		JSONObject moduleInfo = new JSONObject();
		if(module != null) {
			moduleInfo.put("moduleExists", true);
			moduleInfo.put("moduleMeta", module);
			if (module.getModuleId() > 0) {
				importProcessContext.setModuleId(module.getModuleId());
			} else if (StringUtils.isNotEmpty(module.getName())) {
				importProcessContext.setModuleName(module.getName());
			}
		}
		else {
			moduleInfo.put("moduleExists", false);
		}
		importProcessContext.setImportMode(1);

		
		setModuleExists(moduleInfo);
		
		return SUCCESS;
	}
	
	public String deleteFile() throws Exception{
		FileStore fs  = FacilioFactory.getFileStore();
		if(fs.deleteFile(fileId)) {
			System.out.println("File " + fileId + " has been deleted");
		}
		return SUCCESS;
	}
	
	public String updateImportProcessContext() throws Exception {
		ImportAPI.updateImportProcess(importProcessContext);
		setResult(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT ,importProcessContext);
		
		return SUCCESS;
		
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
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		importProcessContext.setModuleId(bean.getModule(moduleName).getModuleId());
		
		ImportAPI.importPasteParsedData(parsedData,columnMaping,importProcessContext);
		
		return SUCCESS;
	}
	
	public String beginImportValidation() throws Exception{
		
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioContext context = new FacilioContext();
		context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, this.importProcessContext);
		FacilioTimer.scheduleInstantJob("ImportReadingLogJob", context);
		return SUCCESS;
	}
	
	public String processImport() throws Exception
	{	
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioContext context = new FacilioContext();
		context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
		FacilioTimer.scheduleInstantJob("GenericImportDataLogJob", context);
		
		return SUCCESS;
	}
	
	public String fetchAllUnvalidated() throws Exception{
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportProcessFields())
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.andCustomWhere("STATUS", ImportProcessContext.ImportStatus.RESOLVE_VALIDATION.getValue());		
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
		
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.VALIDATION_COMPLETE.getValue());
		importProcessContext = ImportAPI.updateTotalRows(importProcessContext);
		ImportAPI.updateImportProcess(importProcessContext);
		return SUCCESS;
	}
	
	
	public String fetchImportHistory() throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule importModule = modBean.getModule(ModuleFactory.getImportProcessModule().getName());
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.select(FieldFactory.getImportProcessFields())
				.orderBy("IMPORT_TIME desc")
				.limit(10);
		
		List<Map<String, Object>> temp = selectRecordBuilder.get();
		importHistory = FieldUtil.getAsBeanListFromMapList(temp, ImportProcessContext.class);
		return SUCCESS;
	}
	
	public String fetchImportHistoryList() throws Exception{
		
		FacilioChain chain = ReadOnlyChainFactory.getImportHistoryListChain();

		chain.getContext().put(FacilioConstants.ContextNames.COUNT, count);
		chain.getContext().put(FacilioConstants.ContextNames.IMPORT_MODE, importMode);
		chain.getContext().put(ImportAPI.ImportProcessConstants.CHOOSEN_MODULE, moduleName);
		
		chain.execute();
		List<ImportProcessContext> historyDetailsList;
		historyDetailsList = (List<ImportProcessContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
		setResult(FacilioConstants.ContextNames.IMPORT_PROCESS_CONTEXT_LIST, historyDetailsList);

		return SUCCESS;
	}
	
	public String fetchDataForValidation() throws Exception{
		String errorConditions = ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getStringValue() + "," + ImportProcessContext.ImportLogErrorStatus.FOUND_IN_DB.getStringValue();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportProcessLogFields())
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IMPORTID","importId", importProcessContext.getId().toString(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ERROR_RESOLVED", "error_resolved", errorConditions, NumberOperators.EQUALS));
		
		records = selectRecordBuilder.get();
		if (importProcessContext.importMode == ImportProcessContext.ImportMode.NORMAL.getValue()) {
			if (importProcessContext.getModule().getParentModule() != null && (importProcessContext.getModule().getParentModule().getName().equals("resource")
					|| importProcessContext.getModule().getParentModule().getName().equals("asset"))
			) {
				unvalidatedRecords = ImportAPI.setAssetName(importProcessContext, importProcessContext.getModule().getParentModule(), FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class));
			} else {
				unvalidatedRecords = ImportAPI.setAssetName(importProcessContext, importProcessContext.getModule(), FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class));
			}
		}
		else {
			unvalidatedRecords = FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class);
		}
		return SUCCESS;
	}
	
	
	public String importReading() throws Exception{
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importProcessContext.getId(), "importReading", 10, "priority");
		return SUCCESS;
	}
	
	
	public String importData() throws Exception{
		if(assetId > 0) {
			importProcessContext.setAssetId(assetId);
			
			JSONObject scheduleInfo = new JSONObject();
			scheduleInfo.put("assetId", assetId);
			
			importProcessContext.setImportJobMeta(scheduleInfo.toJSONString());
		}
				
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importProcessContext.getId(), "importData" , 10, "priority");
		
		return SUCCESS;
	}
	
	public String checkImportStatus() throws Exception {
		ImportProcessContext im = ImportAPI.getImportProcessContext(importProcessContext.getId());
		Integer mail = importProcessContext.getMailSetting();
		Integer status = im.getStatus();
		JSONObject meta = im.getImportJobMetaJson();
		Integer setting = importProcessContext.getImportSetting();
		
		if(status == 8 && (mail == null || mail < 0)) {
			if(setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
				if(meta.get("Inserted") != null) {
					String inserted = meta.get("Inserted").toString();
					im.setnewEntries(Integer.parseInt(inserted));
				}
				if(importProcessContext.getImportMode() == 2 && meta.get("Skipped") != null) {
					String Skipped = meta.get("Skipped").toString();
					im.setSkipEntries(Integer.parseInt(Skipped));
				}
			}
			else if(setting == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
				String skipped = meta.get("Skipped").toString();
				String inserted = meta.get("Inserted").toString();
				im.setSkipEntries(Integer.parseInt(skipped));
				im.setnewEntries(Integer.parseInt(inserted));
			}
			else if(setting == ImportProcessContext.ImportSetting.UPDATE.getValue()) {
				String updated = meta.get("Updated").toString();
				im.setupdateEntries(Integer.parseInt(updated));
			}
			else if(setting == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
				String updated = meta.get("Updated").toString();
				im.setupdateEntries(Integer.parseInt(updated));
			}
			
			else if(setting == ImportProcessContext.ImportSetting.BOTH.getValue()) {
				String inserted = meta.get("Inserted").toString();
				String updated = meta.get("Updated").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				im.setupdateEntries(Integer.parseInt(updated));
			}
			else if(setting == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
				String inserted = meta.get("Inserted").toString();
				String updated = meta.get("Updated").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				im.setupdateEntries(Integer.parseInt(updated));				
			}
		} else if (status == 7 && (mail != null && mail > 0)) {
			ImportAPI.updateImportProcess(getImportProcessContext());
		}
		importProcessContext = im;
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

	private List<ImportProcessContext> importHistory = new ArrayList<ImportProcessContext>();
	
	public List<ImportProcessContext> getImportHistory() {
		return importHistory;
	}
	public void setImportHistory(List<ImportProcessContext> importHistory) {
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
	public ImportProcessContext getImportProcessContext() {
		return importProcessContext;
	}
	public void setImportProcessContext(ImportProcessContext importProcessContext) {
		this.importProcessContext = importProcessContext;
	}

	ImportProcessContext importProcessContext =new ImportProcessContext();
	
	Long assetCategory;
	
	public Long getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(Long assetCategory) {
		this.assetCategory = assetCategory;
	}
	public void setModuleName(String module)
	{
		this.moduleName=module;
	}
	
	public String getModuleName()
	{
		return this.moduleName;
	}

	public void setAssetId(long id) {
		this.assetId = id;
	}
	private long assetId;
	private String moduleName;
	private Long fileId;
	private long orgId;
	private JSONObject moduleExists;
	private long templateId;
	private long siteId;
	
	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public JSONObject getModuleExists() {
		return moduleExists;
	}
	public void setModuleExists(JSONObject moduleExisting) {
		this.moduleExists = moduleExisting;
	}
	
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

	private int count;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	private String moduleMeta;
	public String getModuleMeta() {
		return moduleMeta;
	}
	public void setModuleMeta(String moduleMeta) {
		this.moduleMeta = moduleMeta;
	}

	
}