package com.facilio.permission.commands;

import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class FetchPermissionSetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.getOrDefault(FacilioConstants.ContextNames.ID,-1);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        context.put(PermissionSetConstants.PERMISSION_SET,permissionSetBean.getPermissionSet(id));
        return false;
    }
}