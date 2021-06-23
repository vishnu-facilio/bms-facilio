package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import java.util.ArrayList;

public class UpdateFormListFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<FacilioForm> forms = (ArrayList<FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS_LIST);
        ArrayList<FacilioForm> responseForm = new ArrayList<>();
        for (FacilioForm form: forms) {
            FormsAPI.initFormFields(form);
            responseForm.add(FormsAPI.getFormFromDB(form.getId()));
        }
        context.put(FacilioConstants.ContextNames.FORMS_RESPONSE_LIST, responseForm);
        return false;
    }

}
