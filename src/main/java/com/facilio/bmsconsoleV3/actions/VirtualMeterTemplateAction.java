package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;

public class VirtualMeterTemplateAction extends V3Action {

	
	Long startTime;
	Long endTime;
	
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	private long vmTemplateId;
    public long getVmTemplateId() {
        return vmTemplateId;
    }
    public void setVmTemplateId(long vmTemplateId) {
        this.vmTemplateId = vmTemplateId;
    }

    private long relationShipId;
    public long getRelationShipId() {
        return relationShipId;
    }
    public void setRelationShipId(long relationShipId) {
        this.relationShipId = relationShipId;
    }

    private List<Long> resourceIds;
    public List<Long> getResourceIds() { return resourceIds; }
    public void setResourceIds(List<Long> resourceIds) { this.resourceIds = resourceIds; }
    
    
    public String runHistoryForVMTemplate() throws Exception {
    	
    	if(CollectionUtils.isEmpty(resourceIds)) {
    		throw new IllegalArgumentException("resourceIds cannot be empty");
    	}
    	
    	if(startTime ==null || endTime == null) {
    		throw new IllegalArgumentException("startTime,endTime cannot be empty");
    	}

    	if(vmTemplateId <= 0) {
    		throw new IllegalArgumentException("vmTemplateId cannot be empty");
    	}
    	FacilioChain runHistoryirtualMeterTemplate = TransactionChainFactoryV3.getRunHistoryVirtualMeterTemplateChain();
        FacilioContext context = runHistoryirtualMeterTemplate.getContext();
        context.put("vmTemplateId", vmTemplateId);
        context.put("resourceIds", resourceIds);
        context.put("startTime", startTime);
        context.put("endTime", endTime);
        runHistoryirtualMeterTemplate.execute();
    	
        return SUCCESS;
    }

    public String publishVMTemplate() throws Exception {
        FacilioChain publishVirtualMeterTemplate = TransactionChainFactoryV3.getPublishVirtualMeterTemplateChain();
        FacilioContext context = publishVirtualMeterTemplate.getContext();
        context.put("vmTemplateId", vmTemplateId);
        publishVirtualMeterTemplate.execute();
        return SUCCESS;
    }

    public String generateVirtualMeter() throws Exception {
        FacilioChain generateVirtualMeter = TransactionChainFactoryV3.getGenerateVirtualMeterChain();
        FacilioContext context = generateVirtualMeter.getContext();
        context.put("vmTemplateId", vmTemplateId);
        context.put("resourceIds", resourceIds);
        generateVirtualMeter.execute();
        return SUCCESS;
    }
    public String fetchVMGeneratedResources() throws Exception {
        FacilioChain fetchVMGeneratedResources = TransactionChainFactoryV3.fetchVMGeneratedResourcesChain();
        FacilioContext context = fetchVMGeneratedResources.getContext();
        context.put("relationShipId", relationShipId);
        fetchVMGeneratedResources.execute();
        List<Long> resourceList = new ArrayList<>();
        if(context.get("resourceList") != null) {
            resourceList = (List<Long>) context.get("resourceList");
        }
        setData("resourceIds",resourceList);
        return SUCCESS;
    }
}
