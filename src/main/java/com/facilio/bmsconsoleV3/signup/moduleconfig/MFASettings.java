package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;

import static com.facilio.iam.accounts.impl.IAMOrgBeanImpl.*;

public class MFASettings extends SignUpData {
    @Override
    public void addData() throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        initialiseOrgMfaSettings(orgId);
    }
}
