package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AltayerTenantUserValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        if(appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
            try {
                List<Long> ids = AltayerTenantSiteValueGenerator.getTenantContactForUser(AccountUtil.getCurrentUser().getId());
                if(CollectionUtils.isNotEmpty(ids)) {
                    return StringUtils.join(ids, ",");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Tenant(s) For Logged In User";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerTenantUserValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.USERS;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }
}
