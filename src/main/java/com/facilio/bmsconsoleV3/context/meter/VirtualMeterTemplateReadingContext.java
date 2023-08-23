package com.facilio.bmsconsoleV3.context.meter;

import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NamespaceFrequency;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualMeterTemplateReadingContext extends V3Context {

	VirtualMeterTemplateContext virtualMeterTemplate;
	Long readingFieldId;
	private NamespaceFrequency frequency;
	private NameSpaceContext ns;
    private Boolean status;
	
	
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
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
}
