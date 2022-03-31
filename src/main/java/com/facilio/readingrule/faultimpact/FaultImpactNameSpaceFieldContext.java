package com.facilio.readingrule.faultimpact;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.ns.context.AggregationType;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FaultImpactNameSpaceFieldContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Long impact;
	ResourceContext resource;
    
	Long fieldId;
    Long dataInterval;
    
    AggregationType aggregationType;
    boolean enabledCompaction;
    String varName;
    
    public void setAggregationType(int aggregationType) {
        this.aggregationType = AggregationType.valueOf(aggregationType);
    }
    
    public int getAggregationType() {
        if(aggregationType != null) {
        	return aggregationType.getIndex();
        }
        return -1;
    }
}
