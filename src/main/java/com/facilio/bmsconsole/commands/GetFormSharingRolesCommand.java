package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetFormSharingRolesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);

        FacilioModule formSharingModule = ModuleFactory.getFormSharingModule();
        List<FacilioField> formSharingFields = FieldFactory.getFormSharingFields();
        SharingContext<SingleSharingContext> formSharing = SharingAPI.getSharing(formId,formSharingModule,SingleSharingContext.class, formSharingFields);

        context.put(FacilioConstants.ContextNames.FORM_SHARING, formSharing);


        return false;
    }
}
