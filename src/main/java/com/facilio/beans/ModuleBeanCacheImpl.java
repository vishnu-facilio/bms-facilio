package com.facilio.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.LRUCache;
import org.apache.log4j.LogManager;

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
	private static org.apache.log4j.Logger log = LogManager.getLogger(ModuleBeanCacheImpl.class.getName());
	
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
			LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleName);
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
			
			LOGGER.log(Level.INFO, "getModule result from DB for module: "+moduleId);
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
			//LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleId);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(String moduleName, ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		String key = StringUtils.join(types);
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, types));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleName, types);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, types), modules);
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			//LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleName);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(long moduleId, ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, types));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleId, types);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, types), modules);
			
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
			//LOGGER.log(Level.INFO, "getPrimaryField result from CACHE for module: "+moduleName);
		}
		return fieldObj;
	}

	@Override
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception {
		
		//ArrayList<FacilioField> fields = (ArrayList<FacilioField>) CacheUtil.get(CacheUtil.FIELDS_KEY(getOrgId(), moduleName));
		long begintime = System.currentTimeMillis();
		LRUCache cache = 	LRUCache.getModuleFieldsCache();
		
		Object key = CacheUtil.FIELDS_KEY(getOrgId(), moduleName);
		
		ArrayList<FacilioField> fields = (ArrayList<FacilioField>)cache.get(key);
		if (fields == null) {
			
			fields = super.getAllFields(moduleName);
			
			CacheUtil.set(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), fields);
			cache.put(key, fields);
			
			//LOGGER.log(Level.INFO, "getAllFields result from DB for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		else {
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
			
			//System.out.println( "getField result from DB for Id: "+fieldId);
		}
		else {
			//System.out.println("getField result from CACHE for Id: "+fieldId);
		}
		return field;
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
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
	
	
	
}