package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.RelatedModuleOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;

import java.util.List;
@Log4j
public class ClientValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            V3ClientContext client = V3PeopleAPI.getClientForUser(AccountUtil.getCurrentUser().getId(), true);
            if (client != null) {
                return String.valueOf(client.getId());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.CLIENT;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.ClientValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.CLIENT;
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
    public Criteria getCriteria(FacilioField field, List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField clientField = modBean.getField("client",FacilioConstants.ContextNames.CLIENT_CONTACT);

            Criteria clientcontactCriteria = new Criteria();
            clientcontactCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT)),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()), NumberOperators.EQUALS));
            Criteria clientCriteria = new Criteria();
            clientCriteria.addAndCondition(CriteriaAPI.getCondition(clientField,clientcontactCriteria, RelatedModuleOperator.RELATED));

            return clientCriteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}

