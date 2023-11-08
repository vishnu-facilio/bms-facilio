package com.facilio.readingrule.faultimpact;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.ns.context.AggregationType;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.context.NsFieldType;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaultImpactNameSpaceFieldContext extends V3Context {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long impact;
    ResourceContext resource;

    Long fieldId;
    Long dataInterval;

    AggregationType aggregationTypeEnum;
    boolean enabledCompaction;
    String varName;
    NsFieldType nsFieldTypeEnum;

    public String getAggregationType() {
        if (aggregationTypeEnum != null) {
            return aggregationTypeEnum.getValue();
        }
        return null;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationTypeEnum = AggregationType.valueOf(aggregationType);
    }

    public int getAggregationTypeEnum() {
        if (aggregationTypeEnum != null) {
            return aggregationTypeEnum.getIndex();
        }
        return -1;
    }

    public void setAggregationTypeEnum(int aggregationType) {
        this.aggregationTypeEnum = AggregationType.valueOf(aggregationType);
    }

    public int getNsFieldTypeEnum() {
        if (nsFieldTypeEnum != null) {
            return nsFieldTypeEnum.getIndex();
        }
        return -1;
    }

    public void setNsFieldTypeEnum(int nsFieldType) {
        this.nsFieldTypeEnum = NsFieldType.valueOf(nsFieldType);
    }

    public String getNsFieldType() {
        if (nsFieldTypeEnum != null) {
            return nsFieldTypeEnum.getValue();
        }
        return null;
    }

    public void setNsFieldType(String nsFieldType) {
        this.nsFieldTypeEnum = NsFieldType.valueOf(nsFieldType);
    }

    public NameSpaceField getNameSpaceField() {
        NameSpaceField field = new NameSpaceField();
        field.setFieldId(getFieldId());
        field.setDataInterval(getDataInterval());
        if (getResource() != null) {
            field.setResourceId(getResource().getId());
        } else {
            field.setResourceId(-1l);
        }
        field.setVarName(getVarName());
        field.setAggregationTypeI(getAggregationTypeEnum());
        field.setNsFieldTypeI(getNsFieldTypeEnum());
        return field;
    }

    public static FaultImpactNameSpaceFieldContext getImpactNsFieldFromNsFld(NameSpaceField nsFld) {
        FaultImpactNameSpaceFieldContext impactNsFld = new FaultImpactNameSpaceFieldContext();
        impactNsFld.setFieldId(nsFld.getFieldId());
        impactNsFld.setModuleId(nsFld.getModuleId());
        impactNsFld.setDataInterval(nsFld.getDataInterval());
        if (nsFld.getResourceId() != null) {
            nsFld.setResourceId(nsFld.getResourceId());
        }
        impactNsFld.setVarName(nsFld.getVarName());
        impactNsFld.setAggregationTypeEnum(nsFld.getAggregationTypeI());
        impactNsFld.setNsFieldTypeEnum(nsFld.getNsFieldTypeI());
        return impactNsFld;
    }
}
