package com.facilio.weekends;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.dependentObjects.DependencyConstants;
import com.facilio.dependentObjects.DependencyUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class ValidateWeekendDeletion extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WeekendContext weekend = (WeekendContext) context.get(FacilioConstants.ContextNames.WEEKEND);
        long id = weekend.getId();
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid weekend details passed");
        }
        DependencyUtil.getDependantComponentDetails(DependencyConstants.WeekendDependencies.featureVsDependantColumns, id, true);
        return false;
    }

}
