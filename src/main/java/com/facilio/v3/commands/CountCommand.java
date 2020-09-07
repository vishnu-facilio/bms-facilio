package com.facilio.v3.commands;

import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class CountCommand extends ListCommand{
    public CountCommand(FacilioModule module) {
        super(module);
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Constants.setOnlyCount(context, true);
        return super.executeCommand(context);
    }
}
