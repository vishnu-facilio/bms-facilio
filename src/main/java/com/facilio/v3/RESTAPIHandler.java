package com.facilio.v3;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler extends V3Action implements ServletRequestAware, ServletResponseAware {
    private static final Logger LOGGER = Logger.getLogger(RESTAPIHandler.class.getName());

    private static final Map<String, Supplier<V3Config>> MODULE_HANDLER_MAP = new HashMap<>();

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

    private  void handleSummaryRequest(String moduleName, long id) throws Exception {
        Object record = getRecord(moduleName, id);
        Map<String, Object> result = new HashMap<>();
        result.put(moduleName, record);
        this.setData(FieldUtil.getAsJSON(result));
    }

    private Object getRecord(String moduleName, long id) throws Exception {
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

        FacilioContext context = nonTransactionChain.getContext();

        context.put(Constants.RECORD_ID, id);
        context.put(Constants.QUERY_PARAMS, getQueryParameters());
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);
        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        Constants.setModuleName(context, moduleName);

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List list = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(list)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }
        return list.get(0);
    }

    private Class getBeanClass(V3Config config, FacilioModule module) {
        Class beanClass = null;
        if (config != null) {
            beanClass = config.getBeanClass();
        }
        if (beanClass == null) {
            beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
            if (beanClass == null) {
                if (module.isCustom()) {
                    beanClass = CustomModuleData.class;
                } else {
                    beanClass = ModuleBaseWithCustomFields.class;
                }
            }
        }
        return beanClass;
    }

    private static FacilioModule getModule(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (module == null) {
            //TODO proper excepition handling
            throw new IllegalArgumentException("Invalid module");
        }
        return module;
    }

    private void handleListRequest(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

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

        FacilioContext context = nonTransactionChain.getContext();

        context.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);

        String filters = this.getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(Constants.FILTERS, json);

            boolean excludeParentFilter = this.getExcludeParentFilter();
            context.put(Constants.EXCLUDE_PARENT_CRITERIA, excludeParentFilter);
        }

        Object orderBy = this.getOrderBy();
        Object orderByType = this.getOrderType();

        if (orderBy != null) {
            JSONObject sorting = new JSONObject();
            sorting.put("orderBy", orderBy);
            sorting.put("orderType", orderByType);
            context.put(FacilioConstants.ContextNames.SORTING, sorting);
            context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
        }

        JSONObject pagination = new JSONObject();
        pagination.put("page", this.getPage());
        pagination.put("perPage", this.getPerPage());

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(Constants.WITH_COUNT, getWithCount());
        context.put(Constants.QUERY_PARAMS, getQueryParameters());

        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        nonTransactionChain.execute();

        Map<String, Object> meta = new HashMap<>();
        if (getWithCount()) {
            Map<String, Object> paging = new HashMap<>();
            paging.put("totalCount", context.get(Constants.COUNT));
            meta.put("pagination", paging);
        }

        Map<String, List<WorkflowRuleContext>> stateFlows = (Map<String, List<WorkflowRuleContext>>) context.get(Constants.STATE_FLOWS);
        if (MapUtils.isNotEmpty(stateFlows)) {
            meta.put("stateflows", stateFlows);
        }

        Object supplMap = Constants.getSupplementMap(context);
        if (supplMap != null) {
            meta.put("supplements", supplMap);
        }

        JSONObject recordJSON = Constants.getJsonRecordMap(context);
        this.setData(recordJSON);

        if (MapUtils.isNotEmpty(meta)) {
            this.setMeta(FieldUtil.getAsJSON(meta));
        }
    }

    private static V3Config getV3Config(String moduleName) {
        Supplier<V3Config> v3Config = MODULE_HANDLER_MAP.get(moduleName);
        if (v3Config == null) {
            return null;
        }
        return v3Config.get();
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    private void createHandler(String moduleName, Map<String, Object> createObj, Map<String, Object> bodyParams) throws Exception {
        FacilioModule module = getModule(moduleName);

        V3Config v3Config = getV3Config(moduleName);
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



        FacilioContext context = transactionChain.getContext();

        if (module.isCustom()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField localIdField = modBean.getField("localId", moduleName);
            if (localIdField != null) {
                context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            }
        }

        context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        Constants.setModuleName(context, moduleName);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        Constants.setRawInput(context, createObj);
        Constants.setBodyParams(context, bodyParams);
        context.put(Constants.QUERY_PARAMS, getQueryParameters());

        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        transactionChain.execute();

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
        long id = recordMap.get(moduleName).get(0).getId();
        this.setId(id);
        summary();
    }

    private static void addWorkflowChain(Chain chain) {
        chain.addCommand(new ExecuteStateFlowCommand());
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE));
        chain.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.APPROVAL_STATE_FLOW));
        chain.addCommand(new ForkChainToInstantJobCommand()
                .addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION)));
        chain.addCommand(new ExecuteRollUpFieldCommand());
    }



    private void patchHandler(String moduleName, long id, Map<String, Object> patchObj, Map<String, Object> bodyParams) throws Exception {
        Object record = getRecord(moduleName, id);

        FacilioModule module = getModule(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> modFields = modBean.getAllFields(moduleName);

        Set<String> fieldNames = patchObj.keySet();
        Map<String, FacilioField> fieldAsMap = FieldFactory.getAsMap(modFields);


        List<FacilioField> patchedFields = new ArrayList<>();
        for (String fieldName: fieldNames) {
            FacilioField field = fieldAsMap.get(fieldName);
            if (field != null) {
                patchedFields.add(field);
            }
        }

        V3Config v3Config = getV3Config(moduleName);

        Class beanClass = getBeanClass(v3Config, module);
        List<Map<String, Object>> converted = FieldUtil.getAsMapList(Collections.singletonList(record), beanClass);

        JSONObject summaryRecord = FieldUtil.getAsJSON(converted.get(0));

        Set<String> keys = patchObj.keySet();
        for (String key: keys) {
            summaryRecord.put(key, patchObj.get(key));
        }

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


        FacilioContext context = transactionChain.getContext();

        context.put(Constants.RECORD_ID, id);
        Constants.setModuleName(context, moduleName);
        Constants.setRawInput(context, summaryRecord);
        Constants.setBodyParams(context, bodyParams);
//        context.put(Constants.PATCH_FIELDS, patchedFields);
        context.put(Constants.BEAN_CLASS, beanClass);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        context.put(FacilioConstants.ContextNames.TRANSITION_ID, this.getStateTransitionId());
        context.put(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID, this.getApprovalTransitionId());
        context.put(Constants.QUERY_PARAMS, getQueryParameters());

        transactionChain.execute();

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }

        summary();
    }

    private void updateHandler(String moduleName, long id, Map<String, Object> updateObj, Map<String, Object> bodyParams) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);
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

        FacilioContext context = transactionChain.getContext();

        context.put(Constants.RECORD_ID, id);
        Constants.setModuleName(context, moduleName);
        Constants.setRawInput(context, updateObj);
        Constants.setBodyParams(context, bodyParams);
        context.put(Constants.QUERY_PARAMS, getQueryParameters());
         context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        transactionChain.execute();

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }

        summary();
    }


    public String summary() throws Exception {
        try {
            handleSummaryRequest(this.getModuleName(), this.getId());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getCode());
            this.httpServletResponse.setStatus(ex.getErrorCode().getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling summary request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error");
            this.httpServletResponse.setStatus(ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling summary request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        }
        return SUCCESS;
    }

    public String list() throws Exception {
        try {
            handleListRequest(this.getModuleName());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getCode());
            this.httpServletResponse.setStatus(ex.getErrorCode().getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling list request moduleName: " + this.getModuleName(), ex);
            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error");
            this.httpServletResponse.setStatus(ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling list request moduleName: " + this.getModuleName(), ex);
            return "failure";
        }

        return SUCCESS;
    }

    public String create() throws Exception {
        try {
            //removing permission restricted fields
            removeRestrictedFields(this.getData(), this.getModuleName(), true);

            createHandler(this.getModuleName(), this.getData(), this.getParams());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getCode());
            this.httpServletResponse.setStatus(ex.getErrorCode().getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling create request moduleName: " + this.getModuleName(), ex);
            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error");
            this.httpServletResponse.setStatus(ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling create request moduleName: " + this.getModuleName(), ex);
            return "failure";
        }
        this.httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        return SUCCESS;
    }

    public String update() throws Exception {
        try {
            updateHandler(this.getModuleName(), this.getId(), this.getData(), this.getParams());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getCode());
            this.httpServletResponse.setStatus(ex.getErrorCode().getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling update request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error");
            this.httpServletResponse.setStatus(ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling update request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        }
        return SUCCESS;
    }

    public String patch() throws Exception {
        try {
            //removing permission restricted fields
            removeRestrictedFields(this.getData(), this.getModuleName(), true);
            patchHandler(this.getModuleName(), this.getId(), this.getData(), this.getParams());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getCode());
            this.httpServletResponse.setStatus(ex.getErrorCode().getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling update request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getCode());
            this.setMessage("Internal Server Error");
            this.httpServletResponse.setStatus(ErrorCode.UNHANDLED_EXCEPTION.getHttpStatus());
            LOGGER.log(Level.SEVERE, "exception handling update request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);
            return "failure";
        }
        return SUCCESS;
    }

    public Map<String, List<Object>> getQueryParameters() {
        Map<String, List<Object>> queryParameters = new HashMap<>();
        String queryString = this.httpServletRequest.getQueryString();

        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            List<Object> values = queryParameters.get(keyValuePair[0]);
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(keyValuePair.length == 1 ? "" : keyValuePair[1]);
            queryParameters.put(keyValuePair[0], values);
        }
        return queryParameters;
    }

    private HttpServletRequest httpServletRequest;

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private HttpServletResponse httpServletResponse;

    @Override
    public void setServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    private void removeRestrictedFields(Map<String, Object> dataMap, String moduleName, Boolean validatePermissions) throws Exception{
        FacilioModule module = getModule(moduleName);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FIELD_PERMISSIONS)) {
            List<FacilioField> restrictedFields = FieldUtil.getPermissionRestrictedFields(module,
                    FieldPermissionContext.PermissionType.READ_WRITE, validatePermissions);
            if (CollectionUtils.isNotEmpty(restrictedFields)) {
                for (FacilioField field : restrictedFields) {
                    if(field != null) {
                        dataMap.remove(field.getName());
                    }
                }
            }
        }
    }
}
