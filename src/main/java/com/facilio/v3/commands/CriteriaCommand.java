package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public abstract class CriteriaCommand extends FacilioCommand {
    protected FacilioModule module;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(Constants.CRITERIA_COMMAND_RESULT, getCriteria(context));
        return false;
    }

    public abstract Criteria getCriteria(Context context) throws Exception;
}
