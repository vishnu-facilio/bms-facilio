package com.facilio.modules;

import java.util.ArrayList; 
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;

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
				List<BaseSpaceContext> sites = CommonCommandUtil.getMySites();
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

}
