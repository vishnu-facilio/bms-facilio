package com.facilio.permission.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class AddOrUpdatePermissionSet extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        PermissionSetContext permissionSet = (PermissionSetContext) context.getOrDefault(FacilioConstants.ContextNames.RECORD,null);
        long id = -1;
        if(permissionSet != null) {
            boolean restrict = false;
            PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
            List<FacilioField> fields = PermissionSetFieldFactory.getPermissionSetFields();
            if(permissionSet.getId() > 0) {
                List<String> fieldsNames = Arrays.asList("sysCreatedTime","sysCreatedBy","sysDeletedBy","sysDeletedTime");
                fields.removeIf(f -> fieldsNames.contains(f.getName()));
                id = permissionSet.getId();
                PermissionSetContext ps = permissionSetBean.getPermissionSet(id);
                if(ps != null && ps.isPrivileged()) {
                    restrict = true;
                }
                permissionSetBean.updatePermissionSet(permissionSet);
            } else {
                List<String> fieldsNames = Arrays.asList("sysDeletedBy","sysDeletedTime");
                fields.removeIf(f -> fieldsNames.contains(f.getName()));
                permissionSet.setSysCreatedTime(System.currentTimeMillis());
                permissionSet.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
                permissionSet.setSysModifiedTime(System.currentTimeMillis());
                permissionSet.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
                id = permissionSetBean.addPermissionSet(permissionSet);
                PermissionSetContext ps = permissionSetBean.getPermissionSet(id);
                if(ps != null && ps.isPrivileged()) {
                    restrict = true;
                }
            }
            if(restrict) {
                throw new IllegalArgumentException("Privileged permission set cannot be updated");
            }
        }
        context.put(FacilioConstants.ContextNames.ID,id);
        return false;
    }
}
