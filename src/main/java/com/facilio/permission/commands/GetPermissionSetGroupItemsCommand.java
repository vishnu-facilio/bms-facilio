package com.facilio.permission.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.context.TypeItem.GroupingType;
import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;
import com.facilio.permission.handlers.group.PermissionSetGroupHandler;
import com.facilio.permission.handlers.item.GroupItemHandler;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

public class GetPermissionSetGroupItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String permissionSetGroupName = (String) context.get(PermissionSetConstants.PERMISSION_SET_GROUPING);
        if(StringUtils.isNotEmpty(permissionSetGroupName)) {
            GroupingType groupType = GroupingType.valueOf(permissionSetGroupName.toUpperCase());
            List<PermissionSetGroupingItemContext> list = groupType.getHandler().getGroupItems();
            List<PermissionSetGroupingItemContext> filteredList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(list)) {
                for(PermissionSetGroupingItemContext groupingItem : list) {
                    for(PermissionSetType.Type setType : PermissionSetType.Type.values()) {
                        if(groupingItem != null && setType.getGroupingType() == groupType) {
                            PermissionSetGroupHandler permissionSetGroupHandler = setType.getHandler();
                            if(CollectionUtils.isNotEmpty(permissionSetGroupHandler.getPermissions(groupingItem.getId()))) {
                                filteredList.add(groupingItem);
                            }
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(filteredList)) {
                filteredList = filteredList.stream()
                        .filter(item -> item != null)
                        .filter(distinctByKey(item -> item.getDisplayName()))
                        .sorted(Comparator.comparing(PermissionSetGroupingItemContext::getDisplayName))
                        .collect(Collectors.toList());
            }
            context.put(PermissionSetConstants.PERMISSION_SET_GROUP_ITEMS,filteredList);
        }
        return false;
    }
}
