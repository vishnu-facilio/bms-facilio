package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class AccessibleBasespaceValueGenerator extends ValueGenerator {

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if (CollectionUtils.isNotEmpty(value)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE)), StringUtils.join(value, ","), NumberOperators.EQUALS));
            }
            return criteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
                return getAccessibleSites();
            } else if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    if (tenant.getSiteId() > 0) {
                        return String.valueOf(tenant.getSiteId());
                    }
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
            } else if (appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        List<Long> siteIDs = new ArrayList<>();
                        for (TenantUnitSpaceContext tu : tenantUnits) {
                            siteIDs.add(tu.getSiteId());
                        }
                        return StringUtils.join(siteIDs, ",");
                    }
                } else {
                    return getAccessibleSites();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getAccessibleSites() throws Exception {
        FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
        GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getAccessbileSpaceFields())
                .table(accessibleSpaceMod.getTableName())
                .andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
        List<Map<String, Object>> props = null;
        props = selectAccessibleBuilder.get();
        List<Long> baseSpaceIds = new ArrayList<Long>();
        if (props != null && !props.isEmpty()) {
            for (Map<String, Object> prop : props) {
                Long bsId = (Long) prop.get("bsid");
                Long siteId = (Long) prop.get("siteId");
                if (bsId != null && siteId != null) {
                    baseSpaceIds.add(bsId);
                    if(!baseSpaceIds.contains(siteId)) {
                        baseSpaceIds.add(siteId);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(baseSpaceIds)) {
            return StringUtils.join(baseSpaceIds, ",");
        }
        return FacilioConstants.ContextNames.ALL_VALUE;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.ACCESSIBLE_SPACES;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AccessibleBasespaceValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BASE_SPACE;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 38;
    }

}
