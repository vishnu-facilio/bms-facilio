package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class FieldRelationAction extends FacilioAction {

    private Long assetId;
    private Long startTime;
    private Long endTime;
    private Long fieldDependencyId;

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

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

    public Long getFieldDependencyId() {
        return fieldDependencyId;
    }

    public void setFieldDependencyId(Long fieldDependencyId) {
        this.fieldDependencyId = fieldDependencyId;
    }

    public String runDependencyHistory() throws Exception {
        FacilioChain chain = TransactionChainFactory.runDependencyHistory();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, assetId);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.FIELD_DEPENDENCY_ID, fieldDependencyId);
        chain.execute();
        return SUCCESS;
    }
}
