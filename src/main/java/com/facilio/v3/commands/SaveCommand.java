package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class SaveCommand extends FacilioCommand {
    private FacilioModule module;

    public SaveCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        return false;
    }
}
