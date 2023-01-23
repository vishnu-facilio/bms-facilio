package com.facilio.modules;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AltayerTenantBasedOnTenantUnit extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Collection<SupplementRecord> supplementFields = new ArrayList<>();
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            supplementFields.add((LookupField) fieldsMap.get("tenant"));
            if(AccountUtil.getCurrentAccount() != null && AccountUtil.getCurrentAccount().getUser() != null) {
                User currentUser = AccountUtil.getCurrentAccount().getUser();
                if(currentUser.getAccessibleSpace() == null) {
                        List<V3TenantContext> tenants = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT, null, V3TenantContext.class, null,null,null,null,true);
                        if(CollectionUtils.isNotEmpty(tenants)) {
                            List<Long> tenantIds = tenants.stream().map(V3TenantContext::getId).collect(Collectors.toList());
                            return StringUtils.join(tenantIds,',');
                        }
                }
            }
            List<V3TenantUnitSpaceContext> tenantUnits = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT_UNIT_SPACE, null, V3TenantUnitSpaceContext.class, supplementFields);
            if(CollectionUtils.isNotEmpty(tenantUnits)) {
                List<Long> tenantIds = tenantUnits.stream().filter(unit -> unit.getTenant() != null && unit.getTenant().getId() > 0).map(V3TenantUnitSpaceContext::getTenant).map(V3TenantContext::getId).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(tenantIds)) {
                    return StringUtils.join(tenantIds,',');
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
        return "Altayer Tenant based on accessible tenant units";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerTenantBasedOnTenantUnit";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TENANT;
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
