package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class PreferenceRuleUtil {

	
	public static void addPreferenceRule(long moduleId, long recordId, long ruleId, String prefName) throws Exception {
		List<Map<String, Object>> list = getPreferenceRule(moduleId, prefName, recordId);
		if(CollectionUtils.isNotEmpty(list)) {
			  Map<String, Object> updateMap = new HashMap<String, Object>();
			  updateMap.put("ruleId", ruleId);
			  updatePrefRuleRel(updateMap, Collections.singletonList((Long)list.get(0).get("id")));
			  WorkflowRuleAPI.deleteWorkflowRule((Long)list.get(0).get("ruleId"));
		}
		else {
			Map<String, Object> prop = new HashMap<String, Object>();
			prop.put("moduleId", moduleId);
			prop.put("recordId", recordId);
			prop.put("ruleId", ruleId);
			prop.put("prefName", prefName);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getPreferenceRuleModule().getTableName())
					.fields(FieldFactory.getPreferencesRuleFields())
					.addRecord(prop);
			insertBuilder.save();
		}
	}
	
	public static List<Map<String, Object>> getPreferenceRule(long moduleId, String prefName,long recordId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getPreferenceRuleModule().getTableName())
				.select(FieldFactory.getPreferencesRuleFields())
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PREFERENCE_NAME", "prefName", prefName, StringOperators.IS))
				;
				
	  if(recordId > 0) {
		  builder.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", String.valueOf(recordId), NumberOperators.EQUALS));
	  }
	  else {
		  builder.andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", "", CommonOperators.IS_EMPTY));
	  }
	  List<Map<String, Object>> list = builder.get();
	  return list;
	
	}
	public static void disablePreferenceRule(long moduleId, long recordId, String prefName) throws Exception {
	  List<Map<String, Object>> list = getPreferenceRule(moduleId, prefName, recordId);
		if(CollectionUtils.isNotEmpty(list)) {
		  Map<String, Object> map = list.get(0);
		  deletePreferenceRuleRel((Long)map.get("id"));
		  WorkflowRuleAPI.deleteWorkflowRule((Long)map.get("ruleId"));
	  }
	  
	}
	
	public static int deletePreferenceRuleRel(long id) throws Exception {
		FacilioModule module = ModuleFactory.getPreferenceRuleModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		return builder.delete();
		
		
	}
	
	public static void updatePrefRuleRel(Map<String, Object> updateProps, List<Long> updateIds) throws SQLException {
		FacilioModule module = ModuleFactory.getPreferenceRuleModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getPreferencesRuleFields())
													.andCondition(CriteriaAPI.getIdCondition(updateIds, module));
		updateBuilder.update(updateProps);
	
	}

}
