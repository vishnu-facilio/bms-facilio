package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
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
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
            if (MapUtils.isNotEmpty(data)) {
                for (String key : data.keySet()) {
                    FacilioField facilioField = fieldMap.get(key);
                    if (facilioField == null) {
                        continue;
                    }
                    if (facilioField.isDefault()) {
                        try {
                            PropertyUtils.setProperty(moduleData, key, data.get(key));
                        } catch (Exception e) {
                        }
                    } else {
                        moduleData.setDatum(key, data.get(key));
                    }
                }
            }

            List<ConfirmationDialogContext> dialogContexts = new ArrayList<>();
            for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
                if (confirmationDialog.getCriteria() == null) {
                    dialogContexts.add(confirmationDialog); // it will match for all records
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
