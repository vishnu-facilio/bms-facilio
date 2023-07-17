package com.facilio.bulkUpdateV2;

import com.facilio.bmsconsole.actions.SendNotificationForOfflineRecordUpdate;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.enums.OfflineUpdateType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.VerifyApprovalCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.command.UpdateClassificationDataCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.commands.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Command;

import java.util.*;
import java.util.stream.Collectors;

public class WorkorderBulkUpdateUtil extends V3Util{

    public static void workorderBulkUpdateHandler(Map<String, Object> dataMap, String moduleName, Map<String, Object> bodyParams, List<Long> id,Map<String, List<Object>> queryParams,
                                                  Long stateTransitionId, Long customButtonId, Long approvalTransitionId, String qrValue, String locationValue,Map<String, Double>currentLocation) throws Exception{
        List<Map<String, Object>> rawRecords = (List<Map<String, Object>>) dataMap.get(moduleName);
        List<Long> ids = new ArrayList<>();
        FacilioContext summaryContext = V3Util.getSummary(moduleName, id,null,true);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summaryContext, moduleName);

        FacilioModule module = ChainUtil.getModule(moduleName);
        FacilioContext context = processWorkorderBulkRecords(module, moduleBaseWithCustomFields, rawRecords, bodyParams,queryParams, stateTransitionId,
                customButtonId, approvalTransitionId, qrValue,locationValue,currentLocation, false,false,id);

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    public static FacilioContext processWorkorderBulkRecords(FacilioModule module, List<ModuleBaseWithCustomFields> oldRecords, List<Map<String, Object>> rawRecords,
                                                                Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                                                Long stateTransitionId, Long customButtonId, Long approvalTransitionId, String qrValue, String locationValue,Map<String, Double>currentLocation,
                                                                boolean restrictedActions,boolean skipApproval,List <Long> id) throws Exception {
        Map<Long, Map<String, Object>> idVsRecordMap = new HashMap<>();
        for (ModuleBaseWithCustomFields record: oldRecords) {
            idVsRecordMap.put(record.getId(), FieldUtil.getAsJSON(record));
        }

        Map<String, FacilioField> numberAndDecimalFieldsWithMetrics = getNumberAndDecimalFieldsWithMetrics(module.getName());
        Set<String> keys= new HashSet<>();
        Map<String, Object> rec =new HashMap<>();
        for (Map<String, Object> record: rawRecords){
            rec=record;
            keys = rec.keySet();
        }
        for (Long recid:id)
        {
            Map<String,Object>jsonObject = idVsRecordMap.get(recid);
            for (String key : keys) {
                jsonObject.put(key, rec.get(key));
                if(numberAndDecimalFieldsWithMetrics.containsKey(key) && !keys.contains(key+"Unit")){
                    jsonObject.remove(key+"Unit");
                }
            }
        }
        List<Map<String, Object>> values = new ArrayList<>(idVsRecordMap.values());
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        List<Long> ids = oldRecords.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

        FacilioContext context = workorderUpdateBulkRecords(module, v3Config, true,oldRecords, values, ids, bodyParams,
                queryParams, stateTransitionId, customButtonId, approvalTransitionId, qrValue, locationValue, currentLocation, restrictedActions,skipApproval);
        return context;
    }

    private static FacilioContext workorderUpdateBulkRecords(FacilioModule module, V3Config v3Config, boolean bulkOp,
                                             List<ModuleBaseWithCustomFields>oldRecords,
                                             List<Map<String, Object>> recordList, List<Long> ids,
                                             Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                             Long stateTransitionId, Long customButtonId, Long approvalTransitionId,
                                             String qrValue, String locationValue,Map<String, Double>currentLocation,boolean onlyPermittedActions,boolean skipApproval
    ) throws Exception {
        FacilioChain patchChain;

        patchChain = getWorkorderUpdateBulkChain(module.getName(),true);
        FacilioContext context = patchChain.getContext();
        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);

        if (!bulkOp) {
            if (ids.size() > 0) {
                context.put(Constants.RECORD_ID, ids.get(0));
            }
        }
        Constants.setRecordIds(context, ids);

        Constants.setModuleName(context, module.getName());

        if (!bulkOp) {
            if (recordList.size() > 0) {
                Constants.setRawInput(context, recordList.get(0));
            }
        }
        Constants.setBulkRawInput(context, recordList);

        Constants.setBodyParams(context, bodyParams);
        Constants.addToOldRecordMap(context, module.getName(), oldRecords);
        context.put(Constants.BEAN_CLASS, beanClass);

        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        context.put(FacilioConstants.ContextNames.TRANSITION_ID, stateTransitionId);
        context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, approvalTransitionId);
        context.put(FacilioConstants.ContextNames.QR_VALUE,qrValue);
        context.put(FacilioConstants.ContextNames.LOCATION_VALUE,locationValue);
        context.put(FacilioConstants.ContextNames.CURRENT_LOCATION,currentLocation);
        context.put(FacilioConstants.ContextNames.ONLY_PERMITTED_ACTIONS, onlyPermittedActions);
        context.put(FacilioConstants.ContextNames.SKIP_APPROVAL, skipApproval);

        if (customButtonId != null && customButtonId > 0) {
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST, Collections.singletonList(customButtonId));
            CommonCommandUtil.addEventType(EventType.CUSTOM_BUTTON, context);
        }

        context.put(Constants.QUERY_PARAMS, queryParams);

        patchChain.execute();
        return context;
    }


    public static FacilioChain getWorkorderUpdateBulkChain (String moduleName,boolean isBulkOp) throws Exception{
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command initCommand = getWorkorderBulkUpdateInitChain() ;
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;
        Command activityCommand = new AddActivityForModuleDataCommand(CommonActivityType.UPDATE_RECORD);

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.UpdateHandler updateHandler = v3Config.getUpdateHandler();
            if (updateHandler != null) {
                if (!isBulkOp && updateHandler.getInitCommand() != null) {
                    initCommand = updateHandler.getInitCommand();
                }
                beforeSaveCommand = updateHandler.getBeforeSaveCommand();
                afterSaveCommand = updateHandler.getAfterSaveCommand();
                afterTransactionCommand = updateHandler.getAfterTransactionCommand();
                if (updateHandler.getActivitySaveCommand() != null) {
                    activityCommand = updateHandler.getActivitySaveCommand();
                }
            }
        }

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeSaveCommand);

        transactionChain.addCommand(new VerifyApprovalCommandV3());
        transactionChain.addCommand(new LocationFieldCRUDHandlerCommand());
        transactionChain.addCommand(new AddMultiSelectFieldsCommand());
        transactionChain.addCommand(new EvaluateFormValidationRuleCommand());
        transactionChain.addCommand(new UpdateCommand(module));
        transactionChain.addCommand(new UpdateTransactionEventTypeCommand());
        transactionChain.addCommand(new UpdateClassificationDataCommand());
        transactionChain.addCommand(activityCommand);
        transactionChain.addCommand(new DeleteSubModuleRecordCommand());
        transactionChain.addCommand(new DeleteSubFormLineItems());
        transactionChain.addCommand(new PatchSubFormCommand());
        transactionChain.addCommand(new PatchSubFormLineItemsCommand());
        transactionChain.addCommand(new SaveSubFormCommand());
        transactionChain.addCommand(new SaveSubFormFromLineItemsCommand());
        transactionChain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        transactionChain.addCommand(new UpdateStateForModuleDataCommand());

        addIfNotNull(transactionChain, afterSaveCommand);
        transactionChain.addCommand(new SendNotificationForOfflineRecordUpdate(OfflineUpdateType.RECORD));
        ChainUtil.updateWorkflowChain(transactionChain);
        transactionChain.addCommand(new ExecuteSpecificWorkflowsCommand(WorkflowRuleContext.RuleType.CUSTOM_BUTTON));

        addIfNotNull(transactionChain, afterTransactionCommand);

        transactionChain.addCommand(new AddActivitiesCommandV3());
        return transactionChain;
    }

    private static FacilioChain getWorkorderBulkUpdateInitChain() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new DefaultBulkInit());
        return transactionChain;
    }
    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

}
