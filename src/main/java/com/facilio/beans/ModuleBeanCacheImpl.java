package com.facilio.beans;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Cache Logic:
 * 
 *  Read -> First get objects from cache if it's not exists in cache then get it from DB and set it in the cache and return the same.
 *  
 *  Update, Delete -> Do call the necessary DB methods and then clear it from cache
 *
 */
public class ModuleBeanCacheImpl extends ModuleBeanImpl implements ModuleBean {
	
	private static final Logger LOGGER = LogManager.getLogger(ModuleBeanCacheImpl.class.getName());
	
	@Override
	public FacilioModule getModule(String moduleName) throws Exception {
		
LRUCache modulecache = LRUCache.getModuleCache();
		// FacilioModule moduleObj = (FacilioModule) CacheUtil.get(CacheUtil.MODULE_KEY(getOrgId(), moduleName));
		FacilioModule moduleObj = (FacilioModule) modulecache.get(CacheUtil.MODULE_KEY(getOrgId(), moduleName));
		if (moduleObj == null) {
			
			moduleObj = super.getModule(moduleName);
			
		//	CacheUtil.set(CacheUtil.MODULE_KEY(getOrgId(), moduleName), moduleObj);
			modulecache.put(CacheUtil.MODULE_KEY(getOrgId(), moduleName), moduleObj);
			
			//LOGGER.log(Level.INFO, "getModule result from DB for module: "+moduleName);
		}
		else {
			//LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleName);
		}
		return moduleObj;
	}

	@Override
	public FacilioModule getModule(long moduleId) throws Exception {
		LRUCache modulecache = LRUCache.getModuleCache();

		FacilioModule moduleObj = (FacilioModule) modulecache.get(CacheUtil.MODULE_KEY(getOrgId(), moduleId));
		
		if (moduleObj == null) {
			
			moduleObj = super.getModule(moduleId);
			
			modulecache.put(CacheUtil.MODULE_KEY(getOrgId(), moduleId), moduleObj);
			
			LOGGER.debug("getModule result from DB for module: "+moduleId);
		}
		else {
			//LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleId);
		}
		return moduleObj;
	}
	
	@Override
	public int updateModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		int row =  super.updateModule(module);
		if (row > 0) {
			FacilioModule newModule = super.getModule(module.getModuleId());
			CacheUtil.delete(CacheUtil.MODULE_KEY(getOrgId(), newModule.getModuleId()));
			CacheUtil.delete(CacheUtil.MODULE_KEY(getOrgId(), newModule.getName()));
		}
		return row;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception {
		
		List<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName));
		
		if (modules == null) {
			
			modules = super.getAllSubModules(moduleName);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName), new ArrayList<>(modules));
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			LOGGER.debug("getSubModules result from CACHE for module: "+moduleName);
			modules = Collections.unmodifiableList(modules);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(long moduleId) throws Exception {
		
		List<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId));
		
		if (modules == null) {
			
			modules = super.getAllSubModules(moduleId);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId), new ArrayList<>(modules));
			
			LOGGER.debug("getSubModules result from DB for module: "+moduleId);
		}
		else {
			modules = Collections.unmodifiableList(modules);
			//LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleId);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(String moduleName, ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		List<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, types));
		
		if (modules == null) {
			
			modules = super.getSubModules(moduleName, types);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, types), new ArrayList<>(modules));
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			modules = Collections.unmodifiableList(modules);
			//LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleName);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(long moduleId, ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		List<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, types));
		
		if (modules == null) {
			
			modules = super.getSubModules(moduleId, types);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, types), new ArrayList<>(modules));
			
			LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleId);
		}
		else {
			modules = Collections.unmodifiableList(modules);
			LOGGER.debug("getSubModules result from CACHE for module: "+moduleId);
		}
		return modules;
	}
	
	@Override
	public FacilioField getPrimaryField(String moduleName) throws Exception {
		
		FacilioField fieldObj = (FacilioField) CacheUtil.get(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName));
		
		if (fieldObj == null) {
			
			fieldObj = super.getPrimaryField(moduleName);
			
			CacheUtil.set(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName), fieldObj);
			
			//LOGGER.log(Level.INFO, "getPrimaryField result from DB for module: "+moduleName);
		}
		else {
			//LOGGER.log(Level.INFO, "getPrimaryField result from CACHE for module: "+moduleName);
		}
		return fieldObj;
	}

	@Override
	public List<FacilioField> getAllFields(String moduleName) throws Exception {
		
		System.currentTimeMillis();
		LRUCache cache = 	LRUCache.getModuleFieldsCache();
		
		Object key = CacheUtil.FIELDS_KEY(getOrgId(), moduleName);
		
		List<FacilioField> fields = (List<FacilioField>)cache.get(key);
		if (fields == null) {
			
			fields = super.getAllFields(moduleName);
			
//			CacheUtil.set(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), new ArrayList<>(fields));
			cache.put(key, fields);
			
			//LOGGER.log(Level.INFO, "getAllFields result from DB for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		else {
//			fields = Collections.unmodifiableList(fields);
			//LOGGER.log(Level.INFO, "getAllFields result from CACHE for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		return fields;
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		try {
		LRUCache cache = 	LRUCache.getFieldsCache();

		String key = CacheUtil.FIELD_KEY(getOrgId(), fieldId);
		FacilioField field = (FacilioField)cache.get(key);
		
		
		if (field == null) {
			
			field = super.getField(fieldId);
			
			cache.put(key, field);
			
		}
		else {
		}
		return field;
		}
		catch (Exception e) {
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
	}
	
	@Override
	public FacilioField getFieldFromDB(long fieldId) throws Exception {
		
		return super.getField(fieldId);
	}
	
	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {
		
		FacilioField field = super.getField(fieldName, moduleName);
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
	
	@Override
	public int updateField(FacilioField field) throws Exception {
		// TODO Auto-generated method stub
		int rows = super.updateField(field);
		if (rows > 0) {
			FacilioField newField = super.getField(field.getId());
			dropFieldFromCache(newField);
		
		 // CacheUtil.remove(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName));
		}
		return rows;
	}
	
	private void dropFieldFromCache(FacilioField newField )
	{
		LRUCache cache = 	LRUCache.getModuleFieldsCache();
		cache.remove(CacheUtil.FIELDS_KEY(getOrgId(), newField.getModule().getName()));
		
		cache = LRUCache.getFieldsCache();
		cache.remove(CacheUtil.FIELD_KEY(getOrgId(), newField.getFieldId()));
	}
	@Override
	public int deleteField(long fieldId) throws Exception {
		FacilioField f = getField(fieldId);
		dropFieldFromCache(f);
		return super.deleteField(fieldId);
	}
	
	public int deleteFields(List<Long> fieldIds) throws Exception {
		long fieldId = 0;
		for (long prop : fieldIds) {
			fieldId = prop;
		}
		FacilioField f = getField(fieldId);
		dropFieldFromCache(f);
		return super.deleteFields(fieldIds);
	}

	
	
	
}