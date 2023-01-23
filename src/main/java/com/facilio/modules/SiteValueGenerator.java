package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class SiteValueGenerator extends ValueGenerator {

	@Override
	public String generateValueForCondition(int appType) {
		List<Object> values = new ArrayList<Object>(); 
		try {
			Long currentSiteId = (Long)AccountUtil.getSwitchScopingFieldValue("siteId");
			if(AccountUtil.getShouldApplySwitchScope() && currentSiteId != null && currentSiteId > 0) {
				values.add(String.valueOf(currentSiteId));
				return StringUtils.join(values, ",");
			}
			else if(appType == AppDomainType.FACILIO.getIndex()) {
				List<BaseSpaceContext> sites = getMySites();
				if(CollectionUtils.isNotEmpty(sites)) {
					for(BaseSpaceContext site : sites) {
						values.add(site.getId());
					}
					return StringUtils.join(values, ",");
				}
			}
			else if(appType == AppDomainType.TENANT_PORTAL.getIndex()) {
				V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
				if(tenant != null) {
					if(tenant.getSiteId() > 0) {
						return String.valueOf(tenant.getSiteId());
					}
					//temp handling for multi site for tenant (altayer 407)
					else {
						List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
						if(CollectionUtils.isNotEmpty(tenantUnits)) {
							List<Long> siteIDs = new ArrayList<>();
							for (TenantUnitSpaceContext tu :tenantUnits) {
								siteIDs.add(tu.getSiteId());
							}
							return StringUtils.join(siteIDs, ",");
						}
					}
				}
			}
			else if(appType == AppDomainType.CLIENT_PORTAL.getIndex()) {
				
			}
			else if(appType == AppDomainType.SERVICE_PORTAL.getIndex()) {
				V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
				if(tenant != null){
					if(tenant.getSiteId() > 0) {
						return String.valueOf(tenant.getSiteId());
					}
					//temp handling for multi site for tenant (altayer 407)
					else {
						List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
						if(CollectionUtils.isNotEmpty(tenantUnits)) {
							List<Long> siteIDs = new ArrayList<>();
							for (TenantUnitSpaceContext tu :tenantUnits) {
								siteIDs.add(tu.getSiteId());
							}
							return StringUtils.join(siteIDs, ",");
						}
					}
				}
				else {
					List<SiteContext> sites = SpaceAPI.getAllSites(false,null,null,true);
					if (CollectionUtils.isNotEmpty(sites)) {
						for (SiteContext site : sites) {
							values.add(site.getId());
						}
						return StringUtils.join(values, ",");
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public String getValueGeneratorName() {
		return FacilioConstants.ContextNames.ValueGenerators.SITE;
	}

	@Override
	public String getLinkName() {
		return "com.facilio.modules.SiteValueGenerator";
	}

	@Override
	public String getModuleName() {
		return FacilioConstants.ContextNames.SITE;
	}

	@Override
	public Boolean getIsHidden() {
		return false;
	}


	private List<BaseSpaceContext> getMySites() throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			List<Long> siteIds = new ArrayList<>();
			
			if (AccountUtil.getCurrentUser() != null) {
				FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
				GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
						.select(AccountConstants.getAccessbileSpaceFields())
						.table(accessibleSpaceMod.getTableName())
						.andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
				
				List<Map<String, Object>> props = selectAccessibleBuilder.get();
				
				if (props != null && !props.isEmpty()) {
					for(Map<String, Object> prop : props) {
						Long siteId = (Long) prop.get("siteId");
						if (siteId != null) {
							siteIds.add(siteId);
						}
					}
				}
			}
			
			SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
					.select(fields)
					.module(module)
					.skipScopeCriteria()
					.beanClass(BaseSpaceContext.class)
					.andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType", String.valueOf(SpaceType.SITE.getIntVal()), NumberOperators.EQUALS));
			
			List<BaseSpaceContext> accessibleBaseSpace;
			if (siteIds.isEmpty()) {
				accessibleBaseSpace = selectBuilder.get();
			} else {
				accessibleBaseSpace = selectBuilder.andCondition(CriteriaAPI.getIdCondition(siteIds, module)).get();
			}
			
			if (accessibleBaseSpace == null || accessibleBaseSpace.isEmpty()) {
				return Collections.emptyList();
			}
			
			return accessibleBaseSpace;
		}

	@Override
	public Integer getOperatorId() {
		return 36;
	}

	@Override
	public Criteria getCriteria(FacilioField field,List<Long> value) {
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Criteria criteria = new Criteria();
			if(value != null) {
				criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(getModuleName())), StringUtils.join(value, ","), NumberOperators.EQUALS));
			}
			return criteria;
		} catch (Exception e) {

		}
		return null;
	}
}
