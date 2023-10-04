package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import java.util.List;

public class VirtualMeterTemplateAction extends V3Action {

    private long vmTemplateId;
    public long getVmTemplateId() {
        return vmTemplateId;
    }
    public void setVmTemplateId(long vmTemplateId) {
        this.vmTemplateId = vmTemplateId;
    }

    private List<Long> resourceIds;
    public List<Long> getResourceIds() { return resourceIds; }
    public void setResourceIds(List<Long> resourceIds) { this.resourceIds = resourceIds; }

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
}
