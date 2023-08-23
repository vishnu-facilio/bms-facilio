package com.facilio.bmsconsoleV3.actions;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;

public class VirtualMeterTemplateAction extends V3Action {

    private long vmTemplateId;
    public long getVmTemplateId() {
        return vmTemplateId;
    }
    public void setVmTemplateId(long vmTemplateId) {
        this.vmTemplateId = vmTemplateId;
    }

    public String publishVMTemplate() throws Exception {
        FacilioChain publishVirtualMeterTemplate = TransactionChainFactoryV3.getPublishVirtualMeterTemplateChain();
        FacilioContext context = publishVirtualMeterTemplate.getContext();
        context.put("vmTemplateId", vmTemplateId);
        publishVirtualMeterTemplate.execute();
        return SUCCESS;
    }
}
