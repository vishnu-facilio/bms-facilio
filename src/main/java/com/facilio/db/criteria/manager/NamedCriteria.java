package com.facilio.db.criteria.manager;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioEnum;
import com.facilio.workflowv2.util.WorkflowV2API;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class NamedCriteria {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long orgId = -1L;
    public long getOrgId() {
        return orgId;
    }
    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public void setType(Type type) {
        this.type = type;
    }

    private long criteriaId = -1l;
    public long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(long criteriaId) {
        this.criteriaId = criteriaId;
    }

    private Criteria criteria;
    public Criteria getCriteria() throws Exception {
        if (criteria == null) {
            if (criteriaId > 0) {
                criteria = CriteriaAPI.getCriteria(criteriaId);
            }
        }
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private long workflowId = -1;
    public long getWorkflowId() {
        return workflowId;
    }
    public void setWorkflowId(long workflowId) {
        this.workflowId = workflowId;
    }

    public boolean evaluate(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        switch (type) {
            case CRITERIA:
                return getCriteria().computePredicate(placeHolders).evaluate(record);

            case WORKFLOW:

        }
        return false;
    }

    public void validate() {
        if (type == null) {
            throw new IllegalArgumentException("Type in named criteria cannot be empty");
        }

        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty for named criteria");
        }

        switch (type) {
            case CRITERIA:
                if (criteria == null || criteria.isEmpty()) {
                    throw new IllegalArgumentException("Criteria cannot be empty");
                }
                break;
        }
    }

    public enum Type implements FacilioEnum {
        CRITERIA("Criteria"),
        WORKFLOW("Workflow");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static Type valueOf(int index) {
            if (index >= 1 && index <= Type.values().length) {
                return Type.values()[index - 1];
            }
            return null;
        }
    }
}
