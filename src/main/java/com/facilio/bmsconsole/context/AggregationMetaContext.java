package com.facilio.bmsconsole.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;

import java.io.Serializable;
import java.util.List;

public class AggregationMetaContext implements Serializable {

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private FacilioModule storageModule;
    public FacilioModule getStorageModule() {
        return storageModule;
    }
    public void setStorageModule(FacilioModule storageModule) {
        this.storageModule = storageModule;
    }

    private Long storageModuleId;
    public Long getStorageModuleId() {
        return storageModuleId;
    }
    public void setStorageModuleId(Long storageModuleId) {
        this.storageModuleId = storageModuleId;
    }

    private Long criteriaId;
    public Long getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    private Criteria criteria;
    public Criteria getCriteria() {
        return criteria;
    }
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    private List<AggregationColumnMetaContext> columnList;
    public List<AggregationColumnMetaContext> getColumnList() {
        return columnList;
    }
    public void setColumnList(List<AggregationColumnMetaContext> columnList) {
        this.columnList = columnList;
    }

    private BmsAggregateOperators.DateAggregateOperator aggregateOperator;
    public Integer getAggregateOperator() {
        if (aggregateOperator == null) {
            return null;
        }
        return aggregateOperator.getValue();
    }
    public void setAggregateOperator(Integer operator) {
        if (operator != null) {
            AggregateOperator aggregateOperator = BmsAggregateOperators.getAggregateOperator(operator);
            if (aggregateOperator instanceof BmsAggregateOperators.DateAggregateOperator) {
                this.aggregateOperator = (BmsAggregateOperators.DateAggregateOperator) aggregateOperator;
            }
        }
    }
    public BmsAggregateOperators.DateAggregateOperator getAggregateOperatorEnum() {
        return aggregateOperator;
    }
    public void setAggregateOperator(BmsAggregateOperators.DateAggregateOperator aggregateOperator) {
        this.aggregateOperator = aggregateOperator;
    }

    private Long interval;
    public Long getInterval() {
        return interval;
    }
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    private Long lastSync;
    public Long getLastSync() {
        return lastSync;
    }
    public void setLastSync(Long lastSync) {
        this.lastSync = lastSync;
    }
}
