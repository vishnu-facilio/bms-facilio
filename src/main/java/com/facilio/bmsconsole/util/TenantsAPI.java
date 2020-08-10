package com.facilio.bmsconsole.util;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.tenant.*;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class TenantsAPI {
	
	private static final Logger LOGGER = Logger.getLogger(TenantsAPI.class.getName());
	
	public static final String TENANT_CONTEXT = "tenantContext";
	public static final String RATECARD_CONTEXT = "rateCardContext";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String UTILITY_SUM_VALUE = "utilitySumValue";
	public static final String UTILITY_VALUES = "utilityValues";
	public static final String FORMULA_SUM_VALUE = "formulaSumValue";
	public static final String FORMULA_VALUES = "formulaValues";
	public static final String TAX_VALUE = "taxValue";
	public static final String FINAL_VALUES = "finalValue";
	
	
	public static List<TenantContext> getAllTenants(JSONObject pagination) throws Exception {
		FacilioModule module = ModuleFactory.getTenantsModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsFields())
														.table(module.getTableName());
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> tenantFields = new ArrayList(modBean.getAllFields(FacilioConstants.ContextNames.TENANT));
		Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(tenantFields);
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			if (perPage != -1) {
				int offset = ((page-1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				selectBuilder.offset(offset);
				selectBuilder.limit(perPage);
			}
		}
		return getTenantsFromProps(selectBuilder.get());
	}
	
	
	
	
	
	public static List<TenantUserContext> getAllTenantsUsers(long id) throws Exception {
		
		if (id<=0 ) {
			return null;
		}
		getTenant(id);
		FacilioModule module = ModuleFactory.getTenantsuserModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsUserFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("Tenant_Users.TENANTID = ? ", id);
		List<Map<String, Object>> props = selectBuilder.get();
		List<TenantUserContext> tenantUsers = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				TenantUserContext tenantUser = FieldUtil.getAsBeanFromMap(prop, TenantUserContext.class);
				User user = AccountUtil.getUserBean().getUser(tenantUser.getOuid(), true);
				tenantUser.setOrgUser(user);
				tenantUsers.add(tenantUser);
			}
		}
		return (tenantUsers);
	}
	
	public static Map<String, Double> getTenantMeterReadings(List<String> meterId) throws Exception {
		
		Map<String, Double> newDatas = new HashMap<String, Double>();
		for (String meter : meterId) {
		DateRange daterange = DateOperators.CURRENT_MONTH.getRange(null);
		long startTime = daterange.getStartTime();
		long endTime = daterange.getEndTime();
		Double meterData = DashboardAction.getTotalKwh(Collections.singletonList(meter) ,  startTime,  endTime);
		newDatas.put(meter, meterData);
		}
		return newDatas;
	}
	
	public static long showInPortalAccess(long tenantId, Long tenantMeterId, boolean isShow_In_Portal) throws SQLException {
		
		FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getTenantsUtilityMappingFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Tenants_Utility_Mapping.ASSET_ID = ?", tenantMeterId)
				.andCustomWhere("Tenants_Utility_Mapping.TENANT_ID = ? ", tenantId);
		
		
		Map<String , Object> value = new HashMap<>();
		value.put("showInPortal", isShow_In_Portal);
		updateBuilder.update(value);
		
		return  tenantMeterId;
		
	}
	
	
	public static List<ResourceContext> getUsersTenantId(long userId, long orgId) throws Exception {

		FacilioModule module = ModuleFactory.getTenantsuserModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsUserFields())
														.table(module.getTableName())
														.andCustomWhere("Tenant_Users.ORG_USERID = ?", userId)
														.andCustomWhere("Tenant_Users.ORGID = ? ", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ResourceContext> longArray = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
		Map<String, Object> values = props.get(0);
		long value = (long) values.get("tenantId");
		
		
		FacilioModule modulo = ModuleFactory.getTenantsUtilityMappingModule();
		GenericSelectRecordBuilder selectBuilde = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsUtilityMappingFields())
														.table(modulo.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(modulo))
														.andCustomWhere("Tenants_Utility_Mapping.TENANT_ID = ?", value)
														.andCustomWhere("Tenants_Utility_Mapping.SHOW_IN_PORTAL = ?", true);
		List<Map<String, Object>> prop = selectBuilde.get();
		
		
		long Id;
		List<Long> Ids = new ArrayList<Long>();

		for (Map<String, Object> pro : prop) {
			Id =  (long) pro.get("assetId");
			Ids.add(Id);
		}
		longArray = ResourceAPI.getExtendedResources(Ids,false);
		
		}
		return longArray;

	}

	public static List<TenantContext> getTenants(List<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> tenantFields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
				.module(module)
				.beanClass(TenantContext.class)
				.select(tenantFields)
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		return builder.get();
	}
	
	public static TenantContext getTenant(long id) throws Exception {
		
		if (id <= 0) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> tenantFields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		List<LookupField> lookupFields = new ArrayList<>();
		for (FacilioField f : tenantFields) {
			if (f instanceof LookupField) {
				lookupFields.add((LookupField) f);
			}
		}
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	    SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(tenantFields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
	    if (CollectionUtils.isNotEmpty(lookupFields)) {
	    	builder.fetchSupplements(lookupFields);
	    }

		return builder.fetchFirst();
	}
	
	public static TenantContext fetchTenantForZone(long zoneId) throws Exception {
		
		if (zoneId <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("ZONE_ID", "zoneId", zoneId+"", NumberOperators.EQUALS))
														.andCustomWhere(module.getTableName()+".STATUS = ?", 1)
														
														;
		
		List<TenantContext> records = builder.get();
	//	TenantsAPI.loadTenantLookups(records);
	    if (records != null && !records.isEmpty()) {
			return records.get(0);
		}
		return null;
	}
	
	private static List<TenantContext> getTenantsFromProps(List<Map<String, Object>> props, Boolean...fetchTenantOnly) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<TenantContext> tenants = new ArrayList<>();
			List<Long> ids = new ArrayList<>();
			List<Long> zoneIds = new ArrayList<>();
			boolean fetchExtendedProps = fetchTenantOnly.length == 0 || fetchTenantOnly[0];
			for (Map<String, Object> prop : props) {
				TenantContext tenant = FieldUtil.getAsBeanFromMap(prop, TenantContext.class);
				tenants.add(tenant);
				if (!fetchExtendedProps) {
					continue;
				}
				ids.add(tenant.getId());
			//	zoneIds.add(tenant.getZoneId());
				if (tenant.getLogoId()  != -1) {
					FileStore fs = FacilioFactory.getFileStore();
					tenant.setLogoUrl(fs.getPrivateUrl(tenant.getLogoId()));
				}
			}
			if (!fetchExtendedProps) {
				return tenants;
			}
			Map<Long, List<UtilityAsset>> utilMap = getUtilityAssets(ids);
		//	Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(zoneIds);
			if (utilMap != null && !utilMap.isEmpty()) {
				for (TenantContext tenant : tenants) {
					tenant.setUtilityAssets(utilMap.get(tenant.getId()));
		//			tenant.setZoneId(zoneId);(spaceMap.get(tenant.getSpaceId()));
			//		if (tenant.getContactId() > 0) {
			//			tenant.setContactInfo(AccountUtil.getUserBean().getUser(tenant.getContactId()));
			//		}
				}
			}
			return tenants;
		}
		return null;
	}
	
	public static List<Map<String,Object>> getUtilityAssetsCount(long tenantId) throws Exception {
		
		FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
		List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantIdField = fieldMap.get("tenantId");
		Condition tenantCond = new Condition();
		tenantCond.setField(tenantIdField);
		tenantCond.setOperator(NumberOperators.EQUALS);
		tenantCond.setValue(tenantId+"");

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
												.table(module.getTableName())
												.select(fields)
//												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
												.andCondition(tenantCond)
												;
		List<Map<String,Object>> assets = selectBuilder.get();
		List<Map<String,Object>> completeAssets = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> prop : assets) {
			UtilityAsset util = FieldUtil.getAsBeanFromMap(prop, UtilityAsset.class);
			AssetContext assetInfo = AssetsAPI.getAssetInfo(util.getAssetId());
			util.setAsset(assetInfo);
			completeAssets.add(FieldUtil.getAsProperties(util));
		}
		return completeAssets;
	
	}

	
	private static Map<Long, List<UtilityAsset>> getUtilityAssets(Collection<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
		List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenantId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(tenantId, ids, PickListOperators.IS))
													;
		List<Map<String,Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			Map<Long, List<UtilityAsset>> utilityMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				UtilityAsset util = FieldUtil.getAsBeanFromMap(prop, UtilityAsset.class);
				AssetContext assetInfo = AssetsAPI.getAssetInfo(util.getAssetId());
				util.setAsset(assetInfo);
				List<UtilityAsset> utilList = utilityMap.get(util.getTenantId());
				if (utilList == null) {
					utilList = new ArrayList<>();
					utilityMap.put(util.getTenantId(), utilList);
				}
				utilList.add(util);
			}
			return utilityMap;
		}
		return null;
	}
	public static boolean checkIfZoneOccupiedByTenant (Long zoneId) throws Exception {
		
	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
	FacilioField zone = fieldMap.get("zone");

	Condition zoneIdCond = new Condition();
	zoneIdCond.setField(zone);
	zoneIdCond.setOperator(NumberOperators.EQUALS);
	zoneIdCond.setValue(zoneId+"");
	
	
	SelectRecordsBuilder<TenantContext> selectBuilder = new SelectRecordsBuilder<TenantContext>()
														.select(fields)
														.table(tenantModule.getTableName())
														.moduleName(tenantModule.getName())
														.beanClass(TenantContext.class)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tenantModule))
														.andCondition(zoneIdCond)
														
														;
	
	List<TenantContext> records = selectBuilder.get();
	if(records.size() > 0) {
		throw new IllegalArgumentException("Zone is occupied by the tenant "+records.get(0).getName());
	}
	return false;
		
	}

	public static boolean checkIfSpaceOccupiedByTenant (Long spaceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule tenantModule = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> tenantFields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		
		
		FacilioModule zoneRelModule = ModuleFactory.getZoneRelModule();
		List<FacilioField> fields = FieldFactory.getZoneRelFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		
		FacilioField baseSpaceField = fieldMap.get("basespaceId");

		Condition spaceIdCond = new Condition();
		spaceIdCond.setField(baseSpaceField);
		spaceIdCond.setOperator(NumberOperators.EQUALS);
		
		List<BaseSpaceContext> childrenSpaces = SpaceAPI.getBaseSpaceWithChildren(spaceId);
		StringJoiner idString = new StringJoiner(",");
		for (int i = 0;i<childrenSpaces.size();i++) {
			idString.add(String.valueOf(childrenSpaces.get(i).getId()));
		}
	    idString.add(spaceId+"");
	    spaceIdCond.setValue(idString.toString());
		
		SelectRecordsBuilder<TenantContext> selectBuilder = new SelectRecordsBuilder<TenantContext>()
															.select(tenantFields)
															.table(tenantModule.getTableName())
															.moduleName(tenantModule.getName())
															.beanClass(TenantContext.class)
															.innerJoin(zoneRelModule.getTableName())
															.on(zoneRelModule.getTableName()+".ZONE_ID = "+tenantModule.getTableName()+".ZONE_ID")
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tenantModule))
															.andCondition(spaceIdCond)
															;
		
		List<TenantContext> records = selectBuilder.get();
		if(records.size() > 0) {
			throw new IllegalArgumentException("The space is occupied by the one or more tenants");
		}
		return false;
			
		}

	
	private static Map<Long, List<ContactsContext>> getTenantUserDetails(List<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
																					;
		List<Map<String,Object>> props = ContactsAPI.getTenantContacts(ids);
		
		if (props != null && !props.isEmpty()) {
			Map<Long, List<ContactsContext>> uMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				ContactsContext tenantUser = FieldUtil.getAsBeanFromMap(prop, ContactsContext.class);
				
				List<ContactsContext> userList = uMap.get(tenantUser.getTenant().getId());
				if (userList == null) {
					userList = new ArrayList<>();
					uMap.put(tenantUser.getTenant().getId(), userList);
				}
			}
			return uMap;
		}  
		return null;
	}
	

	public static int updateTenantStatus(Long tenantId,int status) throws Exception{
        
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		FacilioField statusField = tenantFieldMap.get("status");
		updatedfields.add(statusField);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
//											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
											.andCondition(CriteriaAPI.getIdCondition(tenantId, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("status", status);
		int count = updateBuilder.update(value);
		return count;
			
	}

	
	public static void addUtilityMapping(TenantContext tenant,List<Long> spaceIds) throws Exception {
//		if (tenant.getUtilityAssets() == null || tenant.getUtilityAssets().isEmpty()) {
//			throw new IllegalArgumentException("Atleast one utility mapping should be present to add a Tenant");
//		}
		if(CollectionUtils.isNotEmpty(tenant.getUtilityAssets())) {
			validateUtilityMapping(tenant,spaceIds);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getTenantsUtilityMappingModule().getTableName())
															.fields(FieldFactory.getTenantsUtilityMappingFields())
															;
			for (UtilityAsset util : tenant.getUtilityAssets()) {
				util.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				util.setTenantId(tenant.getId());
				insertBuilder.addRecord(FieldUtil.getAsProperties(util));
			}
			insertBuilder.save();
		}
	}
	
	private static void validateUtilityMapping(TenantContext tenant,List<Long> spaceIds) throws Exception {
		StringJoiner idString = new StringJoiner(",");
		for (UtilityAsset util : tenant.getUtilityAssets()) {
			idString.add(String.valueOf(util.getAssetId()));
		}	
		
		validateAssetSpaces(tenant.getUtilityAssets(),spaceIds);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule utilityModule = ModuleFactory.getTenantsUtilityMappingModule();
		FacilioModule tenantModule = modBean.getModule("tenant");
		
		List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
		
		FacilioField assetIdFld = new FacilioField();
		assetIdFld.setName("assetId");
		assetIdFld.setColumnName("ASSET_ID");
		assetIdFld.setModule(ModuleFactory.getTenantsUtilityMappingModule());
		assetIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField sysDeletedFld = new FacilioField();
		sysDeletedFld.setName("sysDeleted");
		sysDeletedFld.setColumnName("SYS_DELETED");
		sysDeletedFld.setModule(tenantModule);
		sysDeletedFld.setDataType(FieldType.NUMBER);
	
		
		Condition assetIdCond = new Condition();
		assetIdCond.setField(assetIdFld);
		assetIdCond.setOperator(NumberOperators.EQUALS);
		assetIdCond.setValue(idString.toString());
	
		Condition sysDeletedCond = new Condition();
		sysDeletedCond.setField(sysDeletedFld);
		sysDeletedCond.setOperator(NumberOperators.NOT_EQUALS);
		sysDeletedCond.setValue("1");
	
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
											.select(fields)
											.table(utilityModule.getTableName())
											.innerJoin(tenantModule.getTableName())
											.on(utilityModule.getTableName()+".TENANT_ID = "+tenantModule.getTableName()+".ID")
											.andCondition(assetIdCond)
//											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(utilityModule))
											.andCondition(sysDeletedCond)
											.andCustomWhere(tenantModule.getTableName()+".STATUS = ?", 1);
												 

        List<Map<String, Object>> rs = builder.get();
        if (rs.size() > 0) {
        	Long alreadyPresentId = (Long) rs.get(0).get("assetId");
        	throw new IllegalArgumentException("Asset"+" #"+alreadyPresentId+" is associated to another tenant");
        }
	}
	
	private static void validateAssetSpaces(List<UtilityAsset> utilityAssets,List<Long> spaceIds) throws Exception {
		for (UtilityAsset util : utilityAssets) {
			BaseSpaceContext assetSpace = AssetsAPI.getAssetInfo(util.getAssetId()).getSpace();
			if(assetSpace != null && assetSpace.getId() > 0) {
		          BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(assetSpace.getId());
		          if(spaceIds.contains(baseSpace.getId())) {
		        	  continue;
		          }
		          if(baseSpace.getSpaceTypeEnum() == SpaceType.SPACE) {
		        	  if (spaceIds.contains(baseSpace.getSpaceId1()) || spaceIds.contains(baseSpace.getSpaceId2()) || spaceIds.contains(baseSpace.getSpaceId3()) || spaceIds.contains(baseSpace.getSpaceId4()) || spaceIds.contains(baseSpace.getFloorId()) || spaceIds.contains(baseSpace.getBuildingId())) {
		        		  continue;
		        	  }
		          }
		          else if(baseSpace.getSpaceTypeEnum() == SpaceType.FLOOR) {
		        	  if (spaceIds.contains(baseSpace.getBuildingId())) {
		        		  continue;
		        	  }
		          }
		          throw new IllegalArgumentException("The asset #"+util.getAssetId()+" doesn't belong to the selected Space");
			}
			else {
			    continue;			
			}
  		}
		
		
	}
	public static long addTenantLogo(TenantContext tenant) throws Exception {
		if (tenant.getTenantLogo() != null) {
			FileStore fs = FacilioFactory.getFileStore();
			long fileId = fs.addFile(tenant.getTenantLogoFileName(), tenant.getTenantLogo(), tenant.getTenantLogoContentType());
			tenant.setLogoId(fileId);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
			FacilioField logoField = modBean.getField("logoId", module.getName());
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			Map<String, Object> updateMap = new HashMap<>();
			
			updateMap.put("logoId", fileId);
			updatedfields.add(logoField);
			
			UpdateRecordBuilder<TenantContext> updateBuilder = new UpdateRecordBuilder<TenantContext>()
					.module(module)
					.fields(updatedfields)
					.andCondition(CriteriaAPI.getIdCondition(tenant.getId(), module))
				;
			
			updateBuilder.updateViaMap(updateMap);
			
		}
		return tenant.getLogoId();
	}
	
	public static void deleteTenantLogo(long logoId) throws Exception {
		if (logoId != -1) {
			FileStore fs = FacilioFactory.getFileStore();
			fs.deleteFile(logoId);
		}
	}
	
	
	public static int updateTenant (TenantContext tenant, List<Long> spaceIds) throws Exception {
		if (tenant.getId() == -1) {
			throw new IllegalArgumentException("Invalid ID during updation of tenant");
		}
		
		if (tenant.getUtilityAssets() != null && !tenant.getUtilityAssets().isEmpty()) {
			deleteUtilityMapping(tenant);
			addUtilityMapping(tenant, spaceIds);
		}
//		else
//		{
//			throw new IllegalArgumentException("Atleast one utility mapping should be present");
//			
//		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		CommonCommandUtil.handleLookupFormData(fields, tenant.getData());
		
		
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	    //updateContactDetails(tenant.getContact(),tenant.getId());
		UpdateRecordBuilder<TenantContext> updateBuilder = new UpdateRecordBuilder<TenantContext>()
														   .module(module)
														   .fields(fields)
//														   .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														   .andCondition(CriteriaAPI.getIdCondition(tenant.getId(), module));
		int count = updateBuilder.update(tenant);
		return count;
	}
	
	private static int deleteUtilityMapping(TenantContext tenant) throws Exception {
		FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
		List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenantId");
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(tenantId, String.valueOf(tenant.getId()), PickListOperators.IS))
														;
		return deleteBuilder.delete();
	}
	
		public static List<RateCardContext> getAllRateCards() throws Exception {
		FacilioModule module = ModuleFactory.getRateCardModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getRateCardFields())
														.table(module.getTableName());
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		return getRateCardsFromProps(selectBuilder.get());
	}
	
	public static RateCardContext getRateCard(long id) throws Exception {
		
		if (id <= 0) {
			return null;
		}
		
		FacilioModule module = ModuleFactory.getRateCardModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getRateCardFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		List<RateCardContext> rateCards = getRateCardsFromProps(selectBuilder.get());
		if (rateCards != null && !rateCards.isEmpty()) {
			return rateCards.get(0);
		}
		return null;
	}
	
	private static List<RateCardContext> getRateCardsFromProps(List<Map<String, Object>> props) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<RateCardContext> rateCards = new ArrayList<>();
			List<Long> ids = new ArrayList<>();
			
			for (Map<String, Object> prop : props) {
				RateCardContext rateCard = FieldUtil.getAsBeanFromMap(prop, RateCardContext.class);
				rateCards.add(rateCard);
				ids.add(rateCard.getId());
			}
			Map<Long, List<RateCardServiceContext>> serviceMap = getRateCardServicesMap(ids);
			if (serviceMap != null && !serviceMap.isEmpty()) {
				for (RateCardContext rateCard : rateCards) {
					rateCard.setServices(serviceMap.get(rateCard.getId()));
				}
			}
			return rateCards;
		}
		return null;
	}
	
	private static Map<Long, List<RateCardServiceContext>> getRateCardServicesMap (List<Long> ids) throws Exception {
		List<Map<String, Object>> props = getRateCardServicesPropsFromRateCardIds(ids);
		
		if (props != null && !props.isEmpty()) {
			Map<Long, List<RateCardServiceContext>> serviceMap = new HashMap<>();
			List<Long> workflowIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				RateCardServiceContext service = FieldUtil.getAsBeanFromMap(prop, RateCardServiceContext.class);
				if (service.getWorkflowId() != -1) {
					workflowIds.add(service.getWorkflowId());
				}
				
				List<RateCardServiceContext> services = serviceMap.get(service.getRateCardId());
				if (services == null) {
					services = new ArrayList<>();
					serviceMap.put(service.getRateCardId(), services);
				}
				services.add(service);
				
				if (!workflowIds.isEmpty()) {
					Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds, true);
					for (List<RateCardServiceContext> serviceList : serviceMap.values()) {
						for (RateCardServiceContext serviceObj : serviceList) {
							if (serviceObj.getWorkflowId() != -1) {
								serviceObj.setWorkflow(workflowMap.get(serviceObj.getWorkflowId()));
							}
						}
					}
				}
			}
			return serviceMap;
		}
		return null;
	}
	
	public static long addRateCard(RateCardContext rateCard) throws Exception {
		if (rateCard.getName() == null || rateCard.getName().isEmpty()) {
			throw new IllegalArgumentException("Invalid name during addition of rate card");
		}
		rateCard.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getRateCardModule().getTableName())
														.fields(FieldFactory.getRateCardFields())
														;
		long id = insertBuilder.insert(FieldUtil.getAsProperties(rateCard));
		rateCard.setId(id);
		addRateCardServices(rateCard);
		return id;
	}
	
	public static int updateRateCard(RateCardContext rateCard) throws Exception {
		if (rateCard.getId() == -1) {
			throw new IllegalArgumentException("Invalid ID during updation of Rate Card");
		}
		
		if (rateCard.getServices() != null && !rateCard.getServices().isEmpty()) {
			deleteRateCardServices(rateCard.getId());
			addRateCardServices(rateCard);
		}
		
		FacilioModule module = ModuleFactory.getRateCardModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getRateCardFields())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(rateCard.getId(), module))
														;
		return updateBuilder.update(FieldUtil.getAsProperties(rateCard));
	}
	
	public static int deleteRateCard (long id) throws Exception {
		if (id == -1) {
			throw new IllegalArgumentException("Invalid ID during deletion of rate card");
		}
		deleteRateCardServices(id);
		FacilioModule module = ModuleFactory.getRateCardModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		return deleteBuilder.delete();
	}
	
	private static List<Map<String, Object>> getRateCardServicesPropsFromRateCardIds (Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getRateCardServiceModule();
		List<FacilioField> fields = FieldFactory.getRateCardServiceFields();
		FacilioField rateCardIdFIeld = FieldFactory.getAsMap(fields).get("rateCardId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(rateCardIdFIeld, StringUtils.join(ids, ","), PickListOperators.IS));
		
		return selectBuilder.get();
	}
	
	private static int deleteRateCardServices (long rateCardId) throws Exception {
		List<Map<String, Object>> props = getRateCardServicesPropsFromRateCardIds(Collections.singletonList(rateCardId));
		
		if (props != null && !props.isEmpty()) {
			List<Long> ids = new ArrayList<>();
			List<Long> workflowIds = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ids.add((Long) prop.get("id"));
				Long workflowId = (Long) prop.get("workflowId");
				if (workflowId != null) {
					workflowIds.add(workflowId);
				}
			}
			FacilioModule module = ModuleFactory.getRateCardServiceModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table(module.getTableName())
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(ids, module))
															;
			int rowsUpdated =  deleteBuilder.delete();
			
			if (!workflowIds.isEmpty()) {
				WorkflowUtil.deleteWorkflows(workflowIds);
			}
			return rowsUpdated;
		}
		return -1;
	}
	
	private static void addRateCardServices (RateCardContext rateCard) throws Exception {
		if (rateCard.getServices() == null || rateCard.getServices().isEmpty()) {
			throw new IllegalArgumentException("Atleast one service has to be added for a Rate Card");
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getRateCardServiceModule().getTableName())
														.fields(FieldFactory.getRateCardServiceFields())
														;
		for (RateCardServiceContext service : rateCard.getServices()) {
			service.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			service.setRateCardId(rateCard.getId());
			validateRateCardService(service);
			updateChildIdsForService(service);
			insertBuilder.addRecord(FieldUtil.getAsProperties(service));
		}
		insertBuilder.save();
	}
	
	private static void updateChildIdsForService(RateCardServiceContext service) throws Exception {
		if (service.getWorkflowId() == -1 && service.getWorkflow() != null) {
			service.setWorkflowId(WorkflowUtil.addWorkflow(service.getWorkflow()));
		}
	}
	
	public static void loadTenantLookups(Collection<? extends TenantContext> tenants) throws Exception {
		loadTenantZones(tenants);
		loadTenantProps(tenants);
		
	}
	
	private static void loadTenantZones(Collection<? extends TenantContext> tenants) throws Exception {
		if(tenants != null && !tenants.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule zoneModule = modBean.getModule(FacilioConstants.ContextNames.ZONE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ZONE);
			
				SelectRecordsBuilder<ZoneContext> selectBuilder = new SelectRecordsBuilder<ZoneContext>()
																				.select(fields)
																				.table(zoneModule.getTableName())
																				.moduleName(zoneModule.getName())
																				.beanClass(ZoneContext.class)
//																				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(zoneModule))
																				;
				Map<Long, ZoneContext> zones = selectBuilder.getAsMap();
				
				for(TenantContext tenant : tenants) {
					if (tenant != null) {
						ZoneContext zone = tenant.getZone();
						if(zone != null) {
							tenant.setZone(zones.get(zone.getId()));
						}
					}
				}
			
		}
	}
	
	private static void loadTenantProps(Collection<? extends TenantContext> tenants) throws Exception {
		try {
			List<Long> ids = new ArrayList<>();
			for (TenantContext tenant : tenants) {
				ids.add(tenant.getId());
				if (tenant.getLogoId()  != -1) {
					FileStore fs = FacilioFactory.getFileStore();
					tenant.setLogoUrl(fs.getPrivateUrl(tenant.getLogoId()));
				}
			}
			Map<Long, List<UtilityAsset>> utilMap = getUtilityAssets(ids);
			if (utilMap != null && !utilMap.isEmpty()) {
				for (TenantContext tenant : tenants) {
					tenant.setUtilityAssets(utilMap.get(tenant.getId()));
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
	}
	
	public static List<ContactsContext> getTenantContacts(long id) throws Exception {
		List<Map<String,Object>> contactList = ContactsAPI.getTenantContacts(Collections.singletonList(id));
		return FieldUtil.getAsBeanListFromMapList(contactList, ContactsContext.class);
	}
	
	public static List<OccupantsContext> getTenantOccupants(long id) throws Exception {
		List<Map<String,Object>> occupantList = OccupantsAPI.getTenantOccupants(Collections.singletonList(id));
		return FieldUtil.getAsBeanListFromMapList(occupantList, OccupantsContext.class);
	}
	
	public static String getLogoUrl(Long logoId) throws Exception {
		FileStore fs = FacilioFactory.getFileStore();
		return fs.getPrivateUrl(logoId);
	}
	
	public static List<Map<String, Object>> getFireAlarmsCount(List<Long> assetIds) throws Exception {
		
	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	   FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.ALARM);
	   List<FacilioField> alarmFields = modBean.getAllFields(alarmModule.getName());

		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		StringJoiner idsString = new StringJoiner(",");
		for (int i=0;i<assetIds.size();i++) {
		   idsString.add(assetIds.get(i)+"");
		}
		Condition resourceIdCond = new Condition();
		resourceIdCond.setField(resourceIdFld);
		resourceIdCond.setOperator(NumberOperators.EQUALS);
		resourceIdCond.setValue(idsString.toString());
		
		
		AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
											.select(alarmFields)
											.table("Alarms")
											.innerJoin("Tickets")
											.on("Alarms.ID = Tickets.ID")
											.andCondition(ViewFactory.getAlarmSeverityCondition(FacilioConstants.Alarm.CLEAR_SEVERITY, false))
											.andCondition(resourceIdCond)
//											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(alarmModule))
											;
		
		List<Map<String, Object>> rs = builder.get();
	    return rs;
	}


	public static List<WorkOrderContext> getWorkOrdersCount(long tenantId) throws Exception {
		
		
		FacilioField tenantIdFld = new FacilioField();
		tenantIdFld.setName("tenantId");
		tenantIdFld.setColumnName("TENANT_ID");
		tenantIdFld.setModule(ModuleFactory.getTicketsModule());
		tenantIdFld.setDataType(FieldType.NUMBER);

		Condition tenantCond = new Condition();
		tenantCond.setField(tenantIdFld);
		tenantCond.setOperator(NumberOperators.EQUALS);
		tenantCond.setValue(tenantId+"");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														  .module(workOrderModule)
														  .beanClass(WorkOrderContext.class)
														  .select(workorderFields)
														  .innerJoin("TicketStatus")
														  .on("Tickets.STATUS_ID = TicketStatus.ID")
														  .andCustomWhere("TicketStatus.STATUS_TYPE = ?", FacilioStatus.StatusType.OPEN.getIntVal())
//														  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(workOrderModule))
														  .andCondition(tenantCond);
									
		List<WorkOrderContext> rs = builder.get();
		TicketAPI.loadTicketLookups(rs);
		
		return rs;
	}
	
	public static TenantContext getTenantForResource(long resourceId) throws Exception{
		Map<Long, TenantContext> tenantMap = getTenantForResources(Collections.singletonList(resourceId));
		if (MapUtils.isNotEmpty(tenantMap)) {
			return tenantMap.get(resourceId);
		}
		return null;
	}
	
	public static Map<Long, TenantContext> getTenantForResources(List<Long> resourceIds) throws Exception{
		List<ResourceContext> resources = ResourceAPI.getResources(resourceIds, false);
		List<Long> spaceIds = new ArrayList<>();
		for(ResourceContext resource: resources) {
			long spaceId = resource.getResourceTypeEnum() == ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
			if (spaceId != -1) {
				spaceIds.add(spaceId);
			}
		}
		Map<Long, TenantContext> spaceTenants = TenantsAPI.getTenantForSpace(spaceIds);
		if (MapUtils.isNotEmpty(spaceTenants)) {
			Map<Long, TenantContext> tenants = new HashMap<>();
			for(ResourceContext resource: resources) {
				long spaceId = resource.getResourceTypeEnum() == ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
				if (spaceId != -1 && spaceTenants.containsKey(spaceId)) {
					tenants.put(resource.getId(), spaceTenants.get(spaceId));
				}
			}
			return tenants;
		}
		return null;
	}
	
	public static TenantContext getTenantForSpace(long spaceId) throws Exception{
		Map<Long, TenantContext> spaceTenant = getTenantForSpace(Collections.singletonList(spaceId));
		if (spaceTenant != null) {
			return spaceTenant.get(spaceId);
		}
		return null;
	}

	public static Map<Long, TenantContext> getTenantForSpace(Collection<Long> spaceIds) throws Exception {
		List<Long> filteredSpaces = spaceIds.stream().filter(i -> i > 0).collect(Collectors.toList());
		Map<Long, TenantContext> empty = new HashMap<>();
		TenantContext emptyTenant = new TenantContext();
		emptyTenant.setId(-99);
		empty.put(-1L, emptyTenant);

		if (filteredSpaces.isEmpty()) {
			return empty;
		}
		List<TenantSpaceContext> tenantSpaces = getTenantSpaces(spaceIds, false);
		if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
			return tenantSpaces.stream().collect(Collectors.toMap(tenantSpace -> tenantSpace.getSpace().getId(), TenantSpaceContext::getTenant));
		}
		return empty;
	}
	
	public static List<TenantContext> getAllTenantsForResource(long resourceId) throws Exception {
		ResourceContext resource = ResourceAPI.getResource(resourceId);
		long spaceId = resource.getResourceTypeEnum() == ResourceType.ASSET ? resource.getSpaceId() : resource.getId();
		return getAllTenantsForSpace(Collections.singletonList(spaceId));
	}
	
	public static List<TenantContext> getAllTenantsForSpace(Collection<Long> spaceIds) throws Exception {
		List<TenantSpaceContext> tenantSpaces = getTenantSpaces(spaceIds, true);
		if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
			return tenantSpaces.stream().map(tenantSpace -> tenantSpace.getTenant()).collect(Collectors.toList());
		}
		return null;
	}
	
	private static List<TenantSpaceContext> getTenantSpaces(Collection<Long> spaceIds, boolean fetchChildTenants) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(fields);
		Operator operator = fetchChildTenants ? BuildingOperator.BUILDING_IS : NumberOperators.EQUALS;
		SelectRecordsBuilder<TenantSpaceContext> builder = new SelectRecordsBuilder<TenantSpaceContext>()
				  .module(module)
				  .beanClass(TenantSpaceContext.class)
				  .select(fields)
				  .andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("space"), spaceIds, operator))
				  .fetchSupplement((LookupField)tenantSpaceFieldMap.get("tenant"))
				  ;
		
		return builder.get();
	}
	
    public static List<Map<String,Object>> getPmCount(long tenantId) throws Exception {
		
    	
    	FacilioField tenantIdFld = new FacilioField();
		tenantIdFld.setName("tenantId");
		tenantIdFld.setColumnName("TENANT_ID");
		tenantIdFld.setModule(ModuleFactory.getTicketsModule());
		tenantIdFld.setDataType(FieldType.NUMBER);

		Condition tenantCond = new Condition();
		tenantCond.setField(tenantIdFld);
		tenantCond.setOperator(NumberOperators.EQUALS);
		tenantCond.setValue(tenantId+"");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
				
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
	   FacilioField pmId = new FacilioField();
	   pmId.setName("pm");
	   pmId.setColumnName("DISTINCT PM_ID");
	   List<FacilioField> selectFields = new ArrayList<FacilioField>();
	   selectFields.add(pmId);
	   GenericSelectRecordBuilder workOrderQuery = new GenericSelectRecordBuilder()
													.table(workOrderModule.getTableName())
													.select(selectFields)
													.innerJoin(ticketModule.getTableName())
													.on(ticketModule.getTableName()+".ID = "+workOrderModule.getTableName()+".ID")
													.innerJoin("TicketStatus")
													.on("Tickets.STATUS_ID = TicketStatus.ID")
													.andCustomWhere("TicketStatus.STATUS_TYPE = "+ FacilioStatus.StatusType.PRE_OPEN.getIntVal())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(workOrderModule))
													.andCondition(tenantCond);
							            		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCustomWhere(FieldFactory.getIdField(module).getCompleteColumnName() + " in (" + workOrderQuery.constructSelectStatement() + ")");
		List<Map<String,Object>> rs = selectBuilder.get();
		
		return rs;
	}


	
	private static void validateRateCardService(RateCardServiceContext service) {
		if (service.getName() == null || service.getName().isEmpty()) {
			throw new IllegalArgumentException("Invalid name for service during rate card addition");
		}
		
		if (service.getServiceTypeEnum() == null) {
			throw new IllegalArgumentException("Service type cannot be null during rate card addition");
		}
		
		switch (service.getServiceTypeEnum()) {
			case UTILITY:
				if (service.getUtilityEnum() == null) {
					throw new IllegalArgumentException("Utility cannot be null for service type : "+RateCardServiceContext.ServiceType.UTILITY);
				}
				if (service.getPrice() == -1) {
					throw new IllegalArgumentException("Pricee cannot be null for service type : "+RateCardServiceContext.ServiceType.UTILITY);
				}
				break;
			case FORMULA:
				if (service.getWorkflow() == null) {
					throw new IllegalArgumentException("Workflow cannot be null for service type : "+RateCardServiceContext.ServiceType.FORMULA);
				}
				break;
		}

}
	
	public static List<BaseSpaceContext> fetchTenantSpaces(long tenantId) throws Exception {
		return fetchTenantSpaces(tenantId, true);
	}
	
	public static List<BaseSpaceContext> fetchTenantSpaces(long tenantId, boolean fetchSpaceDetails) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
		Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		SelectRecordsBuilder<TenantSpaceContext> builder = new SelectRecordsBuilder<TenantSpaceContext>()
				  .module(module)
				  .beanClass(TenantSpaceContext.class)
				  .select(Collections.singletonList(tenantSpaceFieldMap.get("space")))
				  .andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"), String.valueOf(tenantId), NumberOperators.EQUALS))
				  ;
		
		if (fetchSpaceDetails) {
			builder.fetchSupplement((LookupField)tenantSpaceFieldMap.get("space"));
		}
		
		List<TenantSpaceContext> tenantSpaces = builder.get();
		if (tenantSpaces != null && !tenantSpaces.isEmpty()) {
			return tenantSpaces.stream().map(TenantSpaceContext::getSpace).collect(Collectors.toList());
		}
		return null;
	}
	
	public static void deleteTenantSpace(List<Long> tenantIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_SPACES);
		Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        DeleteRecordBuilder<? extends ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"), tenantIds, NumberOperators.EQUALS));
        deleteBuilder.delete();
	}

	public static void addAddress(String name, LocationContext location) throws Exception {
		if (location != null) {
			if (location.getId() > 0) {
				FacilioChain chain = FacilioChainFactory.updateLocationChain();
				chain.getContext().put(FacilioConstants.ContextNames.RECORD, location);
				chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
				chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
				chain.execute();
			} else {
				location.setName(name + "_location");
				FacilioChain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, location);
				addLocation.execute();
				long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
	}

	public static List<TenantUnitSpaceContext> getTenantUnitsForTenant(long tenantId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.TENANT_UNIT_SPACE);
		Map<String, FacilioField> tenantSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		SelectRecordsBuilder<TenantUnitSpaceContext> builder = new SelectRecordsBuilder<TenantUnitSpaceContext>()
				.module(module)
				.beanClass(TenantUnitSpaceContext.class)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getCondition(tenantSpaceFieldMap.get("tenant"), String.valueOf(tenantId), PickListOperators.IS))
				;
		return builder.get();

	}
}
