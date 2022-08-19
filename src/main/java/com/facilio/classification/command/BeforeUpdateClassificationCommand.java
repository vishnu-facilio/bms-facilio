package com.facilio.classification.command;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Map;

public class BeforeUpdateClassificationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<Long, ModuleBaseWithCustomFields> oldRecordMap = Constants.getOldRecordMap(context);

        // need to check old attributes are sent properly - for renaming purpose or we just need to take the patch things
        // since we won't remove old attributes
        // add new attributes

        // we need to change name, description for patch request

        return false;
    }
}
