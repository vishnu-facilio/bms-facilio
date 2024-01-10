package com.facilio.bmsconsoleV3.commands;


import com.facilio.bmsconsole.modulemapping.ModuleMappingConfigUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddModuleMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleMappingConfigUtil moduleMappingConfigObj = new ModuleMappingConfigUtil();

        moduleMappingConfigObj.createTargetModuleRecord(context);

        return false;
    }
}
