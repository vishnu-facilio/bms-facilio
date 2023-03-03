package com.facilio.v3.util;

import com.amazonaws.regions.Regions;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.picklist.HandleDefaultIdAndOrderByForPicklist;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.commands.AddOrUpdateSLABreachJobCommandV3;
import com.facilio.bmsconsoleV3.commands.ExecutePostTransactionWorkFlowsCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.VerifyApprovalCommandV3;
import com.facilio.bmsconsoleV3.context.CustomModuleDataFailureClassRelationship;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.command.AddClassificationDataCommand;
import com.facilio.classification.command.SummaryClassificationDataCommand;
import com.facilio.classification.command.UpdateClassificationDataCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.annotation.ModuleType;
import com.facilio.v3.commands.*;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.CustomModuleDataV3;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChainUtil {
    private static final Map<String, Supplier<V3Config>> MODULE_HANDLER_MAP = new HashMap<>();
    private static final Map<FacilioModule.ModuleType, Supplier<V3Config>> MODULE_TYPE_HANDLER_MAP = new HashMap<>();

    public static FacilioChain getFetchRecordChain(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(module);

        Command afterFetchCommand = null;
        Command beforeFetchCommand = null;
        List<SupplementRecord> supplementFields = null;
        if (v3Config != null) {
            V3Config.SummaryHandler summaryHandler = v3Config.getSummaryHandler();
            if (summaryHandler != null) {
                beforeFetchCommand = summaryHandler.getBeforeFetchCommand();
                afterFetchCommand = summaryHandler.getAfterFetchCommand();
                supplementFields = summaryHandler.getSupplementFields();
            }
        }

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();

        if (CollectionUtils.isNotEmpty(supplementFields)) {
            FacilioContext context = nonTransactionChain.getContext();
            List<SupplementRecord> list = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (list == null) {
                list = new ArrayList<>();
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, list);
            }
            list.addAll(supplementFields);
        }

        if (beforeFetchCommand != null) {
            nonTransactionChain.addCommand(beforeFetchCommand);
        }
        nonTransactionChain.addCommand(new AddCustomLookupInSupplementCommand(true));
        nonTransactionChain.addCommand(new FetchSysFields());
        nonTransactionChain.addCommand(new SummaryCommand(module));
        nonTransactionChain.addCommand(new SummaryClassificationDataCommand());
        nonTransactionChain.addCommand(new CheckContextTampering("getFetchRecordChain", "SummaryCommand", moduleName));
        if (afterFetchCommand != null) {
            nonTransactionChain.addCommand(afterFetchCommand);
            nonTransactionChain.addCommand(new CheckContextTampering("getFetchRecordChain", "afterFetchCommand", moduleName));
        }
        nonTransactionChain.addCommand(new TransformResponseForV4());
        //handling primary value for lookup fields
        nonTransactionChain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        nonTransactionChain.addCommand(new CheckContextTampering("getFetchRecordChain", "LookUpPrimaryFieldHandlingCommandV3", moduleName));
        //validating field permissions in the record data being sent
        nonTransactionChain.addCommand(new ValidateFieldPermissionCommand());
        nonTransactionChain.addCommand(new CheckContextTampering("getFetchRecordChain", "ValidateFieldPermissionCommand", moduleName));

        return nonTransactionChain;
    }

    public static FacilioChain getCountChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);


        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());

        V3Config v3Config = ChainUtil.getV3Config(module);
        Command beforeCountCommand = null;
        V3Config.ListHandler listHandler = null;
        if (v3Config != null) {
            listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                beforeCountCommand = listHandler.getBeforeCountCommand();
            }
        }
        
        addIfNotNull(nonTransactionChain, beforeCountCommand);
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());
        nonTransactionChain.addCommand(new CountCommand(module));
        return nonTransactionChain;
    }

    public static FacilioChain getListChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command beforeFetchCommand = null;
        Command afterFetchCommand = null;
        List<SupplementRecord> supplementFields = null;

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());
        V3Config.ListHandler listHandler = null;
        if (v3Config != null) {
            listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                beforeFetchCommand = listHandler.getBeforeFetchCommand();
                afterFetchCommand = listHandler.getAfterFetchCommand();
                supplementFields = listHandler.getSupplementFields();
            }
        }

        if (CollectionUtils.isNotEmpty(supplementFields)) {
            FacilioContext context = nonTransactionChain.getContext();
            List<SupplementRecord> list = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (list == null) {
                list = new ArrayList<>();
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, list);
            }
            list.addAll(supplementFields);
        }

        addIfNotNull(nonTransactionChain, beforeFetchCommand);

        nonTransactionChain.addCommand(new GenerateCriteriaForV4Command());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());
        nonTransactionChain.addCommand(new AddCustomLookupInSupplementCommand(false));
        nonTransactionChain.addCommand(new FetchSysFields());
        nonTransactionChain.addCommand(new FetchOnlyViewGroupColumnFieldsCommand());
        nonTransactionChain.addCommand(new ListCommand(module));

        if (listHandler != null && listHandler.isShowStateFlowList()) {
            nonTransactionChain.addCommand(new StateFlowListCommand());
        }
        nonTransactionChain.addCommand(new CustomButtonForDataListCommand());
        nonTransactionChain.addCommand(new SystemButtonForDataListCommand());

        addIfNotNull(nonTransactionChain, afterFetchCommand);
        //handling primary value for lookup fields
        nonTransactionChain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        //validating field permissions in the record data being sent
        nonTransactionChain.addCommand(new ValidateFieldPermissionCommand());

        // this should be last command always
        nonTransactionChain.addCommand(new SupplementsCommand());
        nonTransactionChain.addCommand(new FetchRelationsCommand());
        nonTransactionChain.addCommand(new TransformResponseForV4());

        return nonTransactionChain;
    }

    public  static FacilioChain getPickListChain(String moduleName)throws Exception {

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command beforeFetchCommand = null;
        Command afterFetchCommand = null;
        FacilioField mainField = null;
        FacilioField secondaryField = null;
        FacilioField subModuleField = null;

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        FacilioContext context = nonTransactionChain.getContext();
       
        V3Config.PickListHandler pickListHandler = null;
        if (v3Config != null) {
            pickListHandler = v3Config.getPickListHandler();
            if (pickListHandler != null) {
                beforeFetchCommand = pickListHandler.getBeforeFetchCommand();
                afterFetchCommand = pickListHandler.getAfterFetchCommand();
                mainField = pickListHandler.getMainField();
                secondaryField = pickListHandler.getSecondaryField();
                subModuleField = pickListHandler.getSubModuleField();
            }
        }

        if (mainField != null) {
            context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, mainField);
        }

        if (secondaryField != null) {
            context.put(FacilioConstants.PickList.SECONDARY_FIELD, secondaryField);
        }

        if (subModuleField != null) {
            context.put(FacilioConstants.PickList.SUBMODULE_FIELD, subModuleField);
        }

        nonTransactionChain.addCommand(new LoadViewCommand());
        nonTransactionChain.addCommand(new LoadMainFieldCommand());

        addIfNotNull(nonTransactionChain, beforeFetchCommand);

        nonTransactionChain.addCommand(new HandleDefaultIdAndOrderByForPicklist());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());

        nonTransactionChain.addCommand(new ListCommand(module));

        addIfNotNull(nonTransactionChain, afterFetchCommand);

        nonTransactionChain.addCommand(new ConstructPickListOptionCommand());

        return nonTransactionChain;

    }

    public static FacilioChain getCreateChain(String moduleName) throws Exception {
        return getCreateChain(moduleName, false);
    }

    public static FacilioChain getCreateChain(String moduleName, boolean bulkOp) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command initCommand = bulkOp ? new DefaultBulkInit() : new DefaultInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;
        Command activityCommand = new AddActivityForModuleDataCommand(CommonActivityType.ADD_RECORD);

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.CreateHandler createHandler = v3Config.getCreateHandler();
            if (createHandler != null) {
                if (createHandler.getInitCommand() != null) {
                    initCommand = createHandler.getInitCommand();
                }
                beforeSaveCommand = createHandler.getBeforeSaveCommand();
                afterSaveCommand = createHandler.getAfterSaveCommand();
                afterTransactionCommand = createHandler.getAfterTransactionCommand();
                if (createHandler.getActivitySaveCommand() != null) {
                    activityCommand = createHandler.getActivitySaveCommand();
                }
            }
        }

        addIfNotNull(transactionChain, initCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "initCommand", moduleName));
        addIfNotNull(transactionChain, beforeSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "beforeSaveCommand", moduleName));
        transactionChain.addCommand(new AddMultiSelectFieldsCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "AddMultiSelectFieldsCommand", moduleName));
        transactionChain.addCommand(new EvaluateFormValidationRuleCommand());
        transactionChain.addCommand(new SaveCommand(module));
        transactionChain.addCommand(new UpdateTransactionEventTypeCommand());
        transactionChain.addCommand(new AddClassificationDataCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "SaveCommand", moduleName));
        addIfNotNull(transactionChain, activityCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "activityCommand", moduleName));
        transactionChain.addCommand(new SaveSubFormCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "SaveSubFormCommand", moduleName));
        transactionChain.addCommand(new SaveSubFormFromLineItemsCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "SaveSubFormFromLineItemsCommand", moduleName));

        addIfNotNull(transactionChain, afterSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "afterSaveCommand", moduleName));

        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "afterTransactionCommand", moduleName));

        transactionChain.addCommand(new AddActivitiesCommandV3());

        return transactionChain;
    }

    public static FacilioChain getPreCreateChain(String moduleName)throws Exception{

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command initCommand = new DefaultBulkInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.PreCreateHandler preCreateHandler=v3Config.getPreCreateHandler();
            if (preCreateHandler != null) {
                if (preCreateHandler.getInitCommand() != null) {
                    initCommand = preCreateHandler.getInitCommand();
                }
                beforeSaveCommand = preCreateHandler.getBeforeSaveCommand();
                afterSaveCommand = preCreateHandler.getAfterSaveCommand();
            }
        }

        addIfNotNull(transactionChain, initCommand);
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "initCommand", moduleName));
        addIfNotNull(transactionChain, beforeSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "beforeSaveCommand", moduleName));
        transactionChain.addCommand(new AddMultiSelectFieldsCommand());
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "AddMultiSelectFieldsCommand", moduleName));
        transactionChain.addCommand(new SaveCommand(module));
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "SaveCommand", moduleName));
        transactionChain.addCommand(new SaveSubFormCommand());
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "SaveSubFormCommand", moduleName));
        transactionChain.addCommand(new SaveSubFormFromLineItemsCommand());
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "SaveSubFormFromLineItemsCommand", moduleName));

        addIfNotNull(transactionChain, afterSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getPreCreateRecordChain", "afterSaveCommand", moduleName));

        return transactionChain;

    }

    public static FacilioChain getPostCreateChain(String moduleName)throws Exception {

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;
        Command activityCommand = new AddActivityForModuleDataCommand(CommonActivityType.ADD_RECORD);

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.PostCreateHandler postCreateHandler = v3Config.getPostCreateHandler();
            if (postCreateHandler != null) {
                afterSaveCommand=postCreateHandler.getAfterSaveCommand();
                afterTransactionCommand = postCreateHandler.getAfterTransactionCommand();
                if (postCreateHandler.getActivitySaveCommand() != null) {
                    activityCommand = postCreateHandler.getActivitySaveCommand();
                }
            }
        }
        addIfNotNull(transactionChain, activityCommand);
        transactionChain.addCommand(new CheckContextTampering("getPostCreateRecordChain", "activityCommand", moduleName));
        addIfNotNull(transactionChain, afterSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getPostCreateRecordChain", "afterSaveCommand", moduleName));

        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);
        transactionChain.addCommand(new CheckContextTampering("getPostCreateRecordChain", "afterTransactionCommand", moduleName));

        transactionChain.addCommand(new AddActivitiesCommandV3());

        return transactionChain;

    }


    @Deprecated
    public static FacilioChain getUpdateChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);
        Command initCommand = new DefaultInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.UpdateHandler updateHandler = v3Config.getUpdateHandler();
            if (updateHandler != null) {
                if (updateHandler.getInitCommand() != null) {
                    initCommand = updateHandler.getInitCommand();
                }
                beforeSaveCommand = updateHandler.getBeforeSaveCommand();
                afterSaveCommand = updateHandler.getAfterSaveCommand();
                afterTransactionCommand = updateHandler.getAfterTransactionCommand();
            }
        }

        addIfNotNull(transactionChain, initCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "initCommand", moduleName));
        addIfNotNull(transactionChain, beforeSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain","beforeSaveCommand", moduleName));
        transactionChain.addCommand(new AddMultiSelectFieldsCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "AddMultiSelectFieldsCommand", moduleName));
        transactionChain.addCommand(new UpdateCommand(module));
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain","UpdateCommand", moduleName));
        transactionChain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "ChangeApprovalStatusForModuleDataCommand", moduleName));
        transactionChain.addCommand(new VerifyApprovalCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "VerifyApprovalCommand", moduleName));
        transactionChain.addCommand(new UpdateStateForModuleDataCommand());
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain","UpdateStateForModuleDataCommand", moduleName));

        addIfNotNull(transactionChain, afterSaveCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "UpdateStateForModuleDataCommand", moduleName));

        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);
        transactionChain.addCommand(new CheckContextTampering("getCreateRecordChain", "afterTransactionCommand", moduleName));

        return transactionChain;
    }

    public static FacilioChain getDeleteChain(String moduleName) throws Exception {
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        Command initCommand = new DefaultDeleteInit();
        Command beforeDeleteCommand = null;
        Command afterDeleteCommand = null;
        Command afterTransactionCommand = null;

        if (v3Config != null) {
            V3Config.DeleteHandler deleteHandler = v3Config.getDeleteHandler();
            if (deleteHandler != null) {
                if (deleteHandler.getInitCommand() != null) {
                    initCommand = deleteHandler.getInitCommand();
                }
                beforeDeleteCommand = deleteHandler.getBeforeDeleteCommand();
                afterDeleteCommand = deleteHandler.getAfterDeleteCommand();
                afterTransactionCommand = deleteHandler.getAfterTransactionCommand();
            }
        }

        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeDeleteCommand);
        transactionChain.addCommand(new DeleteCommand());
        transactionChain.addCommand(new UpdateTransactionEventTypeCommand());
        addIfNotNull(transactionChain, afterDeleteCommand);
        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);

        return transactionChain;
    }

    private static FacilioChain getBulkPatchInitChain() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new DefaultBulkInit());
        return transactionChain;
    }

    private static FacilioChain getPatchInitChain() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new DefaultInit());
        transactionChain.addCommand(new PatchSubModules());
        return transactionChain;
    }

    public static FacilioChain getBulkPatchChain(String moduleName) throws Exception {
        return getPatchChain(moduleName, true);
    }

    public static FacilioChain getPatchChain(String moduleName) throws Exception {
        return getPatchChain(moduleName, false);
    }

    private static FacilioChain getPatchChain(String moduleName, boolean isBulkOp) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command initCommand = isBulkOp? getBulkPatchInitChain() : getPatchInitChain();
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
        transactionChain.addCommand(new VerifyApprovalCommandV3());
        transactionChain.addCommand(new UpdateStateForModuleDataCommand());

        addIfNotNull(transactionChain, afterSaveCommand);
        updateWorkflowChain(transactionChain);
        // execute custom button action if the custom button id is sent
        transactionChain.addCommand(new ExecuteSpecificWorkflowsCommand(WorkflowRuleContext.RuleType.CUSTOM_BUTTON));

        addIfNotNull(transactionChain, afterTransactionCommand);

        transactionChain.addCommand(new AddActivitiesCommandV3());
        return transactionChain;
    }

    public static FacilioModule getModule(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (module == null) {
            //TODO proper excepition handling
            throw new IllegalArgumentException("Invalid module");
        }
        return module;
    }

    /**
     * Check whether particular module is v3 supported or not. For now any module that supports v3
     * should have valid v3Config entry.
     *
     * By default, all custom modules are v3 supported.
     *
     * This method will be deprecated once all the system modules supports v3.
     *
     * @param module
     * @return
     * @throws Exception
     */
    public static boolean isV3Enabled(FacilioModule module) throws Exception {
        if (module.isCustom()) {
            return true;
        }
        V3Config v3Config = getV3Config(module);
        return v3Config != null && v3Config.canUseInScript();
    }

    public static V3Config getV3Config(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        Objects.requireNonNull(module, "Invalid module for v3 config fetch");
        return getV3Config(module);
    }

    public static V3Config getV3Config (FacilioModule module) {
        return getV3Config(module, true);
    }

    public static V3Config getV3Config (FacilioModule module, boolean checkParent) {
        Objects.requireNonNull(module, "Invalid module for v3 config fetch");
//        FacilioModule currentModule = module;
        Supplier<V3Config> v3Config = findV3Config(MODULE_HANDLER_MAP, (m) -> m.getName(), checkParent, module);
        if (v3Config == null) {
            v3Config = findV3Config(MODULE_TYPE_HANDLER_MAP, (m) -> m.getTypeEnum(), checkParent, module);
        }

        if (v3Config == null) {
            return null;
        }
        return v3Config.get();
    }

    private static <T> Supplier<V3Config> findV3Config(Map<T, Supplier<V3Config>> moduleHandlerMap, Function<FacilioModule, T> function, boolean checkParent, FacilioModule currentModule) {
        Supplier<V3Config> v3Config = moduleHandlerMap.get(function.apply(currentModule));

        if (v3Config == null && checkParent) {
            currentModule = currentModule.getExtendModule();
            while (currentModule != null && v3Config == null) {
                v3Config = moduleHandlerMap.get(function.apply(currentModule));
                currentModule = currentModule.getExtendModule();
            }
        }
        return v3Config;
    }

    public static void initRESTAPIHandler(String packageName) throws InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName), new MethodAnnotationsScanner());
        fillModuleNameMap(reflections);
        fillModuleTypeMap(reflections);
    }

    private static void fillModuleTypeMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithModuleType = reflections.getMethodsAnnotatedWith(ModuleType.class);
        for (Method method: methodsAnnotatedWithModuleType) {
            validateHandlerMethod(method);

            ModuleType annotation = method.getAnnotation(ModuleType.class);
            FacilioModule.ModuleType moduleType = annotation.type();
            if (moduleType == null) {
                throw new IllegalStateException("Module type cannot be empty.");
            }

            Supplier<V3Config> config = (Supplier<V3Config>) method.invoke(null, null);

            if (MODULE_TYPE_HANDLER_MAP.containsKey(moduleType)) {
                throw new IllegalStateException("Module config already present.");
            }

            MODULE_TYPE_HANDLER_MAP.put(moduleType, config);
        }
    }

    private static void fillModuleNameMap(Reflections reflections) throws InvocationTargetException, IllegalAccessException {
        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(Module.class);
        for (Method method: methodsAnnotatedWithModule) {
            validateHandlerMethod(method);

            Module annotation = method.getAnnotation(Module.class);
            String moduleName = annotation.value().trim();
            if (moduleName.isEmpty()) {
                throw new IllegalStateException("Module name cannot be empty.");
            }

            Supplier<V3Config> config = (Supplier<V3Config>) method.invoke(null, null);

            if (MODULE_HANDLER_MAP.containsKey(moduleName)) {
                throw new IllegalStateException("Module config already present.");
            }

            MODULE_HANDLER_MAP.put(moduleName, config);
        }
    }

    private static void validateHandlerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Module annotation should be part of " + declaringClass.getName() + " Config class.");
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (!(genericParameterTypes == null || genericParameterTypes.length == 0)) {
            throw new IllegalStateException("Method should not have parameters");
        }

        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Supplier.class)) {
            throw new IllegalStateException("Return type should be Supplier<V3Config>.");
        }
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    public static void addWorkflowChain(Chain chain) throws Exception {
        chain.addCommand(new ExecuteStateFlowCommand());
		chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.SATISFACTION_SURVEY_RULE));
		chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.SURVEY_ACTION_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
        chain.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        if (Regions.US_WEST_2.getName().equals(FacilioProperties.getRegion()) && DBConf.getInstance().getCurrentOrgId() == 592) {
            chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
        }
        else {
            if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PUSHNOTIFICATION_WMS)) {
                chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
            }
            else {
                chain.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                        .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION))
                );
            }
        }
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.TRANSACTION_RULE));
        chain.addCommand(new ExecuteSLAWorkFlowsCommand());
        chain.addCommand(new AddOrUpdateSLABreachJobCommandV3(true));
        chain.addCommand(new ExecuteRollUpFieldCommand());
    }

    public static void updateWorkflowChain(Chain chain) throws Exception {
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.SATISFACTION_SURVEY_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.SURVEY_ACTION_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
        chain.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PUSHNOTIFICATION_WMS))
        {
            chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
        }
        else {
            chain.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                    .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION))
            );
        }
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.TRANSACTION_RULE));
        chain.addCommand(new AddOrUpdateSLABreachJobCommandV3(false));
        chain.addCommand(new ExecuteSLACommitmentWorkflowsCommand());
        chain.addCommand(new ExecuteRollUpFieldCommand());
    }

    public static Class getBeanClass(V3Config config, FacilioModule module) {
        Class beanClass = null;
        if (config != null) {
            beanClass = config.getBeanClass();
        }
        if (beanClass == null) {
            if (module.getTypeEnum() == FacilioModule.ModuleType.CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP) {
                return CustomModuleDataFailureClassRelationship.class;
            }
            if (module.isCustom()) {
                if (module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                    beanClass = AttachmentV3Context.class;
                } else {
                    beanClass = CustomModuleDataV3.class;
                }
            } else {
                beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
                if (beanClass == null) {
                    if (module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                        beanClass = AttachmentV3Context.class;
                    } else {
                        beanClass = V3Context.class;
                    }
                }
            }
        }
        return beanClass;
    }
    public static FacilioChain getTimelinePatchValidationChain() throws Exception {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new LoadViewCommand());
        chain.addCommand(new ValidateTimelinePatchData());
        return chain;
    }

    public static FacilioChain getTimelineChain() throws Exception {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GenerateCriteriaFromFilterCommand());
        chain.addCommand(new LoadViewCommand());
        chain.addCommand(new GetTimeLineDataCommand());
        chain.addCommand(new AddCustomizationToRecordMap());
        chain.addCommand(new ConstructTimelineResponseCommand());
        return chain;
    }

    public static FacilioChain getTimelineListChain() throws Exception {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GenerateCriteriaFromFilterCommand());
        chain.addCommand(new LoadViewCommand());
        chain.addCommand(new GetTimeLineListCommand());
        chain.addCommand(new AddCustomizationToRecordMap());
        return chain;
    }

    public static FacilioChain getCalendarViewChain(String moduleName, boolean getSingleCellData) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command beforeFetchCommand = null;
        Command afterFetchCommand = null;
        V3Config.ListHandler listHandler = null;
        List<SupplementRecord> supplementFields = null;

        if (v3Config != null) {
            listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                beforeFetchCommand = listHandler.getBeforeFetchCommand();
                afterFetchCommand = listHandler.getAfterFetchCommand();
                supplementFields = listHandler.getSupplementFields();
            }
        }

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());

        if (CollectionUtils.isNotEmpty(supplementFields)) {
            FacilioContext context = nonTransactionChain.getContext();
            List<SupplementRecord> list = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
            if (list == null) {
                list = new ArrayList<>();
                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, list);
            }
            list.addAll(supplementFields);
        }

        addIfNotNull(nonTransactionChain, beforeFetchCommand);

        nonTransactionChain.addCommand(new GenerateCriteriaForV4Command());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromClientCriteriaCommand());
        nonTransactionChain.addCommand(new AddCustomLookupInSupplementCommand(false));
        nonTransactionChain.addCommand(new FetchSysFields());
        nonTransactionChain.addCommand(new GetSelectiveFieldsCommand());

        if (getSingleCellData) {
            nonTransactionChain.addCommand(new CalendarViewListCommand());
        } else {
            nonTransactionChain.addCommand(new CalendarViewDataAggregationCommand());
            nonTransactionChain.addCommand(new CalendarViewDataCommand());
        }

        addIfNotNull(nonTransactionChain, afterFetchCommand);

        nonTransactionChain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        nonTransactionChain.addCommand(new ValidateFieldPermissionCommand());
        nonTransactionChain.addCommand(new SupplementsCommand());

        nonTransactionChain.addCommand(new AddCalendarViewCustomizationCommand());
        if (!getSingleCellData) {
            nonTransactionChain.addCommand(new ConstructCalendarViewResponseCommand());
        }

        return nonTransactionChain;
    }
}
