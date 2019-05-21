package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.*;

public class ModuleLocalIdUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(ModuleLocalIdUtil.class.getName());

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
		modulesWithLocalId.add(FacilioConstants.ContextNames.INVENTORY);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TOOL_TYPES);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ITEM_TYPES);
		modulesWithLocalId.add(FacilioConstants.ContextNames.ITEM);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TOOL);
		modulesWithLocalId.add(FacilioConstants.ContextNames.LABOUR);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TENANT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.PURCHASE_ORDER);
		modulesWithLocalId.add(FacilioConstants.ContextNames.PURCHASE_REQUEST);
		modulesWithLocalId.add(FacilioConstants.ContextNames.RECEIVABLE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.RECEIPTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.PURCHASE_CONTRACTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.LABOUR_CONTRACTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.GATE_PASS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.INVENTORY_REQUESTS);


		return modulesWithLocalId;
	}
	
	private static long getModuleLocalId(String moduleName, Connection conn) throws Exception {
		FacilioModule module = ModuleFactory.getModuleLocalIdModule();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.useExternalConnection(conn) //This connection will not be closed by builder. Use this with caution
																.table(module.getTableName())
																.select(FieldFactory.getModuleLocalIdFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition("MODULE_NAME", "moduleName", moduleName, StringOperators.IS))
																.forUpdate()
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
		Connection conn = null;
		boolean olderCommit = false;
		try {
			conn = FacilioConnectionPool.getInstance().getConnectionFromPool();//Getting connection directly from pool because this should be done outside transaction. Should be used with caution
			olderCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
//			if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
				LOGGER.debug("Connection object instance while getting local id for module "+moduleName+" : "+conn);
//			}
			
			long localId = getModuleLocalId(moduleName, conn);
			updateModuleLocalId(moduleName, localId+currentSize, conn);
			conn.commit();
			
//			if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
				LOGGER.debug("Committed connection while getting local id for module "+moduleName);
//			}
			
			return localId;
		}
		catch (Exception e) {
			if (conn != null) {
				conn.rollback();
//				if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
					LOGGER.debug("Rolled back connection while getting local id for module "+moduleName);
//				}
			}
		}
		finally {
			if (conn != null) {
				conn.setAutoCommit(olderCommit);
			}
			DBUtil.close(conn);
		}
//		if (localId != -1 && updateModuleLocalId(moduleName, localId, localId+currentSize) <= 0) {
//			return getAndUpdateModuleLocalId(moduleName, currentSize);
//		}
		return -1;
	}
	
	//private static int updateModuleLocalId(String moduleName,long oldId,long lastLocalId) throws Exception {
	private static int updateModuleLocalId(String moduleName, long lastLocalId, Connection conn) throws Exception {
		FacilioModule module = ModuleFactory.getModuleLocalIdModule();
		List<FacilioField> fields = FieldFactory.getModuleLocalIdFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField moduleField = fieldMap.get("moduleName");
//		FacilioField localIdField = fieldMap.get("localId");
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.useExternalConnection(conn) //This connection will not be closed by builder. Use this with caution
																.table(module.getTableName())
																.fields(fields)
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(moduleField, moduleName, StringOperators.IS))
//																.andCondition(CriteriaAPI.getCondition(localIdField, String.valueOf(oldId), NumberOperators.EQUALS))
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
