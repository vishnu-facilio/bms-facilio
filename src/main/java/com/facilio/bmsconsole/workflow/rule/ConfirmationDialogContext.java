package com.facilio.bmsconsole.workflow.rule;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.manager.NamedCriteria;

public class ConfirmationDialogContext {
    private long id = -1;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long parentId = -1;
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

//    private Criteria criteria;
//    @Deprecated
//    public Criteria getCriteria() {
//        return criteria;
//    }
//    @Deprecated
//    public void setCriteria(Criteria criteria) {
//        this.criteria = criteria;
//    }

//    private long criteriaId = -1;
//    @Deprecated
//    public long getCriteriaId() {
//        return criteriaId;
//    }
//    @Deprecated
//    public void setCriteriaId(long criteriaId) {
//        this.criteriaId = criteriaId;
//    }

    private NamedCriteria namedCriteria;
    public NamedCriteria getNamedCriteria() {
        return namedCriteria;
    }
    public void setNamedCriteria(NamedCriteria namedCriteria) {
        this.namedCriteria = namedCriteria;
    }

    private long namedCriteriaId = -1;
    public long getNamedCriteriaId() {
        return namedCriteriaId;
    }
    public void setNamedCriteriaId(long namedCriteriaId) {
        this.namedCriteriaId = namedCriteriaId;
    }
}
