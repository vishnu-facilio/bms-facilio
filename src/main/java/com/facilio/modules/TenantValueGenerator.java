package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.RelatedModuleOperator;
import com.facilio.db.criteria.operators.UserOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Log4j
public class TenantValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
            if (tenant != null) {
                return String.valueOf(tenant.getId());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.TENANT;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TenantValueGenerator";
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
            FacilioField tenantField = modBean.getField("tenant",FacilioConstants.ContextNames.TENANT_CONTACT);

            Criteria tenantcontactCriteria = new Criteria();
            tenantcontactCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT)),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()),NumberOperators.EQUALS));
            Criteria tenantCriteria = new Criteria();
            tenantCriteria.addAndCondition(CriteriaAPI.getCondition(tenantField,tenantcontactCriteria, RelatedModuleOperator.RELATED));

            return tenantCriteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
