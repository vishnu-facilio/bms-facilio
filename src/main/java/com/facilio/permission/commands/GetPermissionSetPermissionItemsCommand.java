package com.facilio.permission.commands;

import com.facilio.beans.PermissionSetBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetType;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPermissionSetPermissionItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String groupType = (String) context.get(PermissionSetConstants.PERMISSION_SET_GROUP_TYPE);
        Long permissionSetId = (Long) context.get(PermissionSetConstants.PERMISSION_SET_ID);
        Long groupId = (Long) context.get(PermissionSetConstants.PERMISSION_SET_GROUP_ID);
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        context.put(PermissionSetConstants.PERMISSION_ITEMS,permissionSetBean.getPermissionSetItems(permissionSetId,groupId,groupType));
        if(groupType != null) {
            List<Map<String,String>> fieldsMap = new ArrayList<>();
            PermissionSetType.Type type = PermissionSetType.Type.valueOf(groupType.toUpperCase());
            if(type != null) {
                for(PermissionFieldEnum permissionFieldEnum : type.getPermissionFieldEnumList()) {
                    Map<String,String> fieldPermMap = new HashMap<>();
                    fieldPermMap.put("fieldName",permissionFieldEnum.name());
                    fieldPermMap.put("displayName",permissionFieldEnum.getDisplayName());
                    fieldsMap.add(fieldPermMap);
                }
                context.put(PermissionSetConstants.PERMISSION_FIELDS_MAP,fieldsMap);
                context.put(PermissionSetConstants.PERMISSIONS_SET_GROUP_KEYS,type.getQueryFields());
            }
        }

        return false;
    }
}
