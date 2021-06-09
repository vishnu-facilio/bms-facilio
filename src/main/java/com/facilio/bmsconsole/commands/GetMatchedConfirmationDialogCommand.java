package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ConfirmationDialogContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class GetMatchedConfirmationDialogCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        List<ConfirmationDialogContext> confirmationDialogs = (List<ConfirmationDialogContext>) context.get(FacilioConstants.ContextNames.CONFIRMATION_DIALOGS);

        if (moduleData != null && CollectionUtils.isNotEmpty(confirmationDialogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module name");
            }
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

            Map<String, Object> data = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DATA);
            Class classFromModule = FacilioConstants.ContextNames.getClassFromModule(module);
            ModuleBaseWithCustomFields sourceObject = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(data, classFromModule);

            if (MapUtils.isNotEmpty(data)) {
                handleDataComingFromV2Components(sourceObject, fieldMap, data.keySet());
                for (String key : data.keySet()) {
                    FacilioField facilioField = fieldMap.get(key);
                    if (facilioField == null) {
                        continue;
                    }
                    FieldUtil.setValue(moduleData, facilioField, FieldUtil.getValue(sourceObject, facilioField));
                }
            }

            List<ConfirmationDialogContext> dialogContexts = new ArrayList<>();
            Map<String, Object> placeHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
            for (ConfirmationDialogContext confirmationDialog : confirmationDialogs) {
                if (confirmationDialog.getNamedCriteria() == null) {
                    dialogContexts.add(confirmationDialog); // it will match for all records
                    continue;
                }
                if (confirmationDialog.getNamedCriteria().evaluate(moduleData, context, placeHolders)) {
                    dialogContexts.add(confirmationDialog);
                }
            }

            for (ConfirmationDialogContext confirmationDialogContext : dialogContexts) {
                confirmationDialogContext.setMessage(confirmationDialogContext.getResolvedMessage(moduleData));
            }

            context.put(FacilioConstants.ContextNames.VALID_CONFIRMATION_DIALOGS, dialogContexts);
        }
        return false;
    }

    private void handleDataComingFromV2Components(ModuleBaseWithCustomFields data, Map<String, FacilioField> fieldMap, Set<String> keySet) throws Exception {
        for (String key : keySet) {
            FacilioField facilioField = fieldMap.get(key);
            if (!facilioField.isDefault() && facilioField instanceof LookupField) {
                Object value = FieldUtil.getValue(data, facilioField);
                if (value == null || value instanceof Map) {
                    continue;
                }

                Map<String, Object> map = null;
                if (value instanceof Long) {
                    map = new HashMap<>();
                    map.put("id", value);
                }
                FieldUtil.setValue(data, facilioField, map);
            }
        }
    }
}
