package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;

import java.util.List;

@Log4j
public class PeopleValueGenerator extends ValueGenerator {
    @Override
    public String generateValueForCondition(int appType) {
        try {
            Long pplId = V3PeopleAPI.getPeopleIdForUser(AccountUtil.getCurrentUser().getId());
            return String.valueOf(pplId);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.PEOPLE;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.PeopleValueGenerator";
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
    public Criteria getCriteria(FacilioField field,List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria resultCriteria = new Criteria();
            resultCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(getModuleName())),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()), NumberOperators.EQUALS));
            return resultCriteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}
