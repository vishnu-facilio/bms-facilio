package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.util.FormsAPI;

import java.util.Collections;

public class DeleteFormFieldCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formFieldId = (long) context.get(FacilioConstants.ContextNames.FORM_FIELD_ID);

        if (formFieldId > 0) {
            FormsAPI.deleteFormFields(Collections.singletonList(formFieldId));
        }

        return false;
    }
}
