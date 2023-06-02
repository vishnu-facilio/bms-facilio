package com.facilio.bmsconsole.actions;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ImportFieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.*;

public class ImportProcessContext implements Serializable,Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportProcessContext.class.getName());

	Long id,orgId,moduleId,fileId;
	Integer status,importType;
	Long importTime;
	String columnHeadingString,filePath,filePathFailed,fieldMappingString,importJobMeta;
	Integer newEntries =0;
	Integer updatedEntries =0; 
	Integer skippedEntries =0;  //A:Prashanth
	JSONObject firstRow = new JSONObject(); //A:Prashanth
	Integer mailSetting;
	Integer importSetting;
	Integer importMode;
	Long templateId;
	String fileName;
	Long totalRows;
	String firstRowString;
	String moduleName;
	private long siteId = -1;
	
	private long uploadedBy;
	
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	
	public String getFirstRowString() {
		return firstRowString;
	}
	public void setFirstRowString(String firstRowString) {
		this.firstRowString = firstRowString;
	}
	
	public Long getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(Long totalRows) {
		this.totalRows = totalRows;
	}
	public String getFileName() throws Exception{
		if(fileId != null) {
			FileStore fs = FacilioFactory.getFileStore();
			String name = fs.getFileInfo(fileId).getFileName();
			setFileName(name);
		}
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getImportMode() {
		return this.importMode;
	}
	@Override
	public String toString() {
		return "ImportProcessContext [id=" + id + ", orgId=" + orgId + ", moduleId=" + moduleId + ", fileId=" + fileId
				+ ", status=" + status + ", importType=" + importType + ", importTime=" + importTime
				+ ", columnHeadingString=" + columnHeadingString + ", filePath=" + filePath + ", filePathFailed="
				+ filePathFailed + ", fieldMappingString=" + fieldMappingString + ", importJobMeta=" + importJobMeta
				+ ", newEntries=" + newEntries + ", updatedEntries=" + updatedEntries + ", skippedEntries="
				+ skippedEntries + ", firstRow=" + firstRow + ", mailSetting=" + mailSetting + ", importSetting="
				+ importSetting + ", importMode=" + importMode + ", templateId=" + templateId + ", assetId=" + assetId
				+ ", facilioFieldMapping=" + facilioFieldMapping + ", fieldMapping=" + fieldMapping
				+ ", fieldMappingJSON=" + fieldMappingJSON + "]";
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public void setImportMode(Integer value) {
		this.importMode = value;
	}
	public Integer getImportSetting() {
		return this.importSetting;
	}
	public void setImportSetting(Integer setting) {
		this.importSetting = setting;
	}
	
	public void setSkipEntries(int count) {
		this.skippedEntries = count;
	}
	
	public JSONObject getOptions() throws Exception{
		if(getModule() != null) {
			return ImportFieldFactory.getImportOptions(getModule());
		}
		return null;
	}
	public void setMailSetting(Integer mail) {
		this.mailSetting = mail;
	}
	
	public Integer getMailSetting() {
		return this.mailSetting;
	}
	
	public int getSkipEntries() {
		return this.skippedEntries;
	}
	
	public void setupdateEntries(int count) {
		this.updatedEntries = count;
	}
	public int getUpdateEntries() {
		return this.updatedEntries;
	}
	public void setfirstRow(JSONObject firstRow) {
		this.firstRow = firstRow;
	}
	
	public JSONObject getfirstRow() {
		return this.firstRow;
	}
	
	public Long getId() {
		return id;
	}

	public String getImportJobMeta() {
		return importJobMeta;
	}

	public void setImportJobMeta(String importJobMeta) {
   		this.importJobMeta = importJobMeta;
	}
	
	public JSONObject getImportJobMetaJson() throws ParseException {
		if(importJobMeta != null) {
			JSONParser parser = new JSONParser();
			JSONObject metaJson = (JSONObject) parser.parse(importJobMeta);
			return metaJson;
		}
		return null;
	}

	public void setFieldMappingJSON(JSONObject fieldMappingJSON) {
		this.fieldMappingJSON = fieldMappingJSON;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getModuleId() {
		return moduleId;
	}
	
	public FacilioModule getModule() throws Exception {
		if(moduleId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(moduleId);
		}
		else if (moduleName != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(moduleName);
		}
		return null;
	}
	
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Integer getStatus() {
		return status;
	}
	
	public void setnewEntries(Integer en)
	{
		this.newEntries = en;
	}
	
	public Integer getEntries() {
		return this.newEntries;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getImportType() {
		return importType;
	}

	public void setImportType(Integer importType) {
		this.importType = importType;
	}

	public Long getImportTime() {
		return importTime;
	}

	public void setImportTime(Long importTime) {
		this.importTime = importTime;
	}

	public String getColumnHeadingString() {
		return columnHeadingString;
	}

	public void setColumnHeadingString(String columnHeadingString) {
		this.columnHeadingString = columnHeadingString;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePathFailed() {
		return filePathFailed;
	}

	public void setFilePathFailed(String filePathFailed) {
		this.filePathFailed = filePathFailed;
	}

	public String getFieldMappingString() {
		return fieldMappingString;
	}

	public void setFieldMappingString(String fieldMappingString) {
		this.fieldMappingString = fieldMappingString;
	}

	
	long assetId=0;
	public void setAssetId(long id)
	{
		this.assetId=id;
	}
	
	public long getAssetId()
	{
		return assetId;
	}
	
	public  JSONArray getColumnHeadings() throws Exception
	{
		if(getColumnHeadingString() != null) {
			JSONParser parser = new JSONParser();
			return (JSONArray) parser.parse(getColumnHeadingString());
		}
		return null;
	}
	
	public JSONArray getFields() throws Exception
	{
		if(getModule() != null) {
			return ImportAPI.getFields(getModule().getName(), getImportMode(), getImportSetting());
		}
		return null;
	}
	
	public JSONArray getIgnoreFields() throws Exception
	{
		if(getModule() != null) {
			return ImportAPI.getIgnoreFields(getModule().getName(), getImportMode());
		}
		return null;
	}
	
	private Map<String, FacilioField> facilioFieldMapping = null;
	
	public void setFacilioFieldMapping(Map<String, FacilioField> facilioFieldMapping) {
		this.facilioFieldMapping = facilioFieldMapping;
	}
	
	public Map<String, FacilioField> getFacilioFieldMapping() throws Exception {
		if(facilioFieldMapping == null && getModule() != null) {
			try {
				
				facilioFieldMapping = new HashMap<String, FacilioField>();
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				String moduleName = getModule().getName();
				List<FacilioField> fieldsList= bean.getAllFields(moduleName);
				List<String> removeFields = ImportFieldFactory.getFieldsTobeRemoved(getModule().getName());
				if (moduleName != FacilioConstants.ContextNames.TENANT_UNIT_SPACE) {
					fieldsList.addAll(FieldFactory.getImportFieldMappingDisplayFields(getModule()));
				}
				if(moduleName.equals(FacilioConstants.PM_V2.PM_V2_PLANNER)){
					fieldsList.addAll(ImportFieldFactory.getPmPlannerImportField());
				}
				for(FacilioField field : fieldsList)
				{
					if(!removeFields.contains(field.getName()))
					{
						if (field.getDisplayType() == FacilioField.FieldDisplayType.ADDRESS) {
							facilioFieldMapping.putAll(ImportAPI.getLocationFields((LookupField) field));
						}
						else {
							facilioFieldMapping.put(field.getName(), field);
						}
						
					}
				}

				return facilioFieldMapping;
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
			}
		}
		return facilioFieldMapping;
	}
	
	private HashMap<String, String> fieldMapping = new LinkedHashMap <String, String>();
	
	public void populateFieldMapping() throws Exception
	{
		for(Object field: getFields())
		{
			this.fieldMapping.put((String)field,"-1");
		}
	}
	
	public HashMap<String, String> getFieldMapping() throws Exception {
		
		JSONObject fieldJson = getFieldMappingJSON();
		if(fieldJson != null && !fieldJson.isEmpty()) {
			
			for(Object key:fieldJson.keySet()) {
				
				if(fieldMapping == null) {
					fieldMapping = new HashMap<>(); 
				}
				
				fieldMapping.put((String)key, (String)fieldJson.get(key));
			}
		}
		return fieldMapping;
	}
	public void setFieldMapping(HashMap<String, String> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
	
	public void setFieldMapping(String key,String Value) {
		this.fieldMapping.put(key, Value);
	}
	
	public String getFieldValue(String key) {

		return fieldMapping.get(key);
	}
	JSONObject fieldMappingJSON;
	public JSONObject getFieldMappingJSON() throws ParseException {
		
		if((fieldMappingJSON == null || fieldMappingJSON.isEmpty()) && fieldMappingString != null) {
			JSONParser parser = new JSONParser();
			fieldMappingJSON =  (JSONObject) parser.parse(fieldMappingString);
		}
		return fieldMappingJSON;
		
	}
	public long getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	public enum ImportSetting {
		INSERT,
		INSERT_SKIP,
		UPDATE,
		UPDATE_NOT_NULL,
		BOTH,
		BOTH_NOT_NULL;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public ImportSetting getImportSetting(int value) {
			return IMPORT_SETTING.get(value);
		}
		
		private static final Map<Integer, ImportSetting> IMPORT_SETTING = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportSetting> initTypeMap() {
			Map<Integer, ImportSetting> typeMap = new HashMap<>();
			for(ImportSetting type : ImportSetting.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	
	public enum ImportMode {
		NORMAL,
		READING;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public ImportMode getImportMode(int value) {
			return IMPORT_MODE_MAP.get(value);
		}
		
		private static final Map<Integer, ImportMode> IMPORT_MODE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportMode> initTypeMap() {
			Map<Integer, ImportMode> typeMap = new HashMap<>();
			for(ImportMode type : ImportMode.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	public enum ImportStatus {
		UPLOAD_COMPLETE,		
		PARSING_IN_PROGRESS,	//2
		PARSING_FAILED,
		BEGIN_VALIDATION,		//4
		RESOLVE_VALIDATION,
		VALIDATION_COMPLETE,	//6
		IN_PROGRESS,			
		IMPORTED,				//8
		FAILED;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public ImportStatus getImportStatus(int value) {
			return IMPORT_PROCESS_STATUS_MAP.get(value);
		}
		
		private static final Map<Integer, ImportStatus> IMPORT_PROCESS_STATUS_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportStatus> initTypeMap() {
			Map<Integer, ImportStatus> typeMap = new HashMap<>();
			for(ImportStatus type : ImportStatus.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	
	public enum ImportLogErrorStatus {
		NO_VALIDATION_REQUIRED,
		UNRESOLVED,
		FOUND_IN_DB,
		RESOLVED,
		OTHER_ERRORS;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public String getStringValue() {
			Integer i = new Integer(ordinal() + 1);
			return i.toString();
		}
		
		public ImportLogErrorStatus getImportLogErrorStatus(int value) {
			return IMPORT_LOG_ERROR_STATUS.get(value);
		}
		
		private static final Map<Integer, ImportLogErrorStatus> IMPORT_LOG_ERROR_STATUS = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportLogErrorStatus> initTypeMap() {
			Map<Integer, ImportLogErrorStatus> typeMap = new HashMap<>();
			for(ImportLogErrorStatus type : ImportLogErrorStatus.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	
	public enum ImportRowErrorCode {
		NO_ERRORS,
		NULL_RESOURCES,
		NULL_UNIQUE_FIELDS;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public String getStringValue() {
			Integer i = new Integer(ordinal() + 1);
			return i.toString();
		}
		
		public ImportRowErrorCode getImportRowErrorCode(int value) {
			return IMPORT_ROW_ERROR_CODE.get(value);
		}
		
		private static final Map<Integer, ImportRowErrorCode> IMPORT_ROW_ERROR_CODE = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportRowErrorCode> initTypeMap() {
			Map<Integer, ImportRowErrorCode> typeMap = new HashMap<>();
			for(ImportRowErrorCode type : ImportRowErrorCode.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	
	public enum ImportType {
		
		EXCEL;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public ImportType getImportType(int value) {
			return IMPORT_TYPE_MAP.get(value);
		}
		
		private static final Map<Integer, ImportType> IMPORT_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ImportType> initTypeMap() {
			Map<Integer, ImportType> typeMap = new HashMap<>();
			for(ImportType type : ImportType.values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
	
	public Object clone() throws CloneNotSupportedException { 
		return super.clone(); 
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}