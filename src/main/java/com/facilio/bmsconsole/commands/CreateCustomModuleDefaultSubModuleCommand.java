package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class CreateCustomModuleDefaultSubModuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule cmdNotes = modBean.getModule("cmdnotes");
        modBean.addSubModule(module.getModuleId(), cmdNotes.getModuleId());

        FacilioModule cmdAttachments = modBean.getModule("cmdattachments");
        modBean.addSubModule(module.getModuleId(), cmdAttachments.getModuleId());

        return false;
    }
}
