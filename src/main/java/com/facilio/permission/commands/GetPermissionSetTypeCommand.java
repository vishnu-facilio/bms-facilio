package com.facilio.permission.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.context.TypeItem.GroupingType;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GetPermissionSetTypeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String permissionSetGroupName = (String) context.get(PermissionSetConstants.PERMISSION_SET_GROUPING);
        List<PermissionSetType.Type> subGroupTypes = new ArrayList<>();
        if(StringUtils.isNotEmpty(permissionSetGroupName)) {
            GroupingType groupType = GroupingType.valueOf(permissionSetGroupName.toUpperCase());
            for(PermissionSetType.Type type : PermissionSetType.Type.values()) {
                if(type.getGroupingType() == groupType) {
                    subGroupTypes.add(type);
                }
            }
        }
        context.put(PermissionSetConstants.PERMISSION_SET_TYPES,subGroupTypes);
        return false;
    }
}
