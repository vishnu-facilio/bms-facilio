package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ScopeHandlerImpl extends ScopeHandler {

    /*
     * Having separate methods because there might be need to handle scope based on select or insert.
     * e.g : Even though main app has site scope, it shouldn't be handled during site insertion
     * No separate method for delete because delete is handled via select
    */

    /*
        Insert will be called for each module separately
        e.g : if we are inserting WO, first this method will be called for Ticket, then for WO and so on
        Can be used add default value for scope variables
     */
    @Override
    public Collection<FacilioField> updateValuesForInsertAndGetFields(FacilioModule module, List<Map<String, Object>> props) {
        if (FieldUtil.isSiteIdFieldPresent(module, true)) {
            return Collections.singletonList(FieldFactory.getSiteIdField(module));
        }
        return null;
    }

    @Override
    public ScopeFieldsAndCriteria updateValuesForUpdateAndGetFieldsAndCriteria (FacilioModule module, Collection<FacilioModule> joinModules, Map<String, Object> prop) {
        if (FieldUtil.isSiteIdFieldPresent(module)) {
            long currentSiteId = AccountUtil.getCurrentSiteId();
            if (currentSiteId > 0) {
                prop.remove("siteId"); //Not allowing to change site id if it's scope is set.
            }
            return constructSiteFieldsAndCriteria(module, true);
        }
        return null;
    }

    @Override
    public ScopeFieldsAndCriteria getFieldsAndCriteriaForSelect(FacilioModule module, Collection<FacilioModule> joinModules) {
        if (FieldUtil.isSiteIdFieldPresent(module)) {
            return constructSiteFieldsAndCriteria(module, false);
        }
        return null;
    }

    private ScopeFieldsAndCriteria constructSiteFieldsAndCriteria (FacilioModule module, boolean isUpdate) {
        FacilioField siteIdField = FieldFactory.getSiteIdField(module);
        long currentSiteId = AccountUtil.getCurrentSiteId();
        Criteria criteria = null;
        if (currentSiteId > 0) {// Currently we are handling site id criteria only for the module and not for all parent modules
            criteria = new Criteria();
            Condition siteCondition = CriteriaAPI.getCondition(siteIdField, String.valueOf(currentSiteId), NumberOperators.EQUALS);
            criteria.addAndCondition(siteCondition);
        }
        List<FacilioField> fields = null;
        if (isUpdate) { //Will allow site id to be updated only if current site is -1. Also site fields of all parent modules needs to be added in fields because those have to be updated too
            if (currentSiteId == -1) {
                fields = new ArrayList<>();
                fields.add(siteIdField);
                FacilioModule extendedModule = module.getExtendModule();
                while(extendedModule != null) {
                    if (FieldUtil.isSiteIdFieldPresent(extendedModule)) {
                        fields.add(FieldFactory.getSiteIdField(extendedModule));
                    }
                    extendedModule = extendedModule.getExtendModule();
                }
            }
        }
        else {
            fields = Collections.singletonList(siteIdField);
        }
        return ScopeFieldsAndCriteria.of(fields, criteria);
    }
}
