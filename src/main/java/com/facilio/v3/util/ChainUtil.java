package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.commands.ExecutePostTransactionWorkFlowsCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.VerifyApprovalCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.*;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.CustomModuleDataV3;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class ChainUtil {
    private static final Map<String, Supplier<V3Config>> MODULE_HANDLER_MAP = new HashMap<>();

    public static FacilioChain getFetchRecordChain(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(module);

        Command afterFetchCommand = null;
        Command beforeFetchCommand = null;
        if (v3Config != null) {
            V3Config.SummaryHandler summaryHandler = v3Config.getSummaryHandler();
            if (summaryHandler != null) {
                beforeFetchCommand = summaryHandler.getBeforeFetchCommand();
                afterFetchCommand = summaryHandler.getAfterFetchCommand();
            }
        }

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        if (beforeFetchCommand != null) {
            nonTransactionChain.addCommand(beforeFetchCommand);
        }
        nonTransactionChain.addCommand(new AddCustomLookupInSupplementCommand(true));
        nonTransactionChain.addCommand(new FetchSysFields());
        nonTransactionChain.addCommand(new SummaryCommand(module));
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
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());
        nonTransactionChain.addCommand(new CountCommand(module));
        return nonTransactionChain;
    }

    public static FacilioChain getListChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(module);

        Command beforeFetchCommand = null;
        Command afterFetchCommand = null;

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());
        V3Config.ListHandler listHandler = null;
        if (v3Config != null) {
            listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                beforeFetchCommand = listHandler.getBeforeFetchCommand();
                afterFetchCommand = listHandler.getAfterFetchCommand();
            }
        }

        addIfNotNull(nonTransactionChain, beforeFetchCommand);

        nonTransactionChain.addCommand(new GenerateCriteriaForV4Command());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());
        nonTransactionChain.addCommand(new AddCustomLookupInSupplementCommand(false));
        nonTransactionChain.addCommand(new FetchSysFields());
        nonTransactionChain.addCommand(new ListCommand(module));

        if (listHandler != null && listHandler.isShowStateFlowList()) {
            nonTransactionChain.addCommand(new StateFlowListCommand());
        }
        nonTransactionChain.addCommand(new CustomButtonForDataListCommand());

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
        transactionChain.addCommand(new SaveCommand(module));
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
        addIfNotNull(transactionChain, afterDeleteCommand);
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
        transactionChain.addCommand(new UpdateCommand(module));
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
        addWorkflowChain(transactionChain);
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
        FacilioModule currentModule = module;
        Supplier<V3Config> v3Config = MODULE_HANDLER_MAP.get(currentModule.getName());

        if (v3Config == null && checkParent) {
            currentModule = currentModule.getExtendModule();
            while (currentModule != null && v3Config == null) {
                v3Config = MODULE_HANDLER_MAP.get(currentModule.getName());
                currentModule = currentModule.getExtendModule();
            }
        }

        if (v3Config == null) {
            return null;
        }
        return v3Config.get();
    }

    public static void initRESTAPIHandler(String packageName) throws InvocationTargetException, IllegalAccessException {
        Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName), new MethodAnnotationsScanner());
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

    public static void addWorkflowChain(Chain chain) {
        chain.addCommand(new ExecuteStateFlowCommand());
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
        chain.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        chain.addCommand(new ExecutePostTransactionWorkFlowsCommandV3()
                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION))
        );
        chain.addCommand(new ExecuteRollUpFieldCommand());
    }

    public static Class getBeanClass(V3Config config, FacilioModule module) {
        Class beanClass = null;
        if (config != null) {
            beanClass = config.getBeanClass();
        }
        if (beanClass == null) {
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
                    }
                	else {
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
}
