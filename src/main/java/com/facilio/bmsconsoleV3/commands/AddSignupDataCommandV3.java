package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;

import java.util.Set;

public class AddSignupDataCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Reflections reflections = new Reflections("com.facilio.bmsconsoleV3.signup");
        Set<Class<? extends SignUpData>> scopingClasses =
                reflections.getSubTypesOf(SignUpData.class);
        if(CollectionUtils.isNotEmpty(scopingClasses)){
            for(Class<? extends SignUpData> scopingClass : scopingClasses){
                SignUpData config = scopingClass.newInstance();
                config.addData();
            }
        }
        return false;
    }
}
