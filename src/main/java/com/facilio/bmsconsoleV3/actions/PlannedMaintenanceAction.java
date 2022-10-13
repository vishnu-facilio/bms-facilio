package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.Objects;

@Log4j
public class PlannedMaintenanceAction extends V3Action {

    private long plannerId;
    public long getPlannerId() {
        return plannerId;
    }
    public void setPlannerId(long plannerId) {
        this.plannerId = plannerId;
    }

    private long resourceId;
    public long getResourceId() {
        return resourceId;
    }
    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    private long pmId;
    public long getPmId() {
        return pmId;
    }
    public void setPmId(long pmId) {
        this.pmId = pmId;
    }

    private List<Long> pmIds;
    public List<Long> getPmIds() {
        return pmIds;
    }
    public void setPmIds(List<Long> pmIds) {
        this.pmIds = pmIds;
    }

    public String executeNow() throws Exception {
        FacilioChain executeNowChain = TransactionChainFactoryV3.getExecuteNow();
        FacilioContext context = executeNowChain.getContext();

        LOGGER.error("[execute now] pmId : " + pmId);
        LOGGER.error("[execute now] pmIds : " + String.valueOf(pmIds));
        LOGGER.error("[execute now] planner Id : " + plannerId);
        LOGGER.error("[execute now] resource Id : " + resourceId);

        context.put("plannerId", plannerId);
        context.put("resourceId", resourceId);
        executeNowChain.execute();
        return SUCCESS;
    }

    public String publishPM() throws Exception {
        FacilioChain publishPMChain = TransactionChainFactoryV3.getPublishPM();
        FacilioContext context = publishPMChain.getContext();
        context.put("pmId", pmId);
        publishPMChain.execute();
        return SUCCESS;
    }

    public String deactivate() throws Exception {
        FacilioChain unpublishPMChain = TransactionChainFactoryV3.getDeactivatePM();
        FacilioContext context = unpublishPMChain.getContext();
        context.put("pmId", pmId);
        unpublishPMChain.execute();
        return SUCCESS;
    }

    public String bulkPublish() throws Exception {
        FacilioChain publishPMChain = TransactionChainFactoryV3.getPublishPM();
        FacilioContext context = publishPMChain.getContext();
        context.put("pmIds", pmIds);
        publishPMChain.execute();
        return SUCCESS;
    }

    public String bulkUnPublishPM() throws Exception {
        FacilioChain unPublishPMChain = TransactionChainFactoryV3.getDeactivatePM();
        FacilioContext context = unPublishPMChain.getContext();
        context.put("pmIds", pmIds);
        unPublishPMChain.execute();
        return SUCCESS;
    }

}
