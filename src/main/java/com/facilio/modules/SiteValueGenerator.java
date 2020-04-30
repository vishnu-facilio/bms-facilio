package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
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
			Long currentSiteId = (Long)AccountUtil.getGlobalScopingFieldValue("siteId");
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
				TenantContext tenant = PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId());
				if(tenant != null) {
					//for now one to one mapping for tenant -> sites.ll change in future.
					return String.valueOf(tenant.getSiteId());
				}
			}
			else if(appType == AppDomainType.SERVICE_PORTAL.getIndex()) {
				
			}
			else if(appType == AppDomainType.CLIENT_PORTAL.getIndex()) {
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
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

}
