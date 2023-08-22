package com.facilio.db.criteria.manager;

import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class NamedCondition {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long namedCriteriaId = -1l;
    public long getNamedCriteriaId() {
        return namedCriteriaId;
    }
    public void setNamedCriteriaId(long namedCriteriaId) {
        this.namedCriteriaId = namedCriteriaId;
    }

    private int sequence = -1;
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    private WorkflowContext workflowContext;
    public WorkflowContext getWorkflowContext() throws Exception {
        if (workflowContext == null) {
            if (workflowId > 0) {
                workflowContext = WorkflowUtil.getWorkflowContext(workflowId);
            }
        }
        return workflowContext;
    }
    public void setWorkflowContext(WorkflowContext workflowContext) {
        this.workflowContext = workflowContext;
    }

    private int systemCriteriaId = -1;
    public int getSystemCriteriaId() {
        return systemCriteriaId;
    }
    public void setSystemCriteriaId(int systemCriteriaId) {
        this.systemCriteriaId = systemCriteriaId;
    }

    public boolean evaluate(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        switch (type) {
            case CRITERIA:
                return getCriteria().computePredicate(placeHolders).evaluate(record);

            case WORKFLOW:
                Object result = getWorkflowContext().executeWorkflow();
                if (result != null && result instanceof Boolean) {
                    return (Boolean) result;
                }
                break;

            case SYSTEM_FUNCTION:
                SystemCriteria systemCriteria = SystemCriteria.getSystemCriteria(systemCriteriaId);
                return systemCriteria.evaluate(record, null, (FacilioContext) context);
        }
        return false;
    }

    public void validateAndAddChildren() throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty for named condition");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type in named criteria cannot be empty");
        }

        switch (type) {
            case CRITERIA:
                if (getCriteria() == null || getCriteria().isEmpty()) {
                    throw new IllegalArgumentException("Criteria cannot be empty");
                }
                setCriteriaId(CriteriaAPI.addCriteria(getCriteria()));
                break;

            case WORKFLOW:
                if (getWorkflowContext() == null) {
                    throw new IllegalArgumentException("Workflow cannot be empty");
                }
                Long workflowId = getWorkflowContext().getId();
                if(workflowId!=null && workflowId > 0){
                    WorkflowUtil.updateWorkflow(getWorkflowContext(), workflowId);
                    setWorkflowId(workflowId);
                }
                else{
                    setWorkflowId(WorkflowUtil.addWorkflow(getWorkflowContext()));
                }
                break;

            case SYSTEM_FUNCTION:
                if (systemCriteriaId < 0) {
                    throw new IllegalArgumentException("System function id cannot be empty");
                }
                break;
        }
    }

    public enum Type implements FacilioIntEnum {
        CRITERIA("Criteria"),
        WORKFLOW("Workflow"),
        SYSTEM_FUNCTION("Sys Func");

        private String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
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
