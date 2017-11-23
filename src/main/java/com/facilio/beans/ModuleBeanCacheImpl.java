package com.facilio.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.SubModuleType;
import com.facilio.cache.CacheUtil;

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
	public List<FacilioModule> getSubModules(String moduleName, SubModuleType type) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, type.getIntVal()));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleName, type);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName, type.getIntVal()), modules);
			
			//LOGGER.log(Level.INFO, "getSubModules result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getSubModules result from CACHE for module: "+moduleName);
		}
		return modules;
	}
	
	@Override
	public List<FacilioModule> getSubModules(long moduleId, SubModuleType type) throws Exception {
		
		ArrayList<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, type.getIntVal()));
		
		if (modules == null) {
			
			modules = (ArrayList<FacilioModule>) super.getSubModules(moduleId, type);
			
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleId, type.getIntVal()), modules);
			
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
		
		ArrayList<FacilioField> fields = (ArrayList<FacilioField>) CacheUtil.get(CacheUtil.FIELDS_KEY(getOrgId(), moduleName));
		
		if (fields == null) {
			
			fields = super.getAllFields(moduleName);
			
			CacheUtil.set(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), fields);
			
			LOGGER.log(Level.INFO, "getAllFields result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getAllFields result from CACHE for module: "+moduleName);
		}
		return fields;
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		
		FacilioField field = (FacilioField) CacheUtil.get(CacheUtil.FIELD_KEY(getOrgId(), fieldId));
		
		if (field == null) {
			
			field = super.getField(fieldId);
			
			CacheUtil.set(CacheUtil.FIELD_KEY(getOrgId(), fieldId), field);
			
			LOGGER.log(Level.INFO, "getField result from DB for Id: "+fieldId);
		}
		else {
			LOGGER.log(Level.INFO, "getField result from CACHE for Id: "+fieldId);
		}
		return field;
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
			CacheUtil.delete(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), field.getModule().getName()), CacheUtil.FIELDS_KEY(getOrgId(), field.getModule().getName()));
		}
		return fieldId;
	}
}