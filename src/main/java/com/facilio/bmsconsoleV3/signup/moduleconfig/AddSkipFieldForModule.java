package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;

public class AddSkipFieldForModule extends SignUpData {
    @Override
    public void addData() throws Exception{
        CommonCommandUtil.insertOrgInfo("skipField_flaggedAlarm", "serviceOrder");
    }
}
