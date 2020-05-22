package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.Set;

public class AddDefaultScopingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Reflections reflections = new Reflections("com.facilio.bmsconsoleV3.scoping");
        Set<Class<? extends ModuleScopingConfiguration>> scopingClasses =
                reflections.getSubTypesOf(ModuleScopingConfiguration.class);
        if(CollectionUtils.isNotEmpty(scopingClasses)){
            for(Class<? extends ModuleScopingConfiguration> scopingClass : scopingClasses){
                ModuleScopingConfiguration config = scopingClass.newInstance();
                config.addScopingConfiguration();
            }
        }
        return false;
    }
}
