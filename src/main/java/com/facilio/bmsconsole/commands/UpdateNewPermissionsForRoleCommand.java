package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateNewPermissionsForRoleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
        List<NewPermission> permissions = (List<NewPermission>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
        if(roleId > 0) {
            AccountUtil.getRoleBean().deleteSingleNewPermission(roleId);
            if (permissions != null && !permissions.isEmpty()) {
                for (NewPermission permission : permissions) {
                    permission.setRoleId(roleId);
                    RoleBean roleBean = AccountUtil.getRoleBean();
                    roleBean.addNewPermission(roleId, permission);
                }
            }
        }
        return false;
    }
}
