package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScheduledRuleJobsMetaContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ScheduledRuleJobsMetaUtil {
	
	public static boolean checkNewOrOldScheduleRuleExecution() throws Exception{
		
		Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_NEW_SCHEDULE_RULE_EXECUTION);
    	if (orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
    		String isNewScheduleRuleExecutionProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.IS_NEW_SCHEDULE_RULE_EXECUTION);
			if (isNewScheduleRuleExecutionProp != null && !isNewScheduleRuleExecutionProp.isEmpty() && StringUtils.isNotEmpty(isNewScheduleRuleExecutionProp) && Boolean.valueOf(isNewScheduleRuleExecutionProp)) {
				return true;
			}
    	}
		return false;
	}
	
	public static List<ScheduledRuleJobsMetaContext> fetchScheduledRuleJobsMetaFromRuleId(long ruleId) throws Exception{
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledRuleJobsMetaFields())
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS));
					
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			List<ScheduledRuleJobsMetaContext> scheduledRuleJobsMetaContextList = FieldUtil.getAsBeanListFromMapList(props, ScheduledRuleJobsMetaContext.class);
			if(scheduledRuleJobsMetaContextList != null && scheduledRuleJobsMetaContextList.isEmpty()) {
				return scheduledRuleJobsMetaContextList;	
			}
		}	
		return null;
	}
	
	public static void disableScheduledRuleJobsMetaFromRuleId(long ruleId) throws Exception{
		
		FacilioModule module = ModuleFactory.getScheduledRuleJobsMetaModule();
		List<FacilioField> fields = FieldFactory.getScheduledRuleJobsMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		FacilioField isActiveField = fieldMap.get("isActive");
		updatedfields.add(isActiveField);
		
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("isActive", false);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.fields(updatedfields)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(updateMap);
		updateBuilder.update(props);
	}
	
	public static void updateScheduledRuleJobsMeta(ScheduledRuleJobsMetaContext scheduledRuleJobsMetaContext) throws Exception{
	
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getScheduledRuleJobsMetaModule().getTableName())
				.fields(FieldFactory.getScheduledRuleJobsMetaFields())
				.andCondition(CriteriaAPI.getIdCondition(scheduledRuleJobsMetaContext.getId(), ModuleFactory.getScheduledRuleJobsMetaModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(scheduledRuleJobsMetaContext);
		updateBuilder.update(props);
	}

}
