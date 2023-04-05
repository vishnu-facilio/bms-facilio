package com.facilio.permission.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
@Setter
public class PermissionSetViewAction extends FacilioAction {
    private String groupingName;
    private Long groupId;
    private String groupType;
    private Long permissionSetId;
    public String getGroupings() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getPermissionSetGrouping();
        FacilioContext context = chain.getContext();
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_SET_GROUPING, context.get(PermissionSetConstants.PERMISSION_SET_GROUPING));
        return V3Action.SUCCESS;
    }

    public String getGroupTypes() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getPermissionSetTypeGroup();
        FacilioContext context = chain.getContext();
        context.put(PermissionSetConstants.PERMISSION_SET_GROUPING,getGroupingName());
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_SET_TYPES, context.get(PermissionSetConstants.PERMISSION_SET_TYPES));
        return V3Action.SUCCESS;
    }

    public String getGroupItems() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getPermissionSetGroupItems();
        FacilioContext context = chain.getContext();
        context.put(PermissionSetConstants.PERMISSION_SET_GROUPING,getGroupingName());
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_SET_GROUP_ITEMS, context.get(PermissionSetConstants.PERMISSION_SET_GROUP_ITEMS));
        return V3Action.SUCCESS;
    }

    public String getPermissionItems() throws Exception {
        FacilioChain chain = ReadOnlyChainFactoryV3.getPermissionPermissionItems();
        FacilioContext context = chain.getContext();
        context.put(PermissionSetConstants.PERMISSION_SET_GROUP_TYPE,getGroupType());
        context.put(PermissionSetConstants.PERMISSION_SET_ID,getPermissionSetId());
        context.put(PermissionSetConstants.PERMISSION_SET_GROUP_ID,getGroupId());
        chain.execute();
        setResult(PermissionSetConstants.PERMISSION_ITEMS, context.get(PermissionSetConstants.PERMISSION_ITEMS));
        setResult(PermissionSetConstants.PERMISSION_FIELDS_MAP,context.get(PermissionSetConstants.PERMISSION_FIELDS_MAP));
        setResult(PermissionSetConstants.PERMISSIONS_SET_GROUP_KEYS,context.get(PermissionSetConstants.PERMISSIONS_SET_GROUP_KEYS));

        return V3Action.SUCCESS;
    }
}
