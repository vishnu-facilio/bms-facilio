package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PeopleListValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if(appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
                List<V3PeopleContext> ppl = V3PeopleAPI.getAllPeople();
                if(CollectionUtils.isNotEmpty(ppl)) {
                    List<Long> idList = new ArrayList<>();
                    for(V3PeopleContext people : ppl) {
                        idList.add(people.getId());
                    }
                    return StringUtils.join(idList, ",");
                }
            }
            else if(appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                List<Long> tenantContactIds = V3PeopleAPI.getTenantContactsIdsForLoggedInTenantUser(AccountUtil.getCurrentUser().getId());
                if(CollectionUtils.isNotEmpty(tenantContactIds)) {
                    return StringUtils.join(tenantContactIds, ",");
                }
            }
            else if(appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                List<Long> tenantContactIds = V3PeopleAPI.getTenantContactsIdsForLoggedInTenantUser(AccountUtil.getCurrentUser().getId());
                if(CollectionUtils.isNotEmpty(tenantContactIds)) {
                    return StringUtils.join(tenantContactIds, ",");
                }
                else {
                    Long pplId = V3PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
                    return String.valueOf(pplId);
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
        return FacilioConstants.ContextNames.ValueGenerators.PEOPLE_LIST;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.PeopleListValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.PEOPLE;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
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
