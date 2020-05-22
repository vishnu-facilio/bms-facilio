package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.ModuleFieldPermission;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.Set;

public class AddDefaultFieldPermissionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Reflections reflections = new Reflections("com.facilio.bmsconsoleV3.fieldpermission");
        Set<Class<? extends ModuleFieldPermission>> fieldPermissionClasses =
                reflections.getSubTypesOf(ModuleFieldPermission.class);
        if(CollectionUtils.isNotEmpty(fieldPermissionClasses)){
            for(Class<? extends ModuleFieldPermission> moduleFieldPermissionClass : fieldPermissionClasses){
                ModuleFieldPermission config = moduleFieldPermissionClass.newInstance();
                config.addModuleFieldPermission();
            }
        }
        return false;
    }
}
