package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.scriptengine.context.Value;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AltayerTenantSiteValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                List<Long> ids = getTenantContactForUser(AccountUtil.getCurrentUser().getId());
                if(CollectionUtils.isNotEmpty(ids)) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenantList(ids);
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        List<Long> siteIDs = new ArrayList<>();
                        for (TenantUnitSpaceContext tu : tenantUnits) {
                            siteIDs.add(tu.getSiteId());
                        }
                        return StringUtils.join(siteIDs, ",");
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Altayer Tenant Sites";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerTenantSiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SITE;
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

    public static List<Long> getTenantContactForUser(long ouId) throws Exception {
        long pplId = V3PeopleAPI.getPeopleIdForUser(ouId);
        if (pplId <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid People Id mapped with ORG_User");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tenantContactModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> fields = modBean.getAllFields(tenantContactModule.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        MultiLookupMeta servingTenants = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("tenantmulti"));
        fetchLookupsList.add(servingTenants);
        List<V3TenantContactContext> tenantcontacts = V3RecordAPI.getRecordsListWithSupplements(tenantContactModule.getName(), Collections.singletonList(pplId), V3TenantContactContext.class, null, fetchLookupsList, null, null, true );
        if(CollectionUtils.isNotEmpty(tenantcontacts)) {
            V3TenantContactContext tc = tenantcontacts.get(0);
            List<Long> tenantIds = new ArrayList<>();
            Map<String, Object> map = FieldUtil.getAsProperties(tc);
            if (map.containsKey("tenantmulti")) {

                List<V3TenantContext> tenantsServing = FieldUtil.getAsBeanListFromMapList((List<Map<String, Object>>) map.get("tenantmulti"), V3TenantContext.class);
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(tenantsServing)) {
                    for (V3TenantContext t : tenantsServing) {
                        tenantIds.add(t.getId());
                    }
                }
            } else if (tc.getTenant() != null) {
                tenantIds.add(tc.getTenant().getId());
            }
            return tenantIds;
        }
        return null;
    }

}
