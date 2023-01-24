package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ModuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class GetModuleSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, Object> customization = new HashMap<>();
        Map<String, Object> summary = new HashMap<>();

        // Module
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module summary");

        // Parent Module
        FacilioModule parentModule = module.getParentModule();

        // Form Templates (only DB Forms, not FormFactory)
        ApplicationContext application = AccountUtil.getCurrentApp();
        if (application == null) {
            application = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        }
        long formsCount = FormsAPI.getFormsCount(module.getName(), false, true, application.getId());
        // Related Modules
        long relatedModulesCount = ModuleAPI.getRelatedModulesCount(module, FacilioModule.ModuleType.BASE_ENTITY.getValue(), null);

        // Extended Modules
        long extendedModulesCount = ModuleAPI.getExtendedModulesCount(module, null);

        // Fields
        List<FacilioField> fields = modBean.getAllFields(moduleName);
//        List<String> moduleFieldsToHide = getModuleFieldsToHide(moduleName);
        fields.removeIf(field -> field.getName().equals("stateFlowId") || (field.getName().equals("moduleState") && !module.isStateFlowEnabled()));     // handleStateField
        List<FacilioField> systemFields = fields.stream().filter(FacilioField::isDefault).collect(Collectors.toList());
        long systemFieldsCount = systemFields.size();
        long customFieldsCount = fields.size() - systemFields.size();

        // Buttons
        // TODO System Buttons
        long customButtonsCount = WorkflowRuleAPI.getCustomButtonsCount(module, null);

        summary.put("module", module);
        summary.put("parentModule", parentModule == null ? null : module.getModuleId() != parentModule.getModuleId() ? parentModule : null);

        customization.put("templates", formsCount);
        customization.put("systemFields", systemFieldsCount);
        customization.put("customFields", customFieldsCount);
        customization.put("customButtons", customButtonsCount);
        customization.put("relatedModules", relatedModulesCount);
        customization.put("extendedModules", extendedModulesCount);
        customization.put("stateFlow", module.isStateFlowEnabled());

        context.put(FacilioConstants.ContextNames.SUMMARY, summary);
        context.put(FacilioConstants.ContextNames.CUSTOMIZATION, customization);
        return false;
    }

    private static Map<String, String[]> hideFieldsSpecificToModule = Collections.unmodifiableMap(initModuleFields());

    public static List<String> getModuleFieldsToHide(String moduleName) {
        List<String> moduleFields = new ArrayList<>();
        if (hideFieldsSpecificToModule.containsKey(moduleName)) {
            moduleFields.addAll(Arrays.asList(hideFieldsSpecificToModule.get(moduleName)));
        }
        return moduleFields;
    }

    private static Map<String, String[]> initModuleFields() {
        Map<String, String[]> moduleFields = new HashMap<>();
        String[] workorder = {"stateFlowId", "isWorkDurationChangeAllowed", "approvalState", "approvalRuleId", "qrEnabled", "isSignatureRequired", "signature", "trigger",
                "sysCreatedTime", "estimatedStart", "jobStatus", "status", "preRequestStatus", "photoMandatory", "assignedBy", "resumedWorkStart", "serviceRequest",
                "prerequisiteEnabled", "allowNegativePreRequisite", "preRequisiteApproved", "woCreationOffset", "parentWO", "pm", "totalCost"};
        String[] asset = {"parentAssetId", "hideToCustomer", "lastDowntimeId", "localId", "purchaseOrder", "rotatingItem", "rotatingTool", "stateFlowId", "currentSpaceId",
                "downtimeStatus", "isUsed", "lastDowntimeId", "lastIssuedTime", "lastIssuedToUser", "lastIssuedToWo", "controllerId", "operatingHour", "photoId", "resourceType"};
        String[] tenant = {"localId", "logoId"};
        String[] vendors = {"sourceId", "hasInsurance", "stateFlowId"};
        String[] purchasecontracts = {"localId", "parentId"};
        String[] labourcontracts = {"localId", "parentId"};
        String[] rentalleasecontracts = {"localId", "parentId"};
        String[] warrantycontracts = {"localId", "parentId"};
        String[] workpermit = {"localId", "stateFlowId", "isRecurring", "recurringInfoId"};

        moduleFields.put("workorder", workorder);
        moduleFields.put("asset", asset);
        moduleFields.put("tenant", tenant);
        moduleFields.put("vendors", vendors);
        moduleFields.put("purchasecontracts", purchasecontracts);
        moduleFields.put("labourcontracts", labourcontracts);
        moduleFields.put("rentalleasecontracts", rentalleasecontracts);
        moduleFields.put("warrantycontracts", warrantycontracts);
        moduleFields.put("workpermit", workpermit);

        return moduleFields;
    }
}
