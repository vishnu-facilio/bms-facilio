package com.facilio.modules;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ScopeHandler { //Having separate abstract class because it should be easier when we move these files to fw
    private static final ScopeHandler DEFAULT_HANDLER = new ScopeHandlerImpl(); //Will move this to conf file when we move this to fw
    public static ScopeHandler getInstance() {
        return DEFAULT_HANDLER;
    }

    //Having separate methods because there might be need to handle scope based on select or insert.
    // e.g : Even though main app has site scope, it shouldn't be handled during site insertion
    // No separate method for delete because delete is handled via select

    public abstract Collection<FacilioField> updateValuesForInsertAndGetFields (FacilioModule module, List<Map<String, Object>> props);

    public abstract ScopeFieldsAndCriteria updateValuesForUpdateAndGetFieldsAndCriteria (FacilioModule module, Collection<FacilioModule> joinModules, Map<String, Object> prop);

    public abstract ScopeFieldsAndCriteria getFieldsAndCriteriaForSelect(FacilioModule module, Collection<FacilioModule> joinModules);

    public static class ScopeFieldsAndCriteria {
        private final Criteria criteria;
        private final Collection<FacilioField> fields;

        private ScopeFieldsAndCriteria (Collection<FacilioField> fields, Criteria criteria) {
            this.fields = fields;
            this.criteria = criteria;
        }

        public static ScopeFieldsAndCriteria of (Collection<FacilioField> fields, Criteria criteria) {
            return new ScopeFieldsAndCriteria(fields, criteria);
        }

        public Collection<FacilioField> getFields() {
            return fields;
        }
        public Criteria getCriteria() {
            return criteria;
        }
    }
}
