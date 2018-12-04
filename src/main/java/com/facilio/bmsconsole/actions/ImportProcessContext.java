package com.facilio.bmsconsole.actions;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fw.BeanFactory;

public class ImportProcessContext implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ImportProcessContext.class.getName());
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
		if(moduleId != null ) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(moduleId);
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
			return ImportAPI.getFields(getModule().getName(), getImportMode());
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
				List<FacilioField> fieldsList= bean.getAllFields(getModule().getName());

				for(FacilioField field : fieldsList)
				{
					if(!ImportAPI.isRemovableFieldOnImport(field.getName()))
					{
						facilioFieldMapping.put(field.getName(), field);
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
		IN_PROGRESS,
		IMPORTED,
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
}