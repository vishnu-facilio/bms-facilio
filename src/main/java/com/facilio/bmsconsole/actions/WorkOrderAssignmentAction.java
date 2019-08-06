package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class WorkOrderAssignmentAction extends FacilioAction {

    private Long woId;
    public Long getWoId() {
        return woId;
    }
    public void setWoId(Long woId) {
        this.woId = woId;
    }

    private String roleName;
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getWorkOrderRecommendedUsers() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getWoId());
        context.put(FacilioConstants.ContextNames.ROLE, getRoleName());

        Chain c = ReadOnlyChainFactory.getRecommendedUsers();
        c.execute(context);

        setResult(FacilioConstants.ContextNames.RECOMMENDED_USERS, context.get(FacilioConstants.ContextNames.RECOMMENDED_USERS));

        return SUCCESS;
    }

}
