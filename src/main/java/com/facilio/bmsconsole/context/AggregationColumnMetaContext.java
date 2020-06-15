package com.facilio.bmsconsole.context;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.io.Serializable;

public class AggregationColumnMetaContext implements Serializable {

    private Long id;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private Long aggregationMetaId;
    public Long getAggregationMetaId() {
        return aggregationMetaId;
    }
    public void setAggregationMetaId(Long aggregationMetaId) {
        this.aggregationMetaId = aggregationMetaId;
    }

    public AggregationMetaContext aggregationMeta;
    public AggregationMetaContext getAggregationMeta() {
        return aggregationMeta;
    }
    public void setAggregationMeta(AggregationMetaContext aggregationMeta) {
        this.aggregationMeta = aggregationMeta;
    }

    private FacilioModule module;
    public FacilioModule getModule() {
        return module;
    }
    public void setModule(FacilioModule module) {
        this.module = module;
    }

    private Long moduleId;
    public Long getModuleId() {
        return moduleId;
    }
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    private FacilioField field;
    public FacilioField getField() {
        return field;
    }
    public void setField(FacilioField field) {
        this.field = field;
    }

    private Long fieldId;
    public Long getFieldId() {
        return fieldId;
    }
    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    private AggregateOperator aggregateOperator;
    public Integer getAggregateOperator() {
        if (aggregateOperator != null) {
            return aggregateOperator.getValue();
        }
        return null;
    }
    public void setAggregateOperator(Integer aggregateOperator) {
        if (aggregateOperator != null) {
            this.aggregateOperator = AggregateOperator.getAggregateOperator(aggregateOperator);
        }
    }
    public AggregateOperator getAggregateOperatorEnum() {
        return aggregateOperator;
    }
    public void setAggregateOperatorEnum(AggregateOperator aggregateOperator) {
        this.aggregateOperator = aggregateOperator;
    }

    private Long storageFieldId;
    public Long getStorageFieldId() {
        return storageFieldId;
    }
    public void setStorageFieldId(Long storageFieldId) {
        this.storageFieldId = storageFieldId;
    }

    private FacilioField storageField;
    public FacilioField getStorageField() {
        return storageField;
    }
    public void setStorageField(FacilioField storageField) {
        this.storageField = storageField;
    }
}
