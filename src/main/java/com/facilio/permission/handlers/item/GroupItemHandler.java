package com.facilio.permission.handlers.item;

import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;

import java.util.List;

public interface GroupItemHandler {
    default List<PermissionSetGroupingItemContext> getGroupItems() throws Exception {
        return null;
    }
}