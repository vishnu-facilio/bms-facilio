package com.facilio.v3;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler extends V3Action implements ServletRequestAware {
    private static final Logger LOGGER = Logger.getLogger(RESTAPIHandler.class.getName());

    private static final Map<String, V3Config> MODULE_HANDLER_MAP = new HashMap<>();

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

            V3Config config = (V3Config) method.invoke(null, null);

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

        if (!returnType.equals(V3Config.class)) {
            throw new IllegalStateException("Return type should be V3Config.");
        }
    }

    private  void handleSummaryRequest(String moduleName, long id) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        Command afterFetchCommand = null;
        if (v3Config != null) {
            V3Config.SummaryHandler summaryHandler = v3Config.getSummaryHandler();
            if (summaryHandler != null) {
                afterFetchCommand = summaryHandler.getAfterFetchCommand();
            }
        }

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new SummaryCommand(module));

        if (afterFetchCommand != null) {
            nonTransactionChain.addCommand(afterFetchCommand);
        }

        FacilioContext context = nonTransactionChain.getContext();

        context.put(Constants.RECORD_ID, id);

        Class beanClass = getBeanClass(module);
        context.put(Constants.BEAN_CLASS, beanClass);

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List list = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(list)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with " + id + " does not exist.");
        }

        Map<String, Object> result = new HashMap<>();

        result.put(moduleName, list.get(0));

        this.setData(FieldUtil.getAsJSON(result));
    }

    private Class getBeanClass(FacilioModule module) {
        Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
        if (beanClass == null) {
            if (module.isCustom()) {
                beanClass = CustomModuleData.class;
            }
            else {
                beanClass = ModuleBaseWithCustomFields.class;
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
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());

        if (v3Config != null) {
            V3Config.ListHandler listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                beforeFetchCommand = listHandler.getBeforeFetchCommand();
                afterFetchCommand = listHandler.getAfterFetchCommand();
            }
        }

        addIfNotNull(nonTransactionChain, beforeFetchCommand);

        nonTransactionChain.addCommand(new ListCommand(module));

        addIfNotNull(nonTransactionChain, afterFetchCommand);

        FacilioContext context = nonTransactionChain.getContext();

        context.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        String filters = this.getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(Constants.FILTERS, json);

            boolean includeParentFilter = this.getIncludeParentFilter();
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, includeParentFilter);
        }

        boolean overrideViewOrderBy = this.getOverrideViewOrderBy();
        Object orderBy = this.getOrderBy();
        Object orderByType = this.getOrderType();

        if (overrideViewOrderBy && orderBy != null) {
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

        Class beanClass = getBeanClass(module);
        context.put(Constants.BEAN_CLASS, beanClass);

        nonTransactionChain.execute();

        if (getWithCount()) {
            Map<String, Object> meta = new HashMap<>();
            meta.put("totalCount", context.get(Constants.COUNT));
            this.setMeta(FieldUtil.getAsJSON(meta));
        }

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        this.setData(FieldUtil.getAsJSON(recordMap));
    }

    private static V3Config getV3Config(String moduleName) {
        V3Config v3Config = MODULE_HANDLER_MAP.get(moduleName);
        return v3Config;
    }

    private static void addIfNotNull(FacilioChain chain, Command command) {
        if (command != null) {
            chain.addCommand(command);
        }
    }

    private void createHandler(String moduleName, Map<String, Object> createObj) throws Exception {
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

        addIfNotNull(transactionChain, afterSaveCommand);
        addIfNotNull(transactionChain, afterTransactionCommand);

        addWorkflowChain(transactionChain);

        FacilioContext context = transactionChain.getContext();

        if (module.isCustom()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField localIdField = modBean.getField("localId", moduleName);
            if (localIdField != null) {
                context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            }
        }

        context.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        context.put(Constants.MODULE_NAME, moduleName);
        context.put(Constants.RAW_INPUT, createObj);

        Class beanClass = getBeanClass(module);
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
        chain.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.MODULE_RULE_NOTIFICATION));
    }

    private void updateHandler(String moduleName, long id, Map<String, Object> updateObj) throws Exception {
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
                initCommand = updateHandler.getInitCommand();
                beforeSaveCommand = updateHandler.getBeforeSaveCommand();
                afterSaveCommand = updateHandler.getAfterSaveCommand();
                afterTransactionCommand = updateHandler.getAfterTransactionCommand();
            }
        }

        addIfNotNull(transactionChain, initCommand);
        addIfNotNull(transactionChain, beforeSaveCommand);

        transactionChain.addCommand(new UpdateCommand(module));

        addIfNotNull(transactionChain, afterSaveCommand);
        addIfNotNull(transactionChain, afterTransactionCommand);

        addWorkflowChain(transactionChain);

        FacilioContext context = transactionChain.getContext();

        context.put(Constants.RECORD_ID, id);
        context.put(Constants.MODULE_NAME, moduleName);
        context.put(Constants.RAW_INPUT, updateObj);

        Class beanClass = getBeanClass(module);
        context.put(Constants.BEAN_CLASS, beanClass);

        transactionChain.execute();

        summary();
    }

    private static void deleteHandler(String moduleName, long id) {

    }


    public String summary() throws Exception {
        try {
            handleSummaryRequest(this.getModuleName(), this.getId());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getErrorCode());

            LOGGER.log(Level.SEVERE, "exception handling summary request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);

            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getErrorCode());
            this.setMessage("Internal Server Error");

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
            this.setCode(ex.getErrorCode().getErrorCode());

            LOGGER.log(Level.SEVERE, "exception handling list request moduleName: " + this.getModuleName(), ex);

            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getErrorCode());
            this.setMessage("Internal Server Error");

            LOGGER.log(Level.SEVERE, "exception handling list request moduleName: " + this.getModuleName(), ex);
            return "failure";
        }

        return SUCCESS;
    }

    public String create() throws Exception {
        try {
            createHandler(this.getModuleName(), this.getData());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getErrorCode());

            LOGGER.log(Level.SEVERE, "exception handling create request moduleName: " + this.getModuleName(), ex);

            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getErrorCode());
            this.setMessage("Internal Server Error");

            LOGGER.log(Level.SEVERE, "exception handling create request moduleName: " + this.getModuleName(), ex);
            return "failure";
        }
        return SUCCESS;
    }

    public String update() throws Exception {
        try {
            updateHandler(this.getModuleName(), this.getId(), this.getData());
        } catch (RESTException ex) {
            this.setMessage(ex.getMessage());
            this.setCode(ex.getErrorCode().getErrorCode());

            LOGGER.log(Level.SEVERE, "exception handling update request moduleName: " + this.getModuleName() + " id: " + this.getId(), ex);

            return "failure";
        } catch (Exception ex) {
            this.setCode(ErrorCode.UNHANDLED_EXCEPTION.getErrorCode());
            this.setMessage("Internal Server Error");

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
}
