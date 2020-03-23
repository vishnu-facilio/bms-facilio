package com.facilio.v3;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GenerateSearchConditionCommand;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import java.util.stream.Collectors;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler extends V3Action implements ServletRequestAware {
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

    private static Map<String, Object> handleSummaryRequest(String moduleName, long id) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);
        V3Config.SummaryHandler summaryHandler = v3Config.getSummaryHandler();

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new SummaryCommand(module));
        if (summaryHandler != null && summaryHandler.getAfterFetchCommand() != null) {
            nonTransactionChain.addCommand(summaryHandler.getAfterFetchCommand());
        }

        FacilioContext context = nonTransactionChain.getContext();

        context.put(FacilioConstants.ContextNames.RECORD_ID, id);

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        return null;
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

    private List<Map<String, Object>> handleListRequest(String moduleName) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        FacilioChain nonTransactionChain = FacilioChain.getNonTransactionChain();
        nonTransactionChain.addCommand(new LoadViewCommand());
        nonTransactionChain.addCommand(new GenerateCriteriaFromFilterCommand());
        nonTransactionChain.addCommand(new GenerateSearchConditionCommand());

        if (v3Config.getListHandler() != null && v3Config.getListHandler().getBeforeFetchCommand() != null) {
            nonTransactionChain.addCommand(v3Config.getListHandler().getBeforeFetchCommand());
        }

        nonTransactionChain.addCommand(new ListCommand(module));

        if (v3Config.getListHandler() != null && v3Config.getListHandler().getAfterFetchCommand() != null) {
            nonTransactionChain.addCommand(v3Config.getListHandler().getAfterFetchCommand());
        }

        FacilioContext context = nonTransactionChain.getContext();

        List<FacilioField> fieldsByAccessType = FieldUtil.getFieldsByAccessType((long) FacilioField.AccessType.CRITERIA.getIntVal(), moduleName);
        String search = this.getSearch();
        if (search != null && !CollectionUtils.isEmpty(fieldsByAccessType)) {
            JSONObject searchObj = new JSONObject();
            searchObj.put("fields", fieldsByAccessType.stream().map(field -> moduleName + "." + field.getName()).collect(Collectors.joining(",")));
            searchObj.put("query", search);
            context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
        }

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

        nonTransactionChain.execute();

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        return null;
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

    private static void createHandler(String moduleName, Map<String, Object> createObj) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);
        V3Config.CreateHandler createHandler = null;

        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        if (!module.isCustom()) {
            if (v3Config == null) {
                throw new IllegalArgumentException("not a valid module");
            }

            createHandler = v3Config.getCreateHandler();

            if (createHandler == null) {
                //TODO unsupported operation
                throw new IllegalArgumentException("unsupported operation");
            }

            addIfNotNull(transactionChain, createHandler.getInitCommand());
            addIfNotNull(transactionChain, createHandler.getBeforeSaveCommand());
        } else {
            addIfNotNull(transactionChain, new DefaultInit());
        }

        transactionChain.addCommand(new SaveCommand(module));

        if (createHandler != null) {
            addIfNotNull(transactionChain, createHandler.getAfterSaveCommand());
            addIfNotNull(transactionChain, createHandler.getAfterTransactionCommand());
        }

        FacilioContext context = transactionChain.getContext();

        context.put(Constants.MODULE_NAME, moduleName);
        context.put(Constants.RAW_INPUT, createObj);
        transactionChain.execute();
    }

    private static void updateHandler(String moduleName, long id, Map<String, Object> updateObj) throws Exception {
        FacilioModule module = getModule(moduleName);
        V3Config v3Config = getV3Config(moduleName);

        V3Config.UpdateHandler updateHandler = null;
        FacilioChain transactionChain = FacilioChain.getTransactionChain();

        if (!module.isCustom()) {
            if (v3Config == null) {
                throw new IllegalArgumentException("not a valid module");
            }

            updateHandler = v3Config.getUpdateHandler();

            if (updateHandler != null) {
                addIfNotNull(transactionChain, updateHandler.getInitCommand());
                addIfNotNull(transactionChain, updateHandler.getBeforeSaveCommand());
            }

            if (updateHandler == null) {
                //TODO unsupported operation
                throw new IllegalArgumentException("unsupported operation");
            }
        } else {
            addIfNotNull(transactionChain, new DefaultInit());
        }

        transactionChain.addCommand(new UpdateCommand(module));

        if (updateHandler != null) {
            addIfNotNull(transactionChain, updateHandler.getAfterSaveCommand());
            addIfNotNull(transactionChain, updateHandler.getAfterTransactionCommand());
        }

        FacilioContext context = transactionChain.getContext();

        context.put(Constants.RECORD_ID, id);
        context.put(Constants.MODULE_NAME, moduleName);
        context.put(Constants.RAW_INPUT, updateObj);

        transactionChain.execute();
    }

    private static void deleteHandler(String moduleName, long id) {

    }


    public String summary() throws Exception {
        handleSummaryRequest(this.getModuleName(), this.getId());
        return SUCCESS;
    }

    public String list() throws Exception {
        handleListRequest(this.getModuleName());
        return SUCCESS;
    }

    public String create() throws Exception {
        createHandler(this.getModuleName(), this.getData());
        return SUCCESS;
    }

    public String update() throws Exception {
        updateHandler(this.getModuleName(), this.getId(), this.getData());
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
