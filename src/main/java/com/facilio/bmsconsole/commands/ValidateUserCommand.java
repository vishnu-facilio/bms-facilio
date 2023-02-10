package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.OrgUserApp;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;

public class ValidateUserCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        User user = (User) context.get(FacilioConstants.ContextNames.USER);
        String operation = (String) context.get(FacilioConstants.ContextNames.USER_OPERATION);

        FacilioUtil.throwIllegalArgumentException(user == null, MessageFormat.format("User cannot be null for {0}", operation));
        FacilioUtil.throwIllegalArgumentException(user.getOuid() <= 0, MessageFormat.format("Invalid ouid for {0}", operation));
        FacilioUtil.throwIllegalArgumentException(user.getId() == AccountUtil.getCurrentUser().getId(), MessageFormat.format("Logged In user cannot be used for {0}", operation));

        User iamUser = AccountUtil.getUserBean().getUser(user.getOuid(), false);
        FacilioUtil.throwIllegalArgumentException(iamUser == null, MessageFormat.format("Invalid ouid for {0}", operation));

        //to be checked
        RoleBean roleBean = AccountUtil.getRoleBean();
        Role superAdminRole = roleBean.getRole(AccountUtil.getCurrentOrg().getOrgId(), AccountConstants.DefaultSuperAdmin.SUPER_ADMIN);
        List<OrgUserApp> orgUserApps = roleBean.getRolesAppsMappingForUser(user.getOuid());
        if(CollectionUtils.isEmpty(orgUserApps))  {
            for(OrgUserApp orgUserApp : orgUserApps) {
                if(orgUserApp.getRoleId() == superAdminRole.getRoleId()) {
                    FacilioUtil.throwIllegalArgumentException(AccountConstants.DefaultRole.SUPER_ADMIN.equals(iamUser.getRole().getName()), MessageFormat.format("SuperAdmin user cannot be used for {0}", operation));
                }
            }
        }
        user.setUid(iamUser.getUid());
        user.setIdentifier(iamUser.getIdentifier());
        user.setUserName(iamUser.getUserName());
        user.setEmail(iamUser.getEmail());
        user.setPeopleId(iamUser.getPeopleId());
        return false;
    }
}
