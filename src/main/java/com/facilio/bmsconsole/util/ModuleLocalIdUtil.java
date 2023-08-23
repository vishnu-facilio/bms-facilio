package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
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
		modulesWithLocalId.add(FacilioConstants.ContextNames.SERVICE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WARRANTY_CONTRACTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS_LINE_ITEMS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.VISITOR);
		modulesWithLocalId.add(FacilioConstants.ContextNames.VISITOR_INVITE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.VISITOR_LOGGING);
		modulesWithLocalId.add(FacilioConstants.ContextNames.CONTACT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.INSURANCE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.SHIPMENT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.WATCHLIST);
		modulesWithLocalId.add(FacilioConstants.ContextNames.SAFETY_PLAN);
		modulesWithLocalId.add(FacilioConstants.ContextNames.HAZARD);
		modulesWithLocalId.add(FacilioConstants.ContextNames.PEOPLE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.TENANT_CONTACT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.VENDOR_CONTACT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.CLIENT_CONTACT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.EMPLOYEE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.QUOTE);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);
		modulesWithLocalId.add(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION);
		modulesWithLocalId.add(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
		modulesWithLocalId.add(FacilioConstants.Meter.METER);


		return modulesWithLocalId;
	}
	
	private static long getModuleLocalId(String moduleName, Connection conn) throws Exception {
		FacilioModule module = ModuleFactory.getModuleLocalIdModule();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.useExternalConnection(conn) //This connection will not be closed by builder. Use this with caution
																.table(module.getTableName())
																.select(FieldFactory.getModuleLocalIdFields())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
			conn = FacilioConnectionPool.getInstance().getDirectConnection();//Getting connection directly from pool because this should be done outside transaction. Should be used with caution
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
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(moduleField, moduleName, StringOperators.IS))
//																.andCondition(CriteriaAPI.getCondition(localIdField, String.valueOf(oldId), NumberOperators.EQUALS))
																;
		
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("localId", lastLocalId);
		return updateRecordBuilder.update(prop);
		
	}
	
	public static boolean isModuleWithLocalId(FacilioModule module) throws Exception {
		
		if(AccountUtil.getCurrentOrg().getId() == 92l && module.getName().equals("kdm")) {
			return true;
		}
		if (module.isCustom()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField localIdField = modBean.getField("localId", module.getName());
			if (localIdField != null) {
				return true;
			}
		}

		if (MODULES_WITH_LOCAL_ID.contains(module.getName())) {
			return true;
		} else if (module.getExtendModule() != null)  {
			return isModuleWithLocalId(module.getExtendModule());
		}
		return false;
	}

	public static FacilioField getLocalIdField(FacilioModule module) throws Exception {
		String moduleName = module.getName();
		ModuleBean moduleBean = Constants.getModBean();
		FacilioField localIdField = moduleBean.getField("localId", moduleName);

		if (localIdField != null) {
			return localIdField;
		} else if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			return FieldFactory.getNumberField("serialNumber", "SERIAL_NUMBER", module.getExtendModule());
		} else if (MODULES_WITH_LOCAL_ID.contains(moduleName)) {
			return FieldFactory.getNumberField("localId", "LOCAL_ID", module);
		} else if (module.getExtendModule() != null) {
			return getLocalIdField(module.getExtendModule());
		}

		return null;
	}

}
