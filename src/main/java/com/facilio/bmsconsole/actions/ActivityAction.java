package com.facilio.bmsconsole.actions;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ActivityAction extends FacilioAction {


    private long parentId;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private String getActivityList(String moduleName) throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.PARENT_ID, this.parentId);

        return SUCCESS;
    }
}
