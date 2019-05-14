package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ImportDataAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ImportDataAction.class.getName());
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportDataAction.class.getName());
	public String upload() throws Exception {
		LOGGER.severe("UPLOAD_STARTED");
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
		LOGGER.severe("FILE_UPLOADED -- "+fileId);
		importProcessContext = ImportAPI.getColumnHeadings(fileUpload, importProcessContext);
		
		JSONObject firstRow = ImportAPI.getFirstRow(fileUpload);	
		LOGGER.severe("SECOND POINT-- ");
		importProcessContext.setfirstRow(firstRow);
		
		importProcessContext.setImportMode(getImportMode());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = modBean.getModule(getModuleName());
        
        LOGGER.severe("THIRD POINT-- ");
		if(facilioModule ==  null) {
			return ERROR;
		}
        if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) && (assetCategory != -1)) {
        	Map<String,String> moduleInfo = AssetsAPI.getAssetModuleName(this.assetCategory);
        	String moduleName = moduleInfo.get(FacilioConstants.ContextNames.MODULE_NAME);
        	if(!moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
        		facilioModule = modBean.getModule(moduleName);
        		importProcessContext.setModuleId(facilioModule.getModuleId());
        	}
        	else {
            	importProcessContext.setModuleId(facilioModule.getModuleId());
            }
            
        }
        else {
        	importProcessContext.setModuleId(facilioModule.getModuleId());
        }
        importProcessContext.setStatus(ImportProcessContext.ImportStatus.UPLOAD_COMPLETE.getValue());
        
        importProcessContext.setFileId(fileId);
        
        importProcessContext.setImportTime(DateTimeUtil.getCurrenTime());
        
        importProcessContext.setImportType(ImportProcessContext.ImportType.EXCEL.getValue());
        
        if(assetId > 0) {
        	importProcessContext.setAssetId(assetId);
        }
        
        ImportAPI.addImportProcess(importProcessContext);
		
        if(getModuleName().equals("purchasedTool") || getModuleName().equals("purchasedItem")) {
        	importProcessContext.setFacilioFieldMapping(ImportFieldFactory.getFacilioFieldMapping(getModuleName()));
        	importProcessContext.setFieldMapping(ImportFieldFactory.getFieldMapping(getModuleName()));

        }
        else {
        	ImportAPI.getFieldMapping(importProcessContext);
        }
        
        
        LOGGER.severe("LAST POINT-- ");
		return SUCCESS;
	}
	public String displayColumnFieldMapping()
	{
		return SUCCESS;
	}
	
	public String deleteFile() throws Exception{
		FileStore fs  = FileStoreFactory.getInstance().getFileStore();
		if(fs.deleteFile(fileId)) {
			System.out.println("File " + fileId + " has been deleted");
		}
		return SUCCESS;
	}
	public String getAssets() throws Exception
	{
		if(moduleName != null) {
		switch(moduleName)
		{
		  
		case "Energy" : 
			this.energyMeters=DeviceAPI.getAllEnergyMeters();
		
		}
		try {
			this.chillerAssets = AssetsAPI.getAssetListOfCategory(AssetsAPI.getCategory("Chiller").getId());
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		}
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
		FacilioTimer.scheduleInstantJob("ImportLogJob", context);
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
		ImportAPI.updateImportProcess(importProcessContext);
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
		importHistory = FieldUtil.getAsBeanListFromMapList(temp, ImportProcessContext.class);
		return SUCCESS;
	}
	
	public String fetchDataForValidation() throws Exception{
		List<FacilioField> fields = FieldFactory.getImportProcessLogFields();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportProcessLogFields())
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IMPORTID","importId", importProcessContext.getId().toString(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ERROR_RESOLVED", "error_resolved", ImportProcessContext.ImportLogErrorStatus.UNRESOLVED.getStringValue(), NumberOperators.EQUALS));
		
		records = selectRecordBuilder.get();
		if(importProcessContext.importMode == ImportProcessContext.ImportMode.NORMAL.getValue()) {
			if(importProcessContext.getModule().getParentModule().getName().equals("resource") 
				|| importProcessContext.getModule().getParentModule().getName().equals("asset") 
					) {
				unvalidatedRecords = ImportAPI.setAssetName(importProcessContext, importProcessContext.getModule().getParentModule(), FieldUtil.getAsBeanListFromMapList(records, ImportProcessLogContext.class));
			}
			else {
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
		FacilioTimer.scheduleOneTimeJob(importProcessContext.getId(), "importReading", 5, "priority");
		return SUCCESS;
	}
	public String processImport() throws Exception
	{	
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.PARSING_IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioContext context = new FacilioContext();
		context.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
		FacilioTimer.scheduleInstantJob("ImportDataLogJob", context);
		
		return SUCCESS;
	}
	
	public String importData() throws Exception{
		if(assetId > 0) {
			importProcessContext.setAssetId(assetId);
			
			JSONObject scheduleInfo = new JSONObject();
			scheduleInfo.put("assetId", assetId);
			
			importProcessContext.setImportJobMeta(scheduleInfo.toJSONString());
		}
		
//		if (importProcessContext.getModule().getName().equals("space") || importProcessContext.getModule().getName().equals("Space"))
//		{
//			ProcessSpaceXLS.processImport(importProcessContext);
//			ImportAPI.updateImportProcess(getImportProcessContext());
//		} 
		
		
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioTimer.scheduleOneTimeJob(importProcessContext.getId(), "importData" , 5, "priority", 1);
		
		return SUCCESS;
	}
	
	public String checkImportStatus() throws Exception {
		ImportProcessContext im = ImportAPI.getImportProcessContext(importProcessContext.getId());
		Integer mail = importProcessContext.getMailSetting();
		Integer status = im.getStatus();
		JSONObject meta = im.getImportJobMetaJson();
		Integer setting = importProcessContext.getImportSetting();
		
		if(status == 8 && mail ==null) {
			if(setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
				String inserted = meta.get("Inserted").toString();
				im.setnewEntries(Integer.parseInt(inserted));
				if(importProcessContext.getImportMode() == 2) {
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
		}
		else if(status == 7 && mail!=null) {
			ImportAPI.updateImportProcess(getImportProcessContext());
		}
		importProcessContext = im;
		return SUCCESS;
	}
	
	
	
	public void getSites() throws Exception
	{
		List<SiteContext> sites = SpaceAPI.getAllSites();
		JSONObject result = new JSONObject();
		for(SiteContext site:sites) {
			long siteId=site.getId();
			String name=site.getName();
			result.put(siteId,name);
		}
		LOGGER.severe("The list of Sites available is "+result);
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
		LOGGER.severe("Setting module : " + module);
		this.moduleName=module;
	}
	
	public String getModuleName()
	{
		return this.moduleName;
	}
	
	private List<EnergyMeterContext> energyMeters;
	public List<EnergyMeterContext> getEnergyMeters() {
		return energyMeters;
	}
	public void setAssetId(long id) {
		this.assetId = id;
	}
	private long assetId;
	private String moduleName;
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

	private List<AssetContext> chillerAssets;
	public List<AssetContext> getChillerAssets() {
		return chillerAssets;
	}
	
}