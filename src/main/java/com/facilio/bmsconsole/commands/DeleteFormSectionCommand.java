package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.util.FormsAPI;

import java.util.Collections;

public class DeleteFormSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formSectionId = (long) context.getOrDefault(FacilioConstants.FormContextNames.FORM_SECTION_ID,-1l);

        FormsAPI.deleteFormSections(Collections.singletonList(formSectionId));

        return false;

    }
}
