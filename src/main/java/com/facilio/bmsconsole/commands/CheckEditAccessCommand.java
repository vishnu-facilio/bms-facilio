package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class CheckEditAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.EXISTING_CV);

        Boolean isSuperAdmin = AccountUtil.getCurrentUser().isSuperAdmin();
        if (isSuperAdmin) {
            return false;
        }

        Long currentUserRoleId = AccountUtil.getCurrentUser().getRoleId();
        Role adminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), AccountConstants.DefaultSuperAdmin.ADMINISTRATOR);
        Long adminRoleId = adminRole.getId();
        if (adminRoleId.equals(currentUserRoleId)){
            return false;
        }

        Long currentUserId = AccountUtil.getCurrentUser().getId();
        Long ownerId = view.getOwnerId();
        if (ownerId == -1){
            User user = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
            ownerId = user.getOuid();
        }
        if (ownerId.equals(currentUserId)) {
            return false;
        }

        Boolean isLocked = view.getIsLocked();
        if (isLocked){
            throw new IllegalArgumentException("You don’t have permission to access this View");
        }

        SharingContext<SingleSharingContext> sharingType = SharingAPI.getSharing(view.getId(), ModuleFactory.getViewSharingModule(), SingleSharingContext.class);
        if (sharingType != null && !sharingType.isAllowed()) {
            throw new IllegalArgumentException("You don’t have permission to access this View");
        }
        return false;
    }
}
