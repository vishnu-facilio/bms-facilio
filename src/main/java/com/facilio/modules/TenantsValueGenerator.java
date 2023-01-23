package com.facilio.modules;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class TenantsValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            List<V3TenantContext> tenants = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT, null, V3TenantContext.class, null);
            if(CollectionUtils.isNotEmpty(tenants)) {
                List<Long> tenantIds = tenants.stream().map(V3TenantContext::getId).collect(Collectors.toList());
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
        return "All accessible tenants value generator";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TenantsValueGenerator";
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
    public Criteria getCriteria(FacilioField field,List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if(CollectionUtils.isNotEmpty(value)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.TENANT)), StringUtils.join(value, ","), NumberOperators.EQUALS));
            }
            return criteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
