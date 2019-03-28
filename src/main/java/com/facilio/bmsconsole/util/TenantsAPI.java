package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.LabourContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.RateCardServiceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantUserContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.log4j.Logger;


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
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
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
		getTenant(id, true);
		FacilioModule module = ModuleFactory.getTenantsuserModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsUserFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("Tenant_Users.TENANTID = ? ", id);
		List<Map<String, Object>> props = selectBuilder.get();
		List<TenantUserContext> tenantUsers = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				TenantUserContext tenantUser = FieldUtil.getAsBeanFromMap(prop, TenantUserContext.class);
				User user = AccountUtil.getUserBean().getUser(tenantUser.getOuid());
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
		Map<String, Object> values = props.get(0);
		long value = (long) values.get("tenantId");
		System.out.println("*********************" +value);
		
		
		FacilioModule modulo = ModuleFactory.getTenantsUtilityMappingModule();
		GenericSelectRecordBuilder selectBuilde = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsUtilityMappingFields())
														.table(modulo.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(modulo))
														.andCustomWhere("Tenants_Utility_Mapping.TENANT_ID = ?", value)
														.andCustomWhere("Tenants_Utility_Mapping.SHOW_IN_PORTAL = ?", true);
		List<Map<String, Object>> prop = selectBuilde.get();
		System.out.println("&&&&&&&&&&&&&&&&&&&" +prop);
		
		
		long Id;
		List<Long> Ids = new ArrayList<Long>();

		for (Map<String, Object> pro : prop) {
			Id =  (long) pro.get("assetId");
			Ids.add(Id);
			System.out.println("!!!!!!!!!!!!!!!" +Id);
		}
		System.out.println("!!!!!!!!!!!!!!!" +Ids);
		List<ResourceContext> longArray = ResourceAPI.getExtendedResources(Ids,false);
		System.out.println("$$$$$$$$$$$$$$$$$$$$$" +longArray);
		
		
		return longArray;
}
	
	
	public static long updatePortalUserAccess(long ouiId,Object portal_verified) throws Exception {
		
		
		FacilioModule modulo = ModuleFactory.getOrgUserModule();
		GenericSelectRecordBuilder selectBuilde = new GenericSelectRecordBuilder()
														.select(FieldFactory.getOrgUserFields())
														.table(modulo.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(modulo))
														.andCustomWhere("ORG_Users.ORG_USERID = ?", ouiId);
		List<Map<String, Object>> prop = selectBuilde.get();
		
		Map<String, Object> values = prop.get(0);
		long userId = (long) values.get("userId");
		
		
		User user = AccountUtil.getUserBean().getPortalUser(userId);
		if (user.getPortal_verified() == false) {
		
			(new UserBeanImpl()).sendInvitation(user.getOuid(), user);
			
		}
		
		FacilioModule module = ModuleFactory.getOrgUserModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getOrgUserFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("ORG_Users.USERID = ?", userId);
		
		Map<String, Object> value = new HashMap<>();
		value.put("portal_verified", portal_verified);
		int count = updateBuilder.update(value);
		
		return count;
	}
	 
	public static TenantContext getTenant(long id, Boolean...fetchTenantOnly) throws Exception {
		
		if (id <= 0) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	    SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));

		List<TenantContext> tenants = builder.get();
		loadTenantLookups(tenants);
		if (tenants != null && !tenants.isEmpty()) {
			return tenants.get(0);
		}
		return null;
	}
	
	public static TenantContext fetchTenant(long id) throws Exception {
		
		if (id <= 0) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
														.module(module)
														.beanClass(TenantContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		
		List<TenantContext> records = builder.get();
		TenantsAPI.loadTenantLookups(records);
	    if (records != null && !records.isEmpty()) {
			return records.get(0);
		}
		return null;
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("ZONE_ID", "zoneId", zoneId+"", NumberOperators.EQUALS))
														;
		
		List<TenantContext> records = builder.get();
	//	TenantsAPI.loadTenantLookups(records);
	    if (records != null && !records.isEmpty()) {
			return records.get(0);
		}
		return null;
	}
	
	public static TenantContext fetchTenantForSpace(long spaceId) throws Exception {
		
		if (spaceId <= 0) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		FacilioModule module = ModuleFactory.getZoneRelModule();
		
		FacilioField isDeletedField = FieldFactory.getIsDeletedField();
		isDeletedField.setModule(resourceModule);
		
		Condition isDeletedCond = new Condition();
		isDeletedCond.setField(isDeletedField);
		isDeletedCond.setOperator(NumberOperators.NOT_EQUALS);
		isDeletedCond.setValue(""+1);

		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getZoneRelFields()).table(module.getTableName())
														.innerJoin(resourceModule.getTableName())
														.on(module.getTableName()+".ZONE_ID = "+resourceModule.getTableName()+".ID")
														.andCondition(CriteriaAPI.getCondition(module.getTableName()+".BASE_SPACE_ID", "base_space_id" ,spaceId+"", StringOperators.STARTS_WITH))
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(isDeletedCond)
														;
												
		List<Map<String,Object>> records = builder.get();
	    if (records != null && !records.isEmpty()) {
	    	TenantContext tenant = fetchTenantForZone((Long)records.get(0).get("zoneId"));
			return tenant;
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
					FileStore fs = FileStoreFactory.getInstance().getFileStore();
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
												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tenantModule))
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
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(tenantModule))
															.andCondition(spaceIdCond)
															;
		
		List<TenantContext> records = selectBuilder.get();
		if(records.size() > 0) {
			throw new IllegalArgumentException("The space is occupied by the one or more tenants");
		}
		return false;
			
		}

	
	private static Map<Long, List<TenantUserContext>> getTenantUserDetails(Collection<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		FacilioModule module = ModuleFactory.getTenantsuserModule();
		FacilioModule portalUsersModule = AccountConstants.getPortalUserModule();
		FacilioModule orgUserModule = AccountConstants.getOrgUserModule();
			
		List<FacilioField> fields = FieldFactory.getTenantsUserFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenantId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.innerJoin(orgUserModule.getTableName())
													.on(orgUserModule.getTableName()+".ORG_USERID = "+module.getTableName()+".ORG_USERID")
													.innerJoin(portalUsersModule.getTableName())
													.on(portalUsersModule.getTableName()+".USERID = "+orgUserModule.getTableName()+".USERID")
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(tenantId, ids, PickListOperators.IS))
																					;
		List<Map<String,Object>> props = selectBuilder.get();
			
		if (props != null && !props.isEmpty()) {
			Map<Long, List<TenantUserContext>> uMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				TenantUserContext tenantUser = FieldUtil.getAsBeanFromMap(prop, TenantUserContext.class);
				User user = AccountUtil.getUserBean().getUser(tenantUser.getOuid());
				List<TenantUserContext> userList = uMap.get(tenantUser.getTenantId());
				if (userList == null) {
					userList = new ArrayList<>();
					uMap.put(tenantUser.getTenantId(), userList);
				}
				tenantUser.setOrgUser(user);
				userList.add(tenantUser);
			}
			return uMap;
		}  
		return null;
	}
	
	@SuppressWarnings("unused")
//	public static TenantContext addTenant (TenantContext tenant) throws Exception {
	public static long addTenant (TenantContext tenant) throws Exception {
		
		if (tenant.getName() == null || tenant.getName().isEmpty()) {
			throw new IllegalArgumentException("Invalid name during addition of Tenant");
		}
		
		addTenantLogo(tenant);
		tenant.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		InsertRecordBuilder<TenantContext> insertBuilder = new InsertRecordBuilder<TenantContext>()
														.table(ModuleFactory.getTenantsModule().getTableName())
														.fields(FieldFactory.getTenantsFields());
		long id = insertBuilder.insert(tenant);
		tenant.setId(id);
		addUtilityMapping(tenant);
		return id;
//		return tenant;
	}
	
	public static void addTenantContact(User user, Long tenantId) throws Exception{
		long orgid = AccountUtil.getCurrentOrg().getOrgId();
		user.setOrgId(orgid);
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		try {
			AccountUtil.getUserBean().inviteRequester(orgid, user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    
		Map<String, Object> prop = new HashMap<>();
		
		prop.put("tenantId", tenantId);
		prop.put("ouid", user.getId());
		prop.put("orgId", user.getOrgId());
				
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
		insert.table(ModuleFactory.getTenantsuserModule().getTableName());
		insert.fields(FieldFactory.getTenantsUserFields());
		insert.addRecord(prop);

		insert.save(); 

		
	}

	public static int updateTenantPrimaryContact(User user, Long tenantId) throws Exception{
         
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		Map<String, FacilioField> tenantFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		FacilioField contactField = tenantFieldMap.get("contact");
		updatedfields.add(contactField);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(fields)
											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
											.andCondition(CriteriaAPI.getIdCondition(tenantId, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("contact", user.getOuid());
		int count = updateBuilder.update(value);
		return count;
			
	}

	
	public static void addUtilityMapping(TenantContext tenant) throws Exception {
		if (tenant.getUtilityAssets() == null || tenant.getUtilityAssets().isEmpty()) {
			throw new IllegalArgumentException("Atleast one utility mapping should be present to add a Tenant");
		}
		validateUtilityMapping(tenant);
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
	
	private static void validateUtilityMapping(TenantContext tenant) throws Exception {
		StringJoiner idString = new StringJoiner(",");
		for (UtilityAsset util : tenant.getUtilityAssets()) {
			idString.add(String.valueOf(util.getAssetId()));
		}	
		
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
											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(utilityModule))
											.andCondition(sysDeletedCond);

        List<Map<String, Object>> rs = builder.get();
        if (rs.size() > 0) {
        	Long alreadyPresentId = (Long) rs.get(0).get("assetId");
        	throw new IllegalArgumentException("Asset"+" #"+alreadyPresentId+" is associated to another tenant");
        }
	}
	
	public static void addTenantLogo(TenantContext tenant) throws Exception {
		if (tenant.getTenantLogo() != null) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			long fileId = fs.addFile(tenant.getTenantLogoFileName(), tenant.getTenantLogo(), tenant.getTenantLogoContentType());
			tenant.setLogoId(fileId);
			tenant.setTenantLogo(null);
		}
	}
	
	public static void deleteTenantLogo(long logoId) throws Exception {
		if (logoId != -1) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.deleteFile(logoId);
		}
	}
	
	
	public static int updateTenant (TenantContext tenant) throws Exception {
		if (tenant.getId() == -1) {
			throw new IllegalArgumentException("Invalid ID during updation of tenant");
		}
		
		if (tenant.getUtilityAssets() != null && !tenant.getUtilityAssets().isEmpty()) {
			deleteUtilityMapping(tenant);
			addUtilityMapping(tenant);
		}
		else
		{
			throw new IllegalArgumentException("Atleast one utility mapping should be present");
			
		}
		
		TenantContext oldTenant = null;
		if (tenant.getTenantLogo() != null) {
			addTenantLogo(tenant);
			oldTenant = getTenant(tenant.getId(), true);
		}
		
	    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	    updateContactDetails(tenant.getContact(),tenant.getId());
		UpdateRecordBuilder<TenantContext> updateBuilder = new UpdateRecordBuilder<TenantContext>()
														   .module(module)
														   .fields(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
														   .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														   .andCondition(CriteriaAPI.getIdCondition(tenant.getId(), module));
		int count = updateBuilder.update(tenant);
		
		if (oldTenant != null) {
			deleteTenantLogo(oldTenant.getLogoId());
		}
		
		return count;
	}
	
	private static void updateContactDetails(User contact,Long tenantId) throws Exception {
		String email = contact.getEmail();
		String name = contact.getName();
		long id = contact.getId();
		User oldUser = AccountUtil.getUserBean().getUser(id);
		if (oldUser.getName().contentEquals(name) == false && oldUser.getEmail().contentEquals(email)) {
		  oldUser.setName(name);
		  AccountUtil.getUserBean().updateUser(oldUser);
		}
		else if (oldUser.getEmail().contentEquals(email) == false) {
			long orgid = AccountUtil.getCurrentOrg().getOrgId();
			contact.setOrgId(orgid);
			if(contact.getEmail() == null || contact.getEmail().isEmpty()) {
				contact.setEmail(contact.getMobile());
			}
			long userId = AccountUtil.getUserBean().inviteRequester(orgid, contact);
			addTenantContact(contact, tenantId);
		}
	}
	private static int deleteUtilityMapping(TenantContext tenant) throws SQLException {
		FacilioModule module = ModuleFactory.getTenantsUtilityMappingModule();
		List<FacilioField> fields = FieldFactory.getTenantsUtilityMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenantId");
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(tenantId, String.valueOf(tenant.getId()), PickListOperators.IS))
														;
		return deleteBuilder.delete();
	}
	
		public static List<RateCardContext> getAllRateCards() throws Exception {
		FacilioModule module = ModuleFactory.getRateCardModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getRateCardFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
																				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(zoneModule))
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
					FileStore fs = FileStoreFactory.getInstance().getFileStore();
					tenant.setLogoUrl(fs.getPrivateUrl(tenant.getLogoId()));
				}
				tenant.setContact(AccountUtil.getUserBean().getUser(tenant.getContact().getId()));
				
			}
			Map<Long, List<UtilityAsset>> utilMap = getUtilityAssets(ids);
			if (utilMap != null && !utilMap.isEmpty()) {
				for (TenantContext tenant : tenants) {
					tenant.setUtilityAssets(utilMap.get(tenant.getId()));
				}
			}
			Map<Long, List<TenantUserContext>> userMap = getTenantUserDetails(ids);
			if (userMap != null && !userMap.isEmpty()) {
				for (TenantContext tenant : tenants) {
					tenant.setTenantUsers(userMap.get(tenant.getId()));
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
	}
	
	public static String getLogoUrl(Long logoId) throws Exception {
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
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
											.andCondition(CriteriaAPI.getCurrentOrgIdCondition(alarmModule))
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
														  .andCustomWhere("TicketStatus.STATUS_TYPE = ?", TicketStatusContext.StatusType.OPEN.getIntVal())
														  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(workOrderModule))
														  .andCondition(tenantCond);
									
		List<WorkOrderContext> rs = builder.get();
		TicketAPI.loadTicketLookups(rs);
		
		return rs;
	}

	public static TenantContext getTenantForSpace(long spaceId) throws Exception{
		if(spaceId != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule zoneRelModule = ModuleFactory.getZoneRelModule();
			FacilioModule tenantModule = modBean.getModule("tenant");
			FacilioModule zoneModule = ModuleFactory.getZoneModule();
			
			List<FacilioField> tenantFields = modBean.getAllFields(tenantModule.getName());
			SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
															.module(tenantModule)
															.beanClass(TenantContext.class)
															.select(tenantFields)
															.innerJoin(zoneModule.getTableName())
															.on(tenantModule.getTableName()+".ZONE_ID = "+zoneModule.getTableName()+".ID")
														    .innerJoin(zoneRelModule.getTableName())
															.on(zoneRelModule.getTableName()+".ZONE_ID = "+zoneModule.getTableName()+".ID")
														    .andCondition(CriteriaAPI.getCondition(zoneRelModule.getTableName()+".BASE_SPACE_ID","baseSpaceId",""+spaceId,NumberOperators.EQUALS))
															;
			List<TenantContext> tenantList = builder.get();
			if(!CollectionUtils.isEmpty(tenantList)) {
				return tenantList.get(0);
			}
		}
		TenantContext tenant = new TenantContext();
		tenant.setId(-99);
		return tenant;
		
	}
	
	public static TenantContext getTenantForAsset(long assetId) throws Exception{
		if(assetId != -1) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule tenantUtilityModule = ModuleFactory.getTenantsUtilityMappingModule();
			FacilioModule tenantModule = modBean.getModule("tenant");
			
			List<FacilioField> tenantFields = modBean.getAllFields(tenantModule.getName());
			SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
															.module(tenantModule)
															.beanClass(TenantContext.class)
															.select(tenantFields)
															.innerJoin(tenantUtilityModule.getTableName())
															.on(tenantUtilityModule.getTableName()+".TENANT_ID = "+tenantModule.getTableName()+".ID")
														    .andCondition(CriteriaAPI.getCondition(tenantUtilityModule.getTableName()+".ASSET_ID","assetId",""+assetId,NumberOperators.EQUALS))
															;
			List<TenantContext> tenantList = builder.get();
			if(!CollectionUtils.isEmpty(tenantList)) {
				return tenantList.get(0);
			}
		}
		TenantContext tenant = new TenantContext();
		tenant.setId(-99);
		return tenant;
		
	}
    public static List<Map<String,Object>> getPmCount(long zoneId,List<Long> assetIdList) throws Exception {
		
    	List<Long> zoneList = new ArrayList<Long>();
		zoneList.add(zoneId);
		
		StringJoiner assetIdString = new StringJoiner(",");
		for(Long id : assetIdList) {
			assetIdString.add(String.valueOf(id));
		}
		List<BaseSpaceContext> spaces = SpaceAPI.getZoneChildren(zoneList);
		StringJoiner idString = new StringJoiner(",");
		for(BaseSpaceContext space:spaces) {
			idString.add(String.valueOf(space.getId()));
		}
		idString.add(assetIdString.toString());
		FacilioField tenantIdFld = new FacilioField();
		tenantIdFld.setName("tenantId");
		tenantIdFld.setColumnName("TENANT_ID");
		tenantIdFld.setModule(ModuleFactory.getWorkOrderTemplateModule());
		tenantIdFld.setDataType(FieldType.NUMBER);

		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		FacilioModule woTemplatemodule = ModuleFactory.getWorkOrderTemplateModule();
		
		
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.innerJoin(woTemplatemodule.getTableName())
														.on("TEMPLATE_ID = "+woTemplatemodule.getTableName() +".ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(woTemplatemodule.getTableName()+".RESOURCE_ID", "resourceId", idString.toString(), NumberOperators.EQUALS));
									
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
}
