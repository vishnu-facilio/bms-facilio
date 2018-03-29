package com.facilio.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;

/**
 *  Cache Logic:
 * 
 *  Read -> First get objects from cache if it's not exists in cache then get it from DB and set it in the cache and return the same.
 *  
 *  Update, Delete -> Do call the necessary DB methods and then clear it from cache
 *
 */
public class ModuleBeanCacheImpl extends ModuleBeanImpl implements ModuleBean {
	
	private static final Logger LOGGER = Logger.getLogger(ModuleBeanCacheImpl.class.getName());
	
	@Override
	public FacilioModule getModule(String moduleName) throws Exception {
		
		FacilioModule moduleObj = (FacilioModule) CacheUtil.get(CacheUtil.MODULE_KEY(getOrgId(), moduleName));
		
		if (moduleObj == null) {
			
			moduleObj = super.getModule(moduleName);
			
			CacheUtil.set(CacheUtil.MODULE_KEY(getOrgId(), moduleName), moduleObj);
			
			//LOGGER.log(Level.INFO, "getModule result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleName);
		}
		return moduleObj;
	}

	@Override
	public FacilioModule getModule(long moduleId) throws Exception {
		
		FacilioModule moduleObj = (FacilioModule) CacheUtil.get(CacheUtil.MODULE_KEY(getOrgId(), moduleId));
		
		if (moduleObj == null) {
			
			moduleObj = super.getModule(moduleId);
			
			CacheUtil.set(CacheUtil.MODULE_KEY(getOrgId(), moduleId), moduleObj);
			
			//LOGGER.log(Level.INFO, "getModule result from DB for module: "+moduleId);
		}
		else {
			LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleId);
		}
		return moduleObj;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getAllSubModules(moduleName);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName), modules);
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleName);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(long moduleId) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getAllSubModules(moduleId);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId), modules);
			
			LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleId);
		}
		else {
			LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleId);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(String moduleName, ModuleType type) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, type.getValue()));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleName, type);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, type.getValue()), modules);
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleName);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(long moduleId, ModuleType type) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, type.getValue()));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleId, type);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, type.getValue()), modules);
			
			LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleId);
		}
		else {
			LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleId);
		}
		return modules;
	}
	
	@Override
	public FacilioField getPrimaryField(String moduleName) throws Exception {
		
		FacilioField fieldObj = (FacilioField) CacheUtil.get(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName));
		
		if (fieldObj == null) {
			
			fieldObj = super.getPrimaryField(moduleName);
			
			CacheUtil.set(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName), fieldObj);
			
			LOGGER.log(Level.INFO, "getPrimaryField result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getPrimaryField result from CACHE for module: "+moduleName);
		}
		return fieldObj;
	}

	@Override
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception {
		
		//ArrayList<FacilioField> fields = (ArrayList<FacilioField>) CacheUtil.get(CacheUtil.FIELDS_KEY(getOrgId(), moduleName));
		long begintime = System.currentTimeMillis();
		LRUCache cache = 	LRUCache.getModuleFieldsCache();
		ArrayList<FacilioField> fields = (ArrayList<FacilioField>)cache.get(CacheUtil.FIELDS_KEY(getOrgId(), moduleName));
		if (fields == null) {
			
			fields = super.getAllFields(moduleName);
			
			CacheUtil.set(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), fields);
			cache.put(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), fields);
			
			LOGGER.log(Level.INFO, "getAllFields result from DB for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		else {
			LOGGER.log(Level.INFO, "getAllFields result from CACHE for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		return fields;
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		
		LRUCache cache = 	LRUCache.getFieldsCache();

		String key = CacheUtil.FIELD_KEY(getOrgId(), fieldId);
		FacilioField field = (FacilioField)cache.get(key);
		
		//FacilioField field = (FacilioField) CacheUtil.get(CacheUtil.FIELD_KEY(getOrgId(), fieldId));
		
		if (field == null) {
			
			field = super.getField(fieldId);
			
			//CacheUtil.set(CacheUtil.FIELD_KEY(getOrgId(), fieldId), field);
			cache.put(key, field);
			
			LOGGER.log(Level.INFO, "getField result from DB for Id: "+fieldId);
		}
		else {
			LOGGER.log(Level.INFO, "getField result from CACHE for Id: "+fieldId);
		}
		return field;
	}
	
	@Override
	public FacilioField getFieldFromDB(long fieldId) throws Exception {
		
		return super.getField(fieldId);
	}
	
	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {

		LRUCache cache = 	LRUCache.getFieldsCache();
		String key = CacheUtil.FIELD_KEY(getOrgId(), fieldName,moduleName);
		FacilioField field = (FacilioField)cache.get(key);

		if (field == null) {
			field = super.getField(fieldName,moduleName);
			cache.put(key, field);
			LOGGER.log(Level.INFO, "getField result from DB for Id: "+fieldName+" , "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getField result from CACHE for Id: "+fieldName+" , "+moduleName);
		}
		return field;
	}
	
	@Override
	public long addField(FacilioField field) throws Exception {
		
		long fieldId = super.addField(field);
		
		if (fieldId > 0) {
			// clearing primary field and all fields of this module from cache
		//	CacheUtil.delete(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), field.getModule().getName()), CacheUtil.FIELDS_KEY(getOrgId(), field.getModule().getName()));
		
			LRUCache cache = 	LRUCache.getModuleFieldsCache();
			cache.remove(CacheUtil.FIELDS_KEY(getOrgId(), field.getModule().getName()));
			//cache.remove(CacheUtil.FIELD_KEY(getOrgId(), field.getFieldId()));

		}
		return fieldId;
	}
}