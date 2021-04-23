package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetMatchedConfirmationDialogCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        List<ConfirmationDialogContext> confirmationDialogs = (List<ConfirmationDialogContext>) context.get(FacilioConstants.ContextNames.CONFIRMATION_DIALOGS);

        if (moduleData != null && CollectionUtils.isNotEmpty(confirmationDialogs)) {
            Map<String, Object> data = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA);
            if (MapUtils.isNotEmpty(data)) {
                for (String key : data.keySet()) {
                    try {
                        PropertyUtils.setProperty(moduleData, key, data.get(key));
                    } catch (Exception e) {}
                }
            }

            List<ConfirmationDialogContext> dialogContexts = new ArrayList<>();
            for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
                if (confirmationDialog.getCriteria() == null) {
                    dialogContexts.add(confirmationDialog); // it will match for all records
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
