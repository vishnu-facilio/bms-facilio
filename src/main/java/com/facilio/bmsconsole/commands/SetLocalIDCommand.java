package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

public class SetLocalIDCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module != null) {
            if (module.isCustom()) {
                FacilioField localIdField = modBean.getField("localId", moduleName);
                if (localIdField != null) {
                    context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
                }
            }
        }
        return false;
    }
}
