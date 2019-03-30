package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.actions.ImportProcessContext.ImportType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ImportTemplateContext{
	
	Long id,orgId,save;
	String module;
	String uniqueMappingString, fieldMappingString;
	String templateName;
	JSONObject moduleJSON = new JSONObject();
	JSONObject uniqueMappingJSON = new JSONObject();
	JSONObject fieldMappingJSON = new JSONObject();
	HashMap<String,String> uniqueMapping;
	HashMap<String,String> fieldMapping;
	HashMap<String,String> moduleMapping;
	JSONObject templateMetaJSON = new JSONObject();
	String templateMeta;

	
	public JSONObject getTemplateMetaJSON() throws ParseException{
		if(templateMeta != null) {
			JSONParser jsonParser = new JSONParser();
			templateMetaJSON = (JSONObject) jsonParser.parse(templateMeta);
		}
		return templateMetaJSON;
	}
	public void setTemplateMetaJSON(JSONObject templateMetaJSON) {
		this.templateMetaJSON = templateMetaJSON;
	}
	public String getTemplateMeta() {
		return templateMeta;
	}
	public void setTemplateMeta(String templateMeta) {
		this.templateMeta = templateMeta;
	}
	public Long getSave() {
		return save;
	}
	public void setSave(Long save) {
		this.save = save;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public JSONObject getModuleJSON() throws Exception {
		if(module != null) {
			JSONParser parser = new JSONParser();
			moduleJSON = (JSONObject) parser.parse(module);
		};
		return moduleJSON;
	}
	public void setModuleJSON(JSONObject moduleJSON) {
		this.moduleJSON = moduleJSON;
	}
	public HashMap<String, String> getModuleMapping() throws Exception {
		if(!getModuleJSON().isEmpty()) {
			moduleMapping = new HashMap<>();
			for(Object key : getModuleJSON().keySet()) {
				moduleMapping.put((String) key, getModuleJSON().get(key).toString());
			}
		}
		return moduleMapping;
	}
	public void setModuleMapping(HashMap<String, String> moduleMapping) {
		this.moduleMapping = moduleMapping;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTemplateName() {
		return templateName;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public String getUniqueMappingString() {
		return uniqueMappingString;
	}
	public void setUniqueMappingString(String uniqueMappingString) {
		this.uniqueMappingString = uniqueMappingString;
	}
	public String getFieldMappingString() {
		return fieldMappingString;
	}
	public void setFieldMappingString(String fieldMappingString) {
		this.fieldMappingString = fieldMappingString;
	}
	
	public JSONObject getUniqueMappingJSON() throws ParseException {
		if(uniqueMappingString != null) {
			JSONParser parser = new JSONParser();
			uniqueMappingJSON = (JSONObject) parser.parse(uniqueMappingString);
		}
		return uniqueMappingJSON;
	}
	
	public void setUniqueMappingJSON(JSONObject uniqueMappingJSON) {
		this.uniqueMappingJSON = uniqueMappingJSON;
	}
	
	public JSONObject getFieldMappingJSON() throws ParseException{
		if(fieldMappingString != null) {
			JSONParser parser = new JSONParser();
			fieldMappingJSON = (JSONObject) parser.parse(fieldMappingString);
		}
		return fieldMappingJSON;
	}
	public void setFieldMappingJSON(JSONObject fieldMappingJSON) {
		this.fieldMappingJSON = fieldMappingJSON;
	}
	
	public HashMap<String, String> getFieldMapping() throws Exception {
		if(!getFieldMappingJSON().isEmpty()) {
			fieldMapping = new HashMap<>();
			for(Object key : fieldMappingJSON.keySet()) {
				fieldMapping.put((String) key, (String) fieldMappingJSON.get(key));
			}
		}
		return fieldMapping;
	}
	
	public void setFieldMapping(HashMap<String, String> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
	
	public HashMap<String, String> getUniqueMapping() throws Exception {
		if(!getUniqueMappingJSON().isEmpty()) {	
		uniqueMapping = new HashMap<>();
			for(Object key : getUniqueMappingJSON().keySet()) {
				uniqueMapping.put((String) key, (String) uniqueMappingJSON.get(key));
			}
		}
		return uniqueMapping;
	}
	
	public void setUniqueMapping(HashMap<String, String> uniqueMapping) {
		this.uniqueMapping = uniqueMapping;
	}
	
	public enum ReadingSetting {
		
		SINGLE,
		MULTIPLE;
		
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