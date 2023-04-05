package com.facilio.permission.commands;

import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.util.PermissionSetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdatePermissionsForPermissionSetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Object> permissions = (List<Object>) context.get(FacilioConstants.ContextNames.PERMISSIONS);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        if(CollectionUtils.isNotEmpty(permissions)) {
            for(Object permission : permissions) {
                permissionSetBean.addPermissionsForPermissionSet((Map<String, Object>) permission);
            }
            BasePermissionContext permission = FieldUtil.getAsBeanFromMap((Map<String, Object>) permissions.get(0),BasePermissionContext.class);
            PermissionSetUtil.addAuditLogs(permission.getPermissionSetId(),"updated");
        }
        return false;
    }
}