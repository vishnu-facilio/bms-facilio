package com.facilio.apiv3.sample;

import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;

public class SampleAfterTransactionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        return false;
    }
}
