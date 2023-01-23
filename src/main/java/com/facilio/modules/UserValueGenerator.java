package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public class UserValueGenerator extends ValueGenerator{


	public Criteria getCriteria(FacilioField field, List<Long> value) {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(field,String.valueOf(AccountUtil.getCurrentUser().getOuid()), NumberOperators.EQUALS));
		return criteria;
	}

	@Override
	public String generateValueForCondition(int appType) {
		try {
			if(appType == AppDomainType.FACILIO.getIndex()) {
				return String.valueOf(AccountUtil.getCurrentUser().getId());
			}
			else if(appType == AppDomainType.TENANT_PORTAL.getIndex()) {
				V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
				if(tenant != null) {
					return String.valueOf(tenant.getId());
				}
			}
			else if(appType == AppDomainType.VENDOR_PORTAL.getIndex()) {
				V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId(), true);
				if(vendor != null) {
					return String.valueOf(vendor.getId());
				}
			}
			else if(appType == AppDomainType.CLIENT_PORTAL.getIndex()) {

			}
			else if(appType == AppDomainType.SERVICE_PORTAL.getIndex()) {
				if(AccountUtil.getCurrentUser() != null) {
					return String.valueOf(AccountUtil.getCurrentUser().getOuid());
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
		return FacilioConstants.ContextNames.ValueGenerators.USER;
	}

	@Override
	public String getLinkName() {
		return "com.facilio.modules.UserValueGenerator";
	}

	@Override
	public String getModuleName() {
		return FacilioConstants.ContextNames.USERS;
	}

	@Override
	public Boolean getIsHidden() {
		return false;
	}

	@Override
	public Integer getOperatorId() {
		return 36;
	}

}
