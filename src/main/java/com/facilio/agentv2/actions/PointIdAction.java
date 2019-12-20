package com.facilio.agentv2.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PointIdAction extends FacilioAction
{
    private static final Logger LOGGER = LogManager.getLogger(PointIdAction.class.getName());

    @NotNull
    @Min(1)
    private Long pointId;

    public Long getPointId() { return pointId; }
    public void setPointId(Long pointId) { this.pointId = pointId; }




}
