package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.Map;

public class ActivityAction extends FacilioAction {


    private Long parentId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }


    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    private Long occurrenceId;

    public String getActivityModule() {
        return activityModule;
    }

    public void setActivityModule(String activityModule) {
        this.activityModule = activityModule;
    }

    private String activityModule;

    public String getActivityList() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, activityModule);
        context.put(FacilioConstants.ContextNames.PARENT_ID, this.parentId);
        if (!activityModule.isEmpty()) {
            if (this.occurrenceId != null && this.occurrenceId > 0) {
                context.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE_ID, this.occurrenceId);
            }
            FacilioChain activitives = ReadOnlyChainFactory.getActivitiesChain();
            activitives.execute(context);
            setResult("activity", context.get(FacilioConstants.ContextNames.RECORD_LIST));
        }
        return SUCCESS;
    }
}
