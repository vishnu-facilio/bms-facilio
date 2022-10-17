package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class GetFormFromDBCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String formName = (String) context.get(FacilioConstants.ContextNames.FORM_NAME);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = moduleBean.getModule(moduleName);

        FacilioForm facilioForm = FormsAPI.getFormFromDB(formName, facilioModule);

        context.put(FacilioConstants.ContextNames.FORM, facilioForm);

        return false;
    }
}
