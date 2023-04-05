package com.facilio.permission.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.TypeItem.GroupingType;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPermissionSetGroupingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(PermissionSetConstants.PERMISSION_SET_GROUPING, GroupingType.values());
        return false;
    }
}