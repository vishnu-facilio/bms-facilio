package com.facilio.permission.commands;

import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class DeletePermissionSetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.getOrDefault(FacilioConstants.ContextNames.ID,-1);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        V3Util.throwRestException(permissionSetBean.permissionSetHasPeopleAssociation(id), ErrorCode.VALIDATION_ERROR,"Permission set is associated to users");
        permissionSetBean.deletePermissionSet(id);
        PermissionSetContext ps = permissionSetBean.getPermissionSet(id);
        if(ps != null && ps.isPrivileged()) {
            throw new IllegalArgumentException("Privileged permission set cannot be deleted");
        }
        return false;
    }
}