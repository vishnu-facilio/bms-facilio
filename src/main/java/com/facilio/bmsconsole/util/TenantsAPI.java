package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.DashboardAction;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.RateCardServiceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantUserContext;
import com.facilio.bmsconsole.tenant.UtilityAsset;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;


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
	
	public static List<TenantContext> getAllTenants() throws Exception {
		FacilioModule module = ModuleFactory.getTenantsModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
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
//														.andCondition(CriteriaAPI.getCondition(field, operator)(id, module));
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<TenantUserContext> tenantUsers = new ArrayList<>();
		
//		tenant.getContactId();
//		
//		TenantUserContext tenantUserContext = new TenantUserContext();
//		
//		tenantUserContext.setOrgid(AccountUtil.getCurrentOrg().getId());
//		tenantUserContext.setOuid(tenant.getContactId());
//		tenantUserContext.setTenantId(id);
//		tenantUserContext.setOrgUser((User)AccountUtil.getUserBean().getUser(tenant.getContactId()));
//		
//		tenantUsers.add(tenantUserContext);
		
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
		

				
//		List<String,Object> props ;	
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
		
		FacilioModule module = ModuleFactory.getTenantsModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getTenantsFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<TenantContext> tenants = getTenantsFromProps(selectBuilder.get(), fetchTenantOnly);
		if (tenants != null && !tenants.isEmpty()) {
			return tenants.get(0);
		}
		return null;
	}
	
	private static List<TenantContext> getTenantsFromProps(List<Map<String, Object>> props, Boolean...fetchTenantOnly) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<TenantContext> tenants = new ArrayList<>();
			List<Long> ids = new ArrayList<>();
			List<Long> spaceIds = new ArrayList<>();
			boolean fetchExtendedProps = fetchTenantOnly.length == 0 || fetchTenantOnly[0];
			for (Map<String, Object> prop : props) {
				TenantContext tenant = FieldUtil.getAsBeanFromMap(prop, TenantContext.class);
				tenants.add(tenant);
				if (!fetchExtendedProps) {
					continue;
				}
				ids.add(tenant.getId());
				spaceIds.add(tenant.getSpaceId());
				if (tenant.getLogoId()  != -1) {
					FileStore fs = FileStoreFactory.getInstance().getFileStore();
					tenant.setLogoUrl(fs.getPrivateUrl(tenant.getLogoId()));
				}
			}
			if (!fetchExtendedProps) {
				return tenants;
			}
			Map<Long, List<UtilityAsset>> utilMap = getUtilityAssets(ids);
			Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(spaceIds);
			if (utilMap != null && !utilMap.isEmpty()) {
				for (TenantContext tenant : tenants) {
					tenant.setUtilityAssets(utilMap.get(tenant.getId()));
					tenant.setSpace(spaceMap.get(tenant.getSpaceId()));
					if (tenant.getContactId() > 0) {
						tenant.setContactInfo(AccountUtil.getUserBean().getUser(tenant.getContactId()));
					}
				}
			}
			return tenants;
		}
		return null;
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
	
	@SuppressWarnings("unused")
//	public static TenantContext addTenant (TenantContext tenant) throws Exception {
	public static long addTenant (TenantContext tenant) throws Exception {
		
		if (tenant.getName() == null || tenant.getName().isEmpty()) {
			throw new IllegalArgumentException("Invalid name during addition of Tenant");
		}
		
		if (tenant.getSpaceId() == -1) {
			throw new IllegalArgumentException("Invalid space id during addition of Tenant");
		}
		
		 addTenantLogo(tenant);
		 
		 
		
		
		
//		List<User> users = tenant.getContactInfo();
			User use = tenant.getContactInfo();
		Organization org = AccountUtil.getOrgBean().getPortalOrg(AccountUtil.getCurrentOrg().getDomain());
		use.setPortalId(org.getPortalId());
		User extRequester = AccountUtil.getUserBean().getPortalUsers(use.getEmail(), use.getPortalId());
		if (extRequester != null) {
			tenant.setContactId(extRequester.getOuid());
		}
		else {
			long requesterId = AccountUtil.getUserBean().addRequester(AccountUtil.getCurrentOrg().getOrgId(), use);
			tenant.setContactId(requesterId);
		}
		
//		for(User user : users) {
			
//			user.setPortalId(org.getPortalId());
//		User extRequester = AccountUtil.getUserBean().getPortalUsers(user.getEmail(), user.getPortalId());
//		if (extRequester != null) {
//			user.setContactId(extRequester.getOuid());
//		}
//		else {
//			long requesterId = AccountUtil.getUserBean().addRequester(AccountUtil.getCurrentOrg().getOrgId(), user);
//			contactId.setContactId(requesterId);
//		}
		
//		}
		
		tenant.setOrgId(AccountUtil.getCurrentOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getTenantsModule().getTableName())
														.fields(FieldFactory.getTenantsFields());
		long id = insertBuilder.insert(FieldUtil.getAsProperties(tenant));
		tenant.setId(id);
		addUtilityMapping(tenant);
		return id;
//		return tenant;
	}
	
	private static void addUtilityMapping(TenantContext tenant) throws Exception {
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
	
	private static void addTenantLogo(TenantContext tenant) throws Exception {
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
		
		FacilioModule module = ModuleFactory.getTenantsModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getTenantsFields())
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
	
	public static int deleteTenant (long id) throws Exception {
		
		if (id == -1) {
			throw new IllegalArgumentException("Invalid ID during deletion of tenant");
		}
		
		TenantContext oldTenant = getTenant(id, true);

		FacilioModule module = ModuleFactory.getTenantsModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		int count = deleteBuilder.delete();
		deleteTenantLogo(oldTenant.getLogoId());
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
