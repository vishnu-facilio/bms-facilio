package com.facilio.bmsconsoleV3.context.meter;

import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceCategory;
import com.facilio.connected.ResourceType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualMeterTemplateReadingContext extends V3Context implements IConnectedRule {

	VirtualMeterTemplateContext virtualMeterTemplate;
	Long readingFieldId;
	private NamespaceFrequency frequency;
	private NameSpaceContext ns;
    private Boolean status;
    int resourceType;
    ResourceCategory<? extends V3Context> category;
    ResourceType resourceTypeEnum=ResourceType.METER_CATEGORY;

    public NamespaceFrequency getFrequencyEnum() {
        return this.frequency;
    }

    public void setFrequency(Integer type) {
        this.frequency = NamespaceFrequency.valueOf(type);
    }

    public int getFrequency() {
        return frequency != null ? frequency.getIndex() : -1;
    }

    public Boolean getStatus() {
        return status!=null ? status:Boolean.FALSE;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return virtualMeterTemplate.getName() != null ? virtualMeterTemplate.getName() : null;
    }

    @Override
    public long insertLog(Long startTime, Long endTime, Integer resourceCount, boolean isSysCreated) throws Exception {
        return 0;
    }
}
