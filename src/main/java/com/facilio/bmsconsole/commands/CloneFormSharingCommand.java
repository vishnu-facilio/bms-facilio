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
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CloneFormSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
        Long newFormId = (Long) context.get(FacilioConstants.ContextNames.CLONED_FORM_ID);

        FacilioModule formSharingModule = ModuleFactory.getFormSharingModule();
        List<FacilioField> formSharingFields = FieldFactory.getFormSharingFields();

        SharingContext<SingleSharingContext> formSharing = SharingAPI.getSharing(formId, formSharingModule, SingleSharingContext.class, formSharingFields);
        if (CollectionUtils.isEmpty(formSharing)) {
            return false;
        }
        formSharing.stream().forEach(singleSharingContext -> singleSharingContext.setParentId(newFormId));

        SharingAPI.addSharing(formSharing, formSharingFields, newFormId, formSharingModule);

        return false;
    }
}
