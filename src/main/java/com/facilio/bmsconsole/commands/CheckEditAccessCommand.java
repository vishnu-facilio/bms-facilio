package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckEditAccessCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.EXISTING_CV);
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);

        User currentUser = AccountUtil.getCurrentUser();
        Boolean isSuperAdmin = currentUser.isSuperAdmin();
        if (isSuperAdmin) {
            return false;
        }

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        long currUserAppId = currentUser.getApplicationId();
        Long currentUserRoleId = currentUser.getRoleId();
        ApplicationContext currApp = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
        if (currApp == null) {
            currApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        long currAppId = currApp.getId();
        // Admin/CAFMAdmin Role has equal privileges as SuperAdmin Role
        Criteria roleNameCriteria = new Criteria();
        String[] roleNames = { FacilioConstants.DefaultRoleNames.ADMIN, FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN, FacilioConstants.DefaultRoleNames.CAFM_ADMIN };
        roleNameCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(roleNames, ","), StringOperators.IS));

        List<Role> adminRoles = AccountUtil.getRoleBean().getRoles(roleNameCriteria);
        List<Long> adminRoleIds = CollectionUtils.isNotEmpty(adminRoles) ? adminRoles.stream().map(Role::getId).collect(Collectors.toList()) : new ArrayList<>();

        boolean isPrivilegedRole = currentUser.getRole().isPrevileged() && (currAppId == currUserAppId);
        boolean isAdmin = adminRoleIds.contains(currentUserRoleId);
        if (isAdmin || isPrivilegedRole){
            return false;
        }

        Long currentUserId = currentUser.getId();
        Long ownerId = view.getOwnerId();
        if (ownerId == -1){
            User user = AccountUtil.getOrgBean().getSuperAdmin(orgId);
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
