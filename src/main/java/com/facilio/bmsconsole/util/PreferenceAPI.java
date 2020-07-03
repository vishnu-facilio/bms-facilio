package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

public class PreferenceAPI {

	public static PreferenceMetaContext getEnabledPreference(Long id) throws Exception{
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		List<FacilioField> fields = FieldFactory.getPreferencesMetaFields();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getIdCondition(id, module))
													;
		List<Map<String, Object>> prefs = builder.get();
		if(CollectionUtils.isNotEmpty(prefs)) {
			return FieldUtil.getAsBeanFromMap(prefs.get(0), PreferenceMetaContext.class);
		}
		return null;
	}
	
	public static int deleteEnabledPreference(long id) throws Exception {
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		return builder.delete();
		
		
	}
	public static List<PreferenceMetaContext> getEnabledPreference(Long recordId, String moduleName) throws Exception{
		if(recordId == null || recordId <= 0) {
			throw new IllegalArgumentException("Invalid record id");
		}
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		List<FacilioField> fields = FieldFactory.getPreferencesMetaFields();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule prefModule = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(prefModule.getModuleId()), NumberOperators.EQUALS))
													
													;
		List<Map<String, Object>> prefs = builder.get();
		if(CollectionUtils.isNotEmpty(prefs)) {
			return FieldUtil.getAsBeanListFromMapList(prefs, PreferenceMetaContext.class);
		}
		return null;
	}
	
	public static List<PreferenceMetaContext> getEnabledPreference(String moduleName) throws Exception{
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		List<FacilioField> fields = FieldFactory.getPreferencesMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule prefModule = modBean.getModule(moduleName);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(prefModule.getModuleId()), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("recordId"), CommonOperators.IS_EMPTY))
													;
		List<Map<String, Object>> prefs = builder.get();
		if(CollectionUtils.isNotEmpty(prefs)) {
			return FieldUtil.getAsBeanListFromMapList(prefs, PreferenceMetaContext.class);
		}
		return null;
	}
	
	public static PreferenceMetaContext checkForEnabledPreference(long moduleId, String prefName, long recordId) throws Exception{
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		List<FacilioField> fields = FieldFactory.getPreferencesMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("PREFERENCE_NAME", "name", prefName, StringOperators.IS))
													;
		if(moduleId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));
		}
		if(recordId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS));
			
		}
		List<Map<String, Object>> prefs = builder.get();
		if(CollectionUtils.isNotEmpty(prefs)) {
			return FieldUtil.getAsBeanFromMap(prefs.get(0), PreferenceMetaContext.class);
		}
		return null;
	}
	
	public static void updatePref(Map<String, Object> updateProps, List<Long> updateIds) throws SQLException {
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getPreferencesMetaFields())
													.andCondition(CriteriaAPI.getIdCondition(updateIds, module));
		updateBuilder.update(updateProps);
	
	}

	public static JSONObject getEnabledOrgPreferences() throws Exception{
		//for now only tax application pref -> 1- line item level, 2-> transaction level
		FacilioModule module = ModuleFactory.getPreferenceMetaModule();
		List<FacilioField> fields = FieldFactory.getPreferencesMetaFields();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", "1", CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", "1", CommonOperators.IS_EMPTY))
				;

		List<Map<String, Object>> prefs = builder.get();
		JSONObject result = new JSONObject();
		if (CollectionUtils.isNotEmpty(prefs)) {
			for (Map<String, Object> pref : prefs) {
				result.put(pref.get("preferenceName"), pref);
			}
			return result;
		}

		return null;
	}

	public static JSONObject getAllOrgPreferences() throws Exception{
		//for now only tax application pref -> 1- line item level, 2-> transaction level
		Map<String, Preference> prefs = PreferenceFactory.getAllPreferencesForOrg();
		if(MapUtils.isNotEmpty(prefs)) {
			JSONObject result = new JSONObject();
			result.putAll(prefs);
			return result;
		}
		return null;
	}


}
