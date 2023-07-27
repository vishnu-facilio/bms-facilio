package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddDefaultRolesFsmApp extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ApplicationContext fsm = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Role fsmManager = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FSM_MANAGER);
        //ApplicationApi.addAppRoleMapping(fsmManager.getRoleId(), fsm.getId());
        Role fsmDispatcher = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FSM_DISPATCHER);
        //ApplicationApi.addAppRoleMapping(fsmDispatcher.getRoleId(), fsm.getId());
        Role fsmTechnician = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.FSM_TECHNICIAN);
//        ApplicationApi.addAppRoleMapping(fsmTechnician.getRoleId(), fsm.getId());
        return false;
    }
}
