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
import com.facilio.v3.commands.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler extends V3Action implements ServletRequestAware, ServletResponseAware {
    private static final Logger LOGGER = Logger.getLogger(RESTAPIHandler.class.getName());

    private  void handleSummaryRequest(String moduleName, long id) throws Exception {
        Object record = getRecord(moduleName, id);
        Map<String, Object> result = new HashMap<>();
        result.put(moduleName, record);
        this.setData(FieldUtil.getAsJSON(result));
    }


    private Object getRecord(String moduleName, long id) throws Exception {
        FacilioChain fetchRecordChain = ChainUtil.getFetchRecordChain(moduleName);
        FacilioContext context = fetchRecordChain.getContext();

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config config = ChainUtil.getV3Config(moduleName);

        context.put(Constants.RECORD_ID, id);
        context.put(Constants.QUERY_PARAMS, getQueryParameters());
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);
        Class beanClass = getBeanClass(config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        Constants.setModuleName(context, moduleName);

        fetchRecordChain.execute();

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




    private void handleListRequest(String moduleName) throws Exception {
        FacilioChain listChain = ChainUtil.getListChain(moduleName);
        FacilioContext context = listChain.getContext();

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

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        listChain.execute();
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


    private void createHandler(String moduleName, Map<String, Object> createObj, Map<String, Object> bodyParams) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);

        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(moduleName);
        FacilioContext context = createRecordChain.getContext();

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

        createRecordChain.execute();

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
        long id = recordMap.get(moduleName).get(0).getId();
        this.setId(id);
        summary();
    }

    private void patchHandler(String moduleName, long id, Map<String, Object> patchObj, Map<String, Object> bodyParams) throws Exception {
        Object record = getRecord(moduleName, id);
        FacilioModule module = ChainUtil.getModule(moduleName);
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

        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        Class beanClass = getBeanClass(v3Config, module);
        List<Map<String, Object>> converted = FieldUtil.getAsMapList(Collections.singletonList(record), beanClass);

        JSONObject summaryRecord = FieldUtil.getAsJSON(converted.get(0));

        Set<String> keys = patchObj.keySet();
        for (String key: keys) {
            summaryRecord.put(key, patchObj.get(key));
        }

        FacilioChain patchChain = ChainUtil.getPatchChain(moduleName);

        FacilioContext context = patchChain.getContext();

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

        patchChain.execute();

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }

        summary();
    }



    private void updateHandler(String moduleName, long id, Map<String, Object> updateObj, Map<String, Object> bodyParams) throws Exception {
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        FacilioChain updateChain = ChainUtil.getUpdateChain(moduleName);

        FacilioContext context = updateChain.getContext();

        context.put(Constants.RECORD_ID, id);
        Constants.setModuleName(context, moduleName);
        Constants.setRawInput(context, updateObj);
        Constants.setBodyParams(context, bodyParams);
        context.put(Constants.QUERY_PARAMS, getQueryParameters());
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        Class beanClass = getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        updateChain.execute();

        Integer count = Constants.getRowsUpdated(context);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }

        summary();
    }

    private void addFiles() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new AttachmentCommand());
        FacilioContext context = transactionChain.getContext();

        Constants.setAttachmentFileNames(context, this.getFileNames());
        Constants.setAttachmentContentTypes(context, this.getContentTypes());
        Constants.setAttachmentFileList(context, this.getFiles());

        transactionChain.execute();

        Map<String, Long> attachmentNameVsId = Constants.getAttachmentNameVsId(context);
        this.setData("attachments", FieldUtil.getAsJSON(attachmentNameVsId));
    }



    private void deleteHandler(String moduleName, Map<String, Object> deleteObj, Map<String, Object> bodyParams) throws Exception {
        FacilioChain deleteChain = ChainUtil.getDeleteChain(moduleName);

        FacilioContext context = deleteChain.getContext();

        Constants.setModuleName(context, moduleName);
        Constants.setRawInput(context, deleteObj);
        Constants.setBodyParams(context, bodyParams);
        deleteChain.execute();

        Map<String, Integer> countMap = Constants.getCountMap(context);

        if (MapUtils.isEmpty(countMap)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        this.setData(FieldUtil.getAsJSON(countMap));
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

    public String delete() {
        try {
            deleteHandler(this.getModuleName(), this.getData(), this.getParams());
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

    public String files() throws Exception {
        try {
            addFiles();
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
        FacilioModule module = ChainUtil.getModule(moduleName);
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
