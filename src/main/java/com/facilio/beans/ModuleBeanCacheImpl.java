package com.facilio.beans;

import java.util.*;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FieldUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;

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
		
		FacilioCache modulecache = LRUCache.getModuleCache();
		FacilioModule moduleObj = (FacilioModule) modulecache.get(CacheUtil.MODULE_KEY(getOrgId(), moduleName));
		if (moduleObj == null) {
			moduleObj = super.getModule(moduleName);
			modulecache.put(CacheUtil.MODULE_KEY(getOrgId(), moduleName), moduleObj);
			LOGGER.debug("getModule result from DB for module: " + String.valueOf(moduleName));
		}
		return handleInvalidModuleNull(moduleObj);
	}

	@Override
	public FacilioModule getModule(long moduleId) throws Exception {

		FacilioCache modulecache = LRUCache.getModuleCache();
		FacilioModule moduleObj = (FacilioModule) modulecache.get(CacheUtil.MODULE_KEY(getOrgId(), moduleId));
		if (moduleObj == null) {
			moduleObj = super.getModule(moduleId);
			modulecache.put(CacheUtil.MODULE_KEY(getOrgId(), moduleId), moduleObj);
			LOGGER.debug("getModule result from DB for module: " + moduleId);
		}
		return handleInvalidModuleNull(moduleObj);
	}

	private FacilioModule handleInvalidModuleNull (FacilioModule module) {
		if (module != null && RESERVED_NULL_MODULE_NAME.equals(module.getName())) {
			return null;
		}
		return module;
	}

	private void clearModuleCache (long moduleId) throws Exception {
		FacilioModule newModule = super.getModule(moduleId);
		FacilioCache moduleCache = LRUCache.getModuleCache();
		moduleCache.remove(CacheUtil.MODULE_KEY(getOrgId(), newModule.getModuleId()));
		moduleCache.remove(CacheUtil.MODULE_KEY(getOrgId(), newModule.getName()));
	}

	@Override
	public long addModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		long moduleId =  super.addModule(module);
		if (moduleId > 0) {
			clearModuleCache(moduleId);
		}
		return moduleId;
	}
	
	@Override
	public int updateModule(FacilioModule module) throws Exception {
		// TODO Auto-generated method stub
		int row =  super.updateModule(module);
		if (row > 0) {
			clearModuleCache(module.getModuleId());
		}
		return row;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception {
		
		List<FacilioModule> modules = (ArrayList<FacilioModule>) CacheUtil.get(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName));
		
		if (modules == null) {
			modules = super.getAllSubModules(moduleName);
			CacheUtil.set(CacheUtil.SUB_MODULE_KEY(getOrgId(), moduleName), new ArrayList<>(modules));
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
			
			LOGGER.debug("getSubModules result from DB for module: "+moduleId);
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
		FacilioModule module = getModule(moduleName);
		
		List<FacilioField> allFields = new ArrayList<>();
		while (module != null) {
			List<FacilioField> fields = getModuleFields(module.getName());
			allFields.addAll(fields);
			module = module.getExtendModule();
		}
		
		return allFields;
	}

	@Override
	public List<FacilioField> getModuleFields(String moduleName) throws Exception {
		FacilioCache cache = 	LRUCache.getModuleFieldsCache();
		Object key = CacheUtil.FIELDS_KEY(getOrgId(), moduleName);
		List<FacilioField> fields = (List<FacilioField>)cache.get(key);
		if (fields == null) {
			
			fields = super.getModuleFields(moduleName);
			
//			CacheUtil.set(CacheUtil.FIELDS_KEY(getOrgId(), moduleName), new ArrayList<>(fields));
			cache.put(key, fields);
			
			//LOGGER.log(Level.INFO, "getAllFields result from DB for module: "+moduleName +"\n Time taken"+ (System.currentTimeMillis()-begintime));
		}
		return fields;
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		FacilioCache cache = LRUCache.getFieldsCache();
		String key = CacheUtil.FIELD_KEY(getOrgId(), fieldId);
		FacilioField field = (FacilioField)cache.get(key);
		if (field == null) {
			field = super.getField(fieldId);
			if (field != null) {
				cache.put(key, field);
			}
		}
		return field;
	}
	
	@Override
	public List<FacilioField> getFields(Collection<Long> fieldIds) throws Exception {
		List<FacilioField> fields = null;
		FacilioCache cache = LRUCache.getFieldsCache();
		if (CollectionUtils.isNotEmpty(fieldIds)) {
			List<Long> fieldIdsToFetch = new ArrayList<>();
			fields = new ArrayList<>();
			for(long fieldId: fieldIds) {
				String key = CacheUtil.FIELD_KEY(getOrgId(), fieldId);
				if (!cache.contains(key)) {
					fieldIdsToFetch.add(fieldId);
				}
				else {
					FacilioField field = (FacilioField)cache.get(key);
					fields.add(field);
				}
			}
			if (!fieldIdsToFetch.isEmpty()) {
				List<FacilioField> fieldsFromDb = super.getFields(fieldIdsToFetch);
				if (fieldsFromDb != null) {
					fields.addAll(fieldsFromDb);
					for(FacilioField field: fieldsFromDb) {
						String key = CacheUtil.FIELD_KEY(getOrgId(), field.getFieldId());
						cache.put(key, field);
					}
				}
			}
		}
		return fields;
	}
	
	@Override
	public FacilioField getFieldFromDB(long fieldId) throws Exception {
		return super.getField(fieldId);
	}
	
	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {
		FacilioCache cache = LRUCache.getFieldNameCache();
		String key = CacheUtil.FIELD_NAME_KEY(getOrgId(), fieldName, moduleName);
		FacilioField field = (FacilioField)cache.get(key);
		if (field == null) {
			field = super.getField(fieldName, moduleName);
			if (field != null) {
				cache.put(key, field);
			}
		}
		return field;
	}
	
	@Override
	public long addField(FacilioField field) throws Exception {
		
		long fieldId = super.addField(field);
		
		if (fieldId > 0) {
			// clearing primary field and all fields of this module from cache
		//	CacheUtil.delete(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), field.getModule().getName()), CacheUtil.FIELDS_KEY(getOrgId(), field.getModule().getName()));
		
			FacilioCache cache = LRUCache.getModuleFieldsCache();
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
			FieldUtil.dropFieldFromCache(getOrgId(), newField);

		 // CacheUtil.remove(CacheUtil.PRIMARY_FIELD_KEY(getOrgId(), moduleName));
		}
		return rows;
	}

	@Override
	public int deleteField(long fieldId) throws Exception {
		FacilioField f = getField(fieldId);
		FieldUtil.dropFieldFromCache(getOrgId(), f);
		return super.deleteField(fieldId);
	}
	
	public int deleteFields(List<Long> fieldIds) throws Exception {
		long fieldId = 0;
		for (long prop : fieldIds) {
			fieldId = prop;
		}
		FacilioField f = getField(fieldId);
		FieldUtil.dropFieldFromCache(getOrgId(), f);
		return super.deleteFields(fieldIds);
	}

	@Override
	public List<Long> getPermissibleFieldIds(FacilioModule module, int permissionType) throws Exception {
		FacilioCache cache = LRUCache.getModuleFieldsCache();
		Object key = CacheUtil.PERMISSIBLE_FIELDS_KEY(getOrgId(), module.getName(), permissionType, AccountUtil.getCurrentUser().getRoleId());
		List<Long> permissibleFieldIds = (List<Long>) cache.get(key);
		if (CollectionUtils.isEmpty(permissibleFieldIds)) {
			permissibleFieldIds = super.getPermissibleFieldIds(module, permissionType);
			if (CollectionUtils.isNotEmpty(permissibleFieldIds)) {
				cache.put(key, permissibleFieldIds);
			}
		}
		return permissibleFieldIds;
	}

	@Override
	public List<FacilioModule> getPermissibleSubModules(long moduleId, int permissionType) throws Exception {
		FacilioCache cache = LRUCache.getModuleFieldsCache();
		Object key = CacheUtil.PERMISSIBLE_SUB_MODULES_KEY(getOrgId(), moduleId, permissionType, AccountUtil.getCurrentUser().getRoleId());
		List<FacilioModule> permissibleSubModules = (List<FacilioModule>) cache.get(key);
		if (CollectionUtils.isEmpty(permissibleSubModules)) {
			permissibleSubModules = super.getPermissibleSubModules(moduleId, permissionType);
			if (CollectionUtils.isNotEmpty(permissibleSubModules)) {
				cache.put(key, permissibleSubModules);
			}
		}
		return permissibleSubModules;

	}

}