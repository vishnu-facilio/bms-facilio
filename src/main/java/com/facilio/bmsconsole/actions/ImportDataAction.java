package com.facilio.bmsconsole.actions;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.data.ProcessSpaceXLS;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class ImportDataAction extends ActionSupport {
	
	public String upload() throws Exception {
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		
		long fileId = fs.addFile(fileUploadFileName, fileUpload, fileUploadContentType);
		
		JSONArray columnheadings = ImportAPI.getColumnHeadings(fileUpload);
		
		importProcessContext.setColumnHeadingString(columnheadings.toJSONString().replaceAll("\"", "\\\""));
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = modBean.getModule(getModuleName());
		
        importProcessContext.setModuleId(facilioModule.getModuleId());
        importProcessContext.setStatus(ImportProcessContext.ImportStatus.FILE_UPLOADED.getValue());
        
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
		switch(moduleName)
		{
		  
		case "Energy" : 
			this.energyMeters=DeviceAPI.getAllEnergyMeters();
		
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
		
		ImportAPI.updateImportProcess(getImportProcessContext());
		
		importProcessContext = ImportAPI.getImportProcessContext(getImportProcessContext().getId());
		
		if(assetId > 0) {
			importProcessContext.setAssetId(assetId);
		}
		
		if (importProcessContext.getModule().getName().equals("space") || importProcessContext.getModule().getName().equals("Space"))
		{
			ProcessSpaceXLS.processImport(importProcessContext);
		}
		else
		{
			ProcessXLS.processImport(importProcessContext);
		}
		ImportAPI.updateImportProcess(getImportProcessContext(),ImportStatus.IMPORTED);
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
		System.out.println("The list of Sites available is "+result);
	}
	public String showformupload()
	{
		System.out.println("Displaying formupload");
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
		System.out.println("setting the id "+importprocessid);
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
	
	
	public void setModuleName(String module)
	{
		System.out.println("Setting module : " + module);
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
	private String moduleName="Energy";
}
