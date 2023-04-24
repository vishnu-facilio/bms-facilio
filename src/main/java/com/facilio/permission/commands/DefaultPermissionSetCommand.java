package com.facilio.permission.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;
import com.facilio.permission.context.module.RelatedListPermissionSet;
import com.facilio.permission.handlers.group.RelatedRecordsPermissionSetHandler;
import com.facilio.permission.handlers.item.ModuleGroupItemHandler;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class DefaultPermissionSetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        PermissionSetContext permissionSet = new PermissionSetContext();
        permissionSet.setDisplayName("Privileged Permission Set");
        permissionSet.setDescription("Privileged Permission Set");
        permissionSet.setStatus(true);
        permissionSet.setIsPrivileged(true);
        permissionSet.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        permissionSet.setSysCreatedTime(System.currentTimeMillis());
        permissionSet.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        permissionSet.setSysModifiedTime(System.currentTimeMillis());
        long permissionSetId = permissionSetBean.addPermissionSet(permissionSet);

        long ouid = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId()).getOuid();
        long peopleId = PeopleAPI.getPeopleIdForUser(ouid);
        permissionSetBean.updateUserPermissionSets(peopleId, Collections.singletonList(permissionSetId));

        return false;
    }
}
