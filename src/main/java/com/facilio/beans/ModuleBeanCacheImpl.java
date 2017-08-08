package com.facilio.beans;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
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
			
			LOGGER.log(Level.INFO, "getModule result from DB for module: "+moduleName);
		}
		else {
			LOGGER.log(Level.INFO, "getModule result from CACHE for module: "+moduleName);
		}
		return moduleObj;
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
	public long addField(FacilioField field) throws Exception {
		
		long fieldId = super.addField(field);
		
		if (fieldId > 0) {
			// clearing primary field and all fields of this module from cache
			CacheUtil.delete(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), field.getModuleName()), CacheUtil.FIELDS_KEY(getOrgId(), field.getModuleName()));
		}
		return fieldId;
	}
}