package com.facilio.modules;


import com.facilio.beans.ModuleBean;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

import com.facilio.db.criteria.operators.PeopleOperator;
import com.facilio.db.criteria.operators.RelatedModuleOperator;

import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

import lombok.extern.log4j.Log4j;



import java.util.List;

@Log4j
public class TerritoryBasedOnPeopleValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.CURRENT_PEOPLE_TERRITORIES;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.TerritoryBasedOnPeopleValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.Territory.TERRITORY;
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

            Criteria parentCriteriaForRel = new Criteria();

            Criteria relModCriteria = new Criteria();
            FacilioField peopleField = modBean.getField("left","peopleTerritory");
            relModCriteria.addAndCondition(CriteriaAPI.getCondition(peopleField,(String) null, PeopleOperator.CURRENT_USER));

            FacilioField territoryField = modBean.getField("right","peopleTerritory");
            parentCriteriaForRel.addAndCondition(CriteriaAPI.getCondition(territoryField,relModCriteria,RelatedModuleOperator.RELATED));

            return parentCriteriaForRel;

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

}
