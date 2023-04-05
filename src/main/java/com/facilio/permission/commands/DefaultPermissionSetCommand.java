package com.facilio.permission.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultPermissionSetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");

        PermissionSetContext permissionSet = new PermissionSetContext();
        permissionSet.setDisplayName("Default Permission Set");
        permissionSet.setDescription("Default Permission Set");
        permissionSet.setStatus(true);
        permissionSet.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
        permissionSet.setSysCreatedTime(System.currentTimeMillis());
        permissionSet.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        permissionSet.setSysModifiedTime(System.currentTimeMillis());
        long permissionSetId = permissionSetBean.addPermissionSet(permissionSet);


        ModuleGroupItemHandler moduleGroupItemHandler = new ModuleGroupItemHandler();
        List<PermissionSetGroupingItemContext> permissionSetGroupingItemsContext = moduleGroupItemHandler.getGroupItems();
        if(CollectionUtils.isNotEmpty(permissionSetGroupingItemsContext)) {
            for(PermissionSetGroupingItemContext item : permissionSetGroupingItemsContext) {
                RelatedRecordsPermissionSetHandler handler = new RelatedRecordsPermissionSetHandler();
                List<RelatedListPermissionSet> relatedListPermissionSet = handler.getPermissions(item.getId());
                if(CollectionUtils.isNotEmpty(relatedListPermissionSet)) {
                    for(RelatedListPermissionSet perm : relatedListPermissionSet) {
                        perm.setPermissionSetId(permissionSetId);
                        perm.setPermission(true);
                        perm.setType(PermissionSetType.Type.RELATED_LIST);
                        perm.setPermissionType(PermissionFieldEnum.RELATED_LIST_READ_PERMISSION);
                        Map<String,Object> permProp = FieldUtil.getAsProperties(perm);
                        permissionSetBean.addPermissionsForPermissionSet(permProp);
                    }
                }
            }
        }
        return false;
    }
}
