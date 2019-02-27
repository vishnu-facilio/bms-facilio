package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.ZoneContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
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
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
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
import org.apache.log4j.LogManager;
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
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
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
		
		FacilioModule module = ModuleFactory.getZoneRelModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getZoneRelFields()).table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(module.getTableName()+".BASE_SPACE_ID", "base_space_id" ,spaceId+"", StringOperators.STARTS_WITH))
													    ;
												
		List<Map<String,Object>> records = builder.get();
	    if (records != null && !records.isEmpty()) {
	    	TenantContext tenant = fetchTenantForZone((Long)records.get(0).get("zone_id"));
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
	
	private static Map<Long, List<TenantUserContext>> getTenantUserDetails(Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getTenantsuserModule();
		List<FacilioField> fields = FieldFactory.getTenantsUserFields();
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
	
	public static void addUtilityMapping(TenantContext tenant) throws Exception {
		if (tenant.getUtilityAssets() == null || tenant.getUtilityAssets().isEmpty()) {
			throw new IllegalArgumentException("Atleast one utility mapping should be present to add a Tenant");
		}
		
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
	
	public static void addTenantLogo(TenantContext tenant) throws Exception {
		if (tenant.getTenantLogo() != null) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			long fileId = fs.addFile(tenant.getTenantLogoFileName(), tenant.getTenantLogo(), tenant.getTenantLogoContentType());
			tenant.setLogoId(fileId);
			tenant.setTenantLogo(null);
		}
	}
	
	private static void deleteTenantLogo(long logoId) throws Exception {
		if (logoId != -1) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			fs.deleteFile(logoId);
		}
	}
	
	private static void deleteTenantZone(long zoneId) throws Exception {
		if (zoneId != -1) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ID, zoneId);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "zone");
			Chain deleteZone = FacilioChainFactory.deleteSpaceChain();
			deleteZone.execute(context);
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
		
		TenantContext oldTenant = null;
		if (tenant.getTenantLogo() != null) {
			addTenantLogo(tenant);
			oldTenant = getTenant(tenant.getId(), true);
		}
		
	    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	
		UpdateRecordBuilder<TenantContext> updateBuilder = new UpdateRecordBuilder<TenantContext>()
														   .module(module)
														   .fields(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
														   .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														   .andCondition(CriteriaAPI.getIdCondition(tenant.getId(), module));
		int count = updateBuilder.update(FieldUtil.getAsProperties(tenant));
		
		if (oldTenant != null) {
			deleteTenantLogo(oldTenant.getLogoId());
		}
		
		return count;
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
	
	public static int deleteTenant (List<Long> ids) throws Exception {
		
		for(int i=0;i<ids.size();i++)
		{
			TenantContext oldTenant = getTenant(ids.get(i), true);
			deleteTenantLogo(oldTenant.getLogoId());
			deleteTenantZone(oldTenant.getZone().getId());

		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
	
		DeleteRecordBuilder<? extends TenantContext> deleteBuilder = new DeleteRecordBuilder<TenantContext>()
																	.module(module)
																	.andCondition(CriteriaAPI.getIdCondition(ids, module))
																	;
		int count = deleteBuilder.markAsDelete();
		return count;
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
			
			try {
				SelectRecordsBuilder<ZoneContext> selectBuilder = new SelectRecordsBuilder<ZoneContext>()
																				.select(fields)
																				.table(zoneModule.getTableName())
																				.moduleName(zoneModule.getName())
																				.beanClass(ZoneContext.class);
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
			catch(Exception e) {
				LOGGER.error("Exception occurred ", e);
				throw e;
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
											.andCondition(resourceIdCond);
		
		List<Map<String, Object>> rs = builder.get();
	    return rs;
	}


	public static List<WorkOrderContext> getWorkOrdersCount(long tenantId) throws Exception {
		
		
		FacilioField tenantIdFld = new FacilioField();
		tenantIdFld.setName("tenantId");
		tenantIdFld.setColumnName("TENANT");
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
														  .andCondition(tenantCond);
									
		List<WorkOrderContext> rs = builder.get();
		TicketAPI.loadTicketLookups(rs);
		
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
