package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetMatchedConfirmationDialogCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        List<ConfirmationDialogContext> confirmationDialogs = (List<ConfirmationDialogContext>) context.get(FacilioConstants.ContextNames.CONFIRMATION_DIALOGS);

        if (moduleData != null && CollectionUtils.isNotEmpty(confirmationDialogs)) {
            List<ConfirmationDialogContext> dialogContexts = new ArrayList<>();
            for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
                if (confirmationDialog.getCriteria() == null) {
                    continue;
                }
                if (confirmationDialog.getCriteria().computePredicate().evaluate(moduleData)) {
                    dialogContexts.add(confirmationDialog);
                }
            }

            context.put(FacilioConstants.ContextNames.VALID_CONFIRMATION_DIALOGS, dialogContexts);
        }
        return false;
    }
}
