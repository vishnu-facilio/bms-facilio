package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ModuleLocalIdContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ModuleLocalIdUtil {

	public static final List<String> modulesWithLocalId = new ArrayList<>();
	
	static {
		modulesWithLocalId.add(FacilioConstants.ContextNames.ASSET);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ENERGY_METER);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WORK_ORDER);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TASK);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ALARM);
		modulesWithLocalId.add(FacilioConstants.ContextNames.READING_ALARM);
	}
	
	public static ModuleLocalIdContext getModuleLocalContext(Long orgId,String moduleName) throws Exception {
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.table(ModuleFactory.getModuleLocalIdModule().getTableName());
		selectRecordBuilder.select(FieldFactory.getModuleLocalIdFields());
		
		selectRecordBuilder.andCondition(CriteriaAPI.getOrgIdCondition(orgId, ModuleFactory.getModuleLocalIdModule()));
		selectRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS));
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			ModuleLocalIdContext moduleLocalIdContext = FieldUtil.getAsBeanFromMap(props.get(0), ModuleLocalIdContext.class);
			return moduleLocalIdContext;
		}
		return null;
	}
	public static ModuleLocalIdContext getModuleLocalContext(String moduleName) throws Exception {
		return getModuleLocalContext(AccountUtil.getCurrentOrg().getId(),moduleName);
	}
	
	public static Long getModuleLocalId(String moduleName) throws Exception {
		return getModuleLocalId(AccountUtil.getCurrentOrg().getId(),moduleName);
	}
	public static Long getModuleLocalId(Long orgId,String moduleName) throws Exception {
		ModuleLocalIdContext moduleLocalIdContext = getModuleLocalContext(orgId,moduleName);
		if(moduleLocalIdContext != null) {
			return moduleLocalIdContext.getLocalId();
		}
		return null;
	}
	
	public static boolean updateModuleLocalId(String moduleName,Long lastLocalId) throws Exception {
		
		return updateModuleLocalId(AccountUtil.getCurrentOrg().getId(),moduleName,lastLocalId);
	}
	
	public static boolean updateModuleLocalId(Long orgId, String moduleName,Long lastLocalId) throws Exception {
		
		ModuleLocalIdContext moduleLocalIdContext = new ModuleLocalIdContext();
		moduleLocalIdContext.setOrgId(orgId);
		moduleLocalIdContext.setModuleName(moduleName);
		moduleLocalIdContext.setLocalId(lastLocalId);
		
		return updateModuleLocalId(moduleLocalIdContext);
	}
	
	public static boolean updateModuleLocalId(ModuleLocalIdContext moduleLocalIdContext) throws Exception {
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
		updateRecordBuilder.table(ModuleFactory.getModuleLocalIdModule().getTableName());
		updateRecordBuilder.fields(FieldFactory.getModuleLocalIdFields());
		
		updateRecordBuilder.andCondition(CriteriaAPI.getOrgIdCondition(moduleLocalIdContext.getOrgId(), ModuleFactory.getModuleLocalIdModule()));
		updateRecordBuilder.andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleLocalIdContext.getModuleName(), StringOperators.IS));
		
		Map<String, Object> props = FieldUtil.getAsProperties(moduleLocalIdContext);
		return updateRecordBuilder.update(props) > 0 ? true : false;
		
	}
	
	public static boolean isModuleWithLocalId(String moduleName) {
		
		if(AccountUtil.getCurrentOrg().getId() == 92l) {
			modulesWithLocalId.add("kdm");
		}
		return modulesWithLocalId.contains(moduleName) ? true : false;
	}
}
