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

        String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        String formName = (String) context.getOrDefault(FacilioConstants.ContextNames.FORM_NAME,null);
        long formId = (long) context.getOrDefault(FacilioConstants.ContextNames.FORM_ID,-1L);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = moduleBean.getModule(moduleName);

        FacilioForm facilioForm = null;
        if(formId<0) {
            facilioForm = FormsAPI.getFormFromDB(formName, facilioModule);
        }else{
            facilioForm = FormsAPI.getFormFromDB(formId);
        }

        context.put(FacilioConstants.ContextNames.FORM, facilioForm);

        return false;
    }
}
