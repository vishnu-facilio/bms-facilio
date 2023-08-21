package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class CreateClonedFormCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String appLinkName = (String) context.get(FacilioConstants.ContextNames.APP_LINKNAME);
        Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);

        FacilioUtil.throwIllegalArgumentException((formId<0 || StringUtils.isEmpty(appLinkName)),"Form id or app name cannot be empty.");

        FacilioForm oldForm = FormsAPI.getFormFromDB(formId);
        FacilioModule module = oldForm.getModule();

        String clonedFormName = "copy_of_"+oldForm.getName();
        FacilioForm existingForm = FormsAPI.getFormFromDB(clonedFormName, module);

        FacilioUtil.throwIllegalArgumentException(existingForm!=null,"Form Already Cloned to "+appLinkName+ "app");

        FacilioForm clonedForm = FormsAPI.getClonedForm(oldForm,appLinkName,clonedFormName);

        long newFormId = FormsAPI.createForm(clonedForm,module);

        context.put(FacilioConstants.ContextNames.CLONED_FORM_ID,newFormId);
        context.put(FacilioConstants.ContextNames.FORM,oldForm);
        context.put(FacilioConstants.ContextNames.CLONED_FORM,clonedForm);

        return false;
    }

}
