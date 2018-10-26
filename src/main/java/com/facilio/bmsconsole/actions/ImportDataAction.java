package com.facilio.bmsconsole.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ImportDataAction.class.getName());
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportDataAction.class.getName());
	public String upload() throws Exception {
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
		
		JSONArray columnheadings = ImportAPI.getColumnHeadings(fileUpload);
		
		JSONObject firstRow = ImportAPI.getFirstRow(fileUpload);	
		importProcessContext.setfirstRow(firstRow);
		importProcessContext.setColumnHeadingString(columnheadings.toJSONString().replaceAll("\"", "\\\""));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = modBean.getModule(getModuleName());
		
        if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) && (assetCategory != -1)) {
        	HashMap<String,String> moduleInfo = AssetsAPI.getAssetModuleName(this.assetCategory);
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
		
        ImportAPI.getFieldMapping(importProcessContext);
		return SUCCESS;
	}
	public String displayColumnFieldMapping()
	{
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
	
	public String processImport() throws Exception
	{	
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
		FacilioTimer.scheduleOneTimeJob(importProcessContext.getId(), "importData" , 5, "priority");
		
		return SUCCESS;
	}
	
	public String checkImportStatus() throws Exception {
		ImportProcessContext im = ImportAPI.getImportProcessContext(importProcessContext.getId());
		Integer mail = importProcessContext.getMailSetting();
		Integer status = im.getStatus();
		JSONObject meta = im.getImportJobMetaJson();
		Integer setting = importProcessContext.getImportSetting();
		
		if(status == 3 && mail ==null) {
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
		else if(status ==2 && mail!=null) {
			ImportAPI.updateImportProcess(getImportProcessContext());
		}
		importProcessContext = im;
		return SUCCESS;
	}
	
	public String importReading() throws Exception{
		
		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
		ImportAPI.updateImportProcess(importProcessContext);
		FacilioTimer.scheduleOneTimeJob(importProcessContext.getId(), "importReading", 5, "priority");
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


	private String fileUploadContentType;
	private String fileUploadFileName;
	public long getImportprocessid() {
		return importprocessid;
	}
	public void setImportprocessid(long importprocessid) {
		LOGGER.severe("setting the id "+importprocessid);
		this.importprocessid = importprocessid;
	}

	private long importprocessid=0;

	private static String UPDATEQUERY = "update  ImportProcess set FIELD_MAPPING='#FIELD_MAPPING#' where IMPORTID_ID=#IMPORTID#";	


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
	
	
	private List<AssetContext> chillerAssets;
	public List<AssetContext> getChillerAssets() {
		return chillerAssets;
	}
	
}