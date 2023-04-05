package com.facilio.permission.action;

import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.util.PermissionSetUtil;
import com.facilio.v3.V3Action;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Log4j
@Getter
@Setter
public class PermissionSetAction extends FacilioAction {
    private PermissionSetContext permissionSet;
    private long id;

    private List<Object> permissions;

    public String addOrUpdate() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addOrUpdatePermissionSetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, getPermissionSet());
        chain.execute();
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        PermissionSetContext permissionSet = getPermissionSet();
        if(permissionSet != null) {
            if(permissionSet.getId() > 0) {
                PermissionSetUtil.addAuditLogs(permissionSet.getId(),"updated");
            } else {
                PermissionSetUtil.addAuditLogs(id,"added");
            }
        }
        setResult(PermissionSetConstants.PERMISSION_SET, context.get(PermissionSetConstants.PERMISSION_SET));
        return V3Action.SUCCESS;
    }

    public String fetch() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.fetchPermissionSetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_SET, context.get(PermissionSetConstants.PERMISSION_SET));
        return V3Action.SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.deletePermissionSetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        PermissionSetUtil.addAuditLogs(getId(),"deleted");
        chain.execute();
        setResult("message", "SUCCESS");
        return V3Action.SUCCESS;
    }

    public String updatePermission() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.updatePermissionsForPermissionSetChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PERMISSIONS, getPermissions());
        chain.execute();
        setResult("message", "SUCCESS");
        return V3Action.SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.fetchPermissionSetListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put(FacilioConstants.ContextNames.PAGE, getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_SET, context.get(PermissionSetConstants.PERMISSION_SET));
        setResult(FacilioConstants.ContextNames.CREATED_BY, context.get(FacilioConstants.ContextNames.CREATED_BY));
        setResult(FacilioConstants.ContextNames.COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        return V3Action.SUCCESS;
    }
}
