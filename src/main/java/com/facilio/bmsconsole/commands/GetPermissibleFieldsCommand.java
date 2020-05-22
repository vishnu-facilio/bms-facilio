package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collection;
import java.util.List;

public class GetPermissibleFieldsCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioField> allFields = (List<FacilioField>)context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        FieldPermissionContext.PermissionType fieldPermissionType = (FieldPermissionContext.PermissionType) context
                .get(FacilioConstants.ContextNames.PERMISSION_TYPE);
        Boolean validateFieldPermissions = (Boolean) context
                .getOrDefault(FacilioConstants.ContextNames.DO_FIELD_PERMISSIONS_VALIDATION, true);

        try {
            Collection<FacilioField> permissibleFields = FieldUtil.getPermissibleFields(allFields, moduleName,
                             fieldPermissionType, validateFieldPermissions);
            context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, permissibleFields);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
