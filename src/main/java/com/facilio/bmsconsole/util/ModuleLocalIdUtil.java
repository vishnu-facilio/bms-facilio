package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ModuleLocalIdUtil {

	private static final List<String> MODULES_WITH_LOCAL_ID = Collections.unmodifiableList(initModulesWithLocalIds());
	
	private static List<String> initModulesWithLocalIds() {
		List<String> modulesWithLocalId = new ArrayList<>();
		modulesWithLocalId.add(FacilioConstants.ContextNames.ASSET);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ENERGY_METER);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WORK_ORDER);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TASK);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ALARM);
		modulesWithLocalId.add(FacilioConstants.ContextNames.READING_ALARM);
		
		return modulesWithLocalId;
	}
	
	public static long getModuleLocalId(String moduleName) throws Exception {
		FacilioModule module = ModuleFactory.getModuleLocalIdModule();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(module.getTableName())
																.select(FieldFactory.getModuleLocalIdFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS))
																;
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if(props != null && !props.isEmpty()) {
			return (long) props.get(0).get("localId");
		}
		return -1;
	}
	
	public static long getAndUpdateModuleLocalId(String moduleName, int currentSize) throws Exception {
		if (currentSize <= 0) {
			throw new IllegalArgumentException("Invalid current id size for fetching local Id");
		}
		long localId = getModuleLocalId(moduleName);
		if (localId != -1 && updateModuleLocalId(moduleName, localId, localId+currentSize) <= 0) {
			return getAndUpdateModuleLocalId(moduleName, currentSize);
		}
		return localId;
	}
	
	public static int updateModuleLocalId(String moduleName,long oldId,long lastLocalId) throws Exception {
		FacilioModule module = ModuleFactory.getModuleLocalIdModule();
		List<FacilioField> fields = FieldFactory.getModuleLocalIdFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField moduleField = fieldMap.get("moduleName");
		FacilioField localIdField = fieldMap.get("localId");
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.table(module.getTableName())
																.fields(fields)
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(moduleField, moduleName, StringOperators.IS))
																.andCondition(CriteriaAPI.getCondition(localIdField, String.valueOf(oldId), NumberOperators.EQUALS))
																;
		
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("localId", lastLocalId);
		return updateRecordBuilder.update(prop);
		
	}
	
	public static boolean isModuleWithLocalId(String moduleName) {
		
		if(AccountUtil.getCurrentOrg().getId() == 92l && moduleName.equals("kdm")) {
			return true;
		}
		return MODULES_WITH_LOCAL_ID.contains(moduleName) ? true : false;
	}
}
