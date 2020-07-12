package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.ExecutePostTransactionWorkFlowsCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.*;
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
import java.util.Set;
import java.util.function.Supplier;

public class ChainUtil {
    private static final Map<String, Supplier<V3Config>> MODULE_HANDLER_MAP = new HashMap<>();

    public static FacilioChain getFetchRecordChain(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

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
        nonTransactionChain.addCommand(new SummaryCommand(module));
        if (afterFetchCommand != null) {
            nonTransactionChain.addCommand(afterFetchCommand);
        }

        //handling primary value for lookup fields
        nonTransactionChain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        //validating field permissions in the record data being sent
        nonTransactionChain.addCommand(new ValidateFieldPermissionCommand());

        return nonTransactionChain;
    }

    public static FacilioChain getListChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

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

        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());
        nonTransactionChain.addCommand(new ListCommand(module));

        if (listHandler != null && listHandler.isShowStateFlowList()) {
            nonTransactionChain.addCommand(new StateFlowListCommand());
        }

        addIfNotNull(nonTransactionChain, afterFetchCommand);
        //handling primary value for lookup fields
        nonTransactionChain.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        //validating field permissions in the record data being sent
        nonTransactionChain.addCommand(new ValidateFieldPermissionCommand());

        // this should be last command always
        nonTransactionChain.addCommand(new SupplementsCommand());

        return nonTransactionChain;
    }

    public static FacilioChain getCreateRecordChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        Command initCommand = new DefaultInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;

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
            }
        }

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeSaveCommand);

        transactionChain.addCommand(new SaveCommand(module));
        transactionChain.addCommand(new SaveSubFormCommand());

        addIfNotNull(transactionChain, afterSaveCommand);

        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);

        return transactionChain;
    }

    public static FacilioChain getUpdateChain(String moduleName) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
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
        addIfNotNull(transactionChain, beforeSaveCommand);

        transactionChain.addCommand(new UpdateCommand(module));

        transactionChain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        transactionChain.addCommand(new VerifyApprovalCommand());
        transactionChain.addCommand(new UpdateStateForModuleDataCommand());

        addIfNotNull(transactionChain, afterSaveCommand);

        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);

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
                initCommand = deleteHandler.getInitCommand();
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

    public static FacilioChain getPatchChain(String moduleName) throws Exception {
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        FacilioModule module = ChainUtil.getModule(moduleName);

        Command initCommand = new DefaultInit();
        Command beforeSaveCommand = null;
        Command afterSaveCommand = null;
        Command afterTransactionCommand = null;

        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (v3Config != null) {
            V3Config.UpdateHandler updateHandler = v3Config.getUpdateHandler();
            if (updateHandler != null)
            {
                if (updateHandler.getInitCommand() != null) {
                    initCommand = updateHandler.getInitCommand();
                }
                beforeSaveCommand = updateHandler.getBeforeSaveCommand();
                afterSaveCommand = updateHandler.getAfterSaveCommand();
                afterTransactionCommand = updateHandler.getAfterTransactionCommand();
            }
        }

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeSaveCommand);

        transactionChain.addCommand(new UpdateCommand(module));
        transactionChain.addCommand(new ChangeApprovalStatusForModuleDataCommand());
        transactionChain.addCommand(new VerifyApprovalCommand());
        transactionChain.addCommand(new UpdateStateForModuleDataCommand());

        addIfNotNull(transactionChain, afterSaveCommand);
        addWorkflowChain(transactionChain);
        addIfNotNull(transactionChain, afterTransactionCommand);
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

    public static V3Config getV3Config(String moduleName) {
        Supplier<V3Config> v3Config = MODULE_HANDLER_MAP.get(moduleName);
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
            throw new IllegalStateException("Module annotation should be part of Config class.");
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
}
