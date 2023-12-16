package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddSkipFieldForModule extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CommonCommandUtil.insertOrgInfo("skipField_flaggedAlarm", "serviceOrder");
        return false;
    }
}
