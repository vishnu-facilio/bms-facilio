package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class DeletePageComponentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(id != null && id > 0){

            if(module.equals(ModuleFactory.getPagesModule())) {
                CustomPageAPI.deletePage(Collections.singletonList(id), module);
            }
            else {
                CustomPageAPI.deletePageComponent(id, module);
            }
        }

        return false;
    }
}
