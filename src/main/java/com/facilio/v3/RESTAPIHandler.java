package com.facilio.v3;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.actions.picklist.PickListUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SecurityUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.commands.AttachmentCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RESTAPIHandler extends V3Action implements ServletRequestAware {
    private static final Logger LOGGER = Logger.getLogger(RESTAPIHandler.class.getName());

    protected void handleSummaryRequest(String moduleName, long id) throws Exception {
        api currentApi = currentApi();
        Object record;
        if (currentApi == api.v3) {
            record = V3Util.getRecord(moduleName, id, getQueryParameters(), true);
        } else {
            record = getV4TransformedRecord(moduleName, id);
        }
        JSONObject result = new JSONObject();
        result.put(moduleName, FieldUtil.getAsJSON(record));
        this.setData(result);
    }

    private Object getV4TransformedRecord(String moduleName, long id) throws Exception {
        FacilioChain fetchRecordChain = ChainUtil.getFetchRecordChain(moduleName);
        FacilioContext context = fetchRecordChain.getContext();

        api currentApi = currentApi();
        Constants.setIsV4(context, true);

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config config = ChainUtil.getV3Config(moduleName);

        Constants.setRecordIds(context, Collections.singletonList(id));
        context.put(Constants.QUERY_PARAMS, getQueryParameters());
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);
        Class beanClass = ChainUtil.getBeanClass(config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        Constants.setModuleName(context, moduleName);
        Constants.setV3config(context, config);

        fetchRecordChain.execute();

        List list;
        JSONObject jsonRecordMap = Constants.getJsonRecordMap(context);
        list = (List) jsonRecordMap.get(moduleName);

        if (CollectionUtils.isEmpty(list)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }
        return list.get(0);
    }


    private void countHandler(String moduleName) throws Exception {
        FacilioChain countChain = ChainUtil.getCountChain(moduleName);
        FacilioContext context = countChain.getContext();

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

        String quickFilter =this.getQuickFilter();
        if(StringUtils.isNotEmpty(quickFilter)){
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(quickFilter);
            context.put(Constants.QUICK_FILTER, json);
        }

        String search=this.getSearch();
        if (search != null) {
            context.put(FacilioConstants.ContextNames.SEARCH, search);
        }

        context.put(Constants.QUERY_PARAMS, getQueryParameters());

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        countChain.execute();

        Long count = Constants.getCount(context);
        this.setData("count", count);
    }

    private void handleListRequest(String moduleName) throws Exception {

        api currentApi = currentApi();
        if(!SecurityUtil.isClean(this.getOrderBy(),this.getOrderType())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid order clause parameter passed");
        }
        FacilioContext listContext = V3Util.fetchList(moduleName, (currentApi == api.v3), this.getViewName(), this.getFilters(), this.getExcludeParentFilter(), this.getClientCriteria(),
                this.getOrderBy(), this.getOrderType(), this.getSearch(), this.getPage(), this.getPerPage(), this.getWithCount(), getQueryParameters(), null,this.getWithoutCustomButtons(),this.getFetchOnlyViewGroupColumn(),this.getQuickFilter());

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);
        this.setData(recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
    }


    private void handlePickListRequest(String moduleName) throws Exception {

        if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
            List<String> localSearchDisabled = Arrays.asList(FacilioConstants.ContextNames.USERS,FacilioConstants.ContextNames.READING_RULE_MODULE);

            this.setData(FacilioConstants.ContextNames.PICKLIST, PickListUtil.getSpecialModulesPickList(moduleName, this.getPage(), this.getPerPage(), this.getSearch(), this.getFilters(), this.getDefault()));
            this.setMeta(FacilioConstants.ContextNames.MODULE_TYPE, FacilioModule.ModuleType.PICK_LIST.name());
            this.setMeta(FacilioConstants.PickList.LOCAL_SEARCH, !localSearchDisabled.contains(moduleName));
        }

        else {

            FacilioContext pickListContext = V3Util.fetchPickList(this.getModuleName(), this.getFilters(), this.getExcludeParentFilter(), this.getClientCriteria(), this.getDefault(), this.getOrderBy(), this.getOrderType(), this.getSearch(), this.getPage(), this.getPerPage(), this.getWithCount(), this.getQueryParameters(), null);
            this.setData(FacilioConstants.ContextNames.PICKLIST, pickListContext.get(FacilioConstants.ContextNames.PICKLIST));
            if (pickListContext.containsKey(FacilioConstants.ContextNames.META)) {
                this.setMeta((JSONObject) pickListContext.get(FacilioConstants.ContextNames.META));
            }
        }
    }

    private void bulkPatchHandler(Map<String, Object> dataMap, String moduleName, Map<String, Object> bodyParams) throws Exception{
        List<Map<String, Object>> rawRecords = (List<Map<String, Object>>) dataMap.get(moduleName);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> record: rawRecords) {
            ids.add((long) record.get("id"));
        }
        FacilioContext summaryContext = V3Util.getSummary(moduleName, ids,null,true,null);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summaryContext, moduleName);

        FacilioModule module = ChainUtil.getModule(moduleName);
        FacilioContext context = V3Util.processAndUpdateBulkRecords(module, moduleBaseWithCustomFields, rawRecords, bodyParams, getQueryParameters(), getStateTransitionId(),
                getCustomButtonId(), getApprovalTransitionId(), getQrValue(), getLocationValue(),getCurrentLocation(), false,false,null);

        Integer count = (Integer) context.get(Constants.ROWS_UPDATED);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Map<String, List<ModuleBaseWithCustomFields>> fetchAfterSave = Constants.getRecordMap(context);
        this.setData(FieldUtil.getAsJSON(fetchAfterSave));
    }

    /**
     * The support to update the entire object is not supported.
     *
     * Refer to {@link RESTAPIHandler#patch()}
     *
     * @return
     * @throws Exception
     */
    @Deprecated
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

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        Constants.setV3config(context, v3Config);

        updateChain.execute();

        Integer count = Constants.getRowsUpdated(context);

        if (count == null || count <= 0) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }

        handleSummaryRequest(moduleName, id);
    }

    private void addFiles() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new AttachmentCommand());
        FacilioContext context = transactionChain.getContext();

        Constants.setAttachmentFileNames(context, this.getFilesFileName());
        Constants.setAttachmentContentTypes(context, this.getFilesContentType());
        Constants.setAttachmentFileList(context, this.getFiles());

        transactionChain.execute();

        Map<String, Long> attachmentNameVsId = Constants.getAttachmentNameVsId(context);
        this.setData("attachments", FieldUtil.getAsJSON(attachmentNameVsId));
    }

    public String summary() throws Exception {
    	 	handleSummaryRequest(this.getModuleName(), this.getId());
        return SUCCESS;
    }

    public String glimpse() throws Exception{

        handleSummaryRequest(this.getModuleName(), this.getId());

        if(isFetchGlimpseMetaFields()) {
            FacilioChain chain = TransactionChainFactory.getModuleSettingConfigurationChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
            context.put(FacilioConstants.ContextNames.MODULE_SETTING_NAME, FacilioConstants.ContextNames.GLIMPSE);
            chain.execute();

            this.setData(FacilioConstants.ContextNames.GLIMPSE_META_DATA, context.get(FacilioConstants.ContextNames.GLIMPSE));
        }
        return SUCCESS;
    }
    public String list() throws Exception {
    	 	handleListRequest(this.getModuleName());
        return SUCCESS;
    }

    public String pickList() throws Exception {
        handlePickListRequest(this.getModuleName());
        return SUCCESS;
    }

    public String create() throws Exception {
    		//removing permission restricted fields
        removeRestrictedFields(this.getData(), this.getModuleName(), true);

        String moduleName = getModuleName();
        FacilioModule module = ChainUtil.getModule(moduleName);
        FacilioContext context = V3Util.createRecord(module, getData(), getParams(), getQueryParameters());

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(Constants.RECORD_MAP);
        ModuleBaseWithCustomFields record = recordMap.get(moduleName).get(0);
        JSONObject result = new JSONObject();
        JSONObject recordJSON = FieldUtil.getAsJSON(record);
        result.put(moduleName, recordJSON);
        this.setData(result);

        addAuditLog(Collections.singletonList(recordJSON), getModuleName(), "Record {%s} of module %s has been created",
                AuditLogHandler.ActionType.ADD, true);

        this.httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        return SUCCESS;
    }

    public String bulkCreate() throws Exception {
    		String moduleName = getModuleName();
        List<Map<String, Object>> recordList = (List<Map<String, Object>>) getData().get(moduleName);

        // remove restricted fields
        removeRestrictedFields(recordList, moduleName, true);

        FacilioModule module = ChainUtil.getModule(moduleName);
        FacilioContext context = V3Util.createRecordList(module, recordList, getParams(), getQueryParameters());
        List<ModuleBaseWithCustomFields> addedRecords = Constants.getRecordList(context);
        JSONObject result = new JSONObject();

        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        result.put(moduleName, FieldUtil.getAsMapList(addedRecords, ChainUtil.getBeanClass(v3Config, module)));
        setData(result);

        return SUCCESS;
    }

    protected void addAuditLog(List<JSONObject> props, String moduleName, String message,
                             AuditLogHandler.ActionType actionType, boolean addLinkConfig) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioField primaryField = modBean.getPrimaryField(moduleName);
        if (primaryField == null || module == null) {
            return;
        }
        for (Map<String, Object> prop : props) {
            Object primaryValue = prop.get(primaryField.getName());
            if (primaryValue instanceof Map) {
                primaryValue = ((Map<?, ?>) primaryValue).get("primaryValue");
            }
            if (primaryValue == null) {
                continue;
            }
            Long recordId = (Long) prop.get("id");
            AuditLogHandler.AuditLogContext auditLogContext = new AuditLogHandler.AuditLogContext(String.format(message, primaryValue, module.getDisplayName()),
                    null, AuditLogHandler.RecordType.MODULE, moduleName,
                    recordId)
                    .setActionType(actionType);

            if (addLinkConfig) {
                auditLogContext.setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("moduleName", moduleName);
                    json.put("id", recordId);
                    array.add(json);
                    return array.toJSONString();
                }).apply(null));
            }

            sendAuditLogs(auditLogContext);
        }
    }

    /**
     * This API is deprecated. Refer to {@link RESTAPIHandler#patch()}
     *
     * @return
     * @throws Exception
     */
    @Deprecated
    public String update() throws Exception {
    		updateHandler(this.getModuleName(), this.getId(), this.getData(), this.getParams());
        return SUCCESS;
    }

    public String bulkPatch() throws Exception {
    		bulkPatchHandler(this.getData(), this.getModuleName(), this.getParams());

        return SUCCESS;
    }

    public String bulkGet() throws Exception {
        String moduleName = getModuleName();
        JSONObject data = getData();
        List<Long> ids = (List<Long>) data.get(moduleName);
        FacilioContext summaryContext = V3Util.getSummary(moduleName, ids, null, true,null);

        List<ModuleBaseWithCustomFields> records = Constants.getRecordListFromContext(summaryContext, moduleName);
        JSONObject result = new JSONObject();
        result.put(moduleName, FieldUtil.getAsJSONArray(records, V3Context.class));
        this.setData(result);
        return SUCCESS;
    }

    public String patch() throws Exception {
    		//removing permission restricted fields
        Map<String,Object> data = this.getData();
        if (data == null){
            data = new HashMap<>();
        }
        removeRestrictedFields(data, this.getModuleName(), true);


        JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(), getQueryParameters(), this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(),this.getQrValue(),this.getLocationValue(), this.getCurrentLocation(),null);

        addAuditLog(Collections.singletonList((JSONObject)result.get(getModuleName())), getModuleName(), "Record {%s} of module %s has been updated",
                    AuditLogHandler.ActionType.UPDATE, true);

        this.setData(result);

        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioContext context = V3Util.deleteRecords(getModuleName(), getData(), getParams(), getQueryParameters(), false);
        Map<String, Integer> countMap = Constants.getCountMap(context);
        if (MapUtils.isEmpty(countMap)) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        this.setData(FieldUtil.getAsJSON(countMap));

        addDeleteAuditLog(context, countMap);
        return SUCCESS;
    }

    private void addDeleteAuditLog(FacilioContext context, Map<String, Integer> countMap) {
        try {
            List<ModuleBaseWithCustomFields> deletedRecords = Constants.getDeletedRecords(context);
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = moduleBean.getModule(getModuleName());
            if (CollectionUtils.isNotEmpty(deletedRecords) && module != null) {
                if (deletedRecords.size() > 1) {
                    sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Deleted %s records of module %s", deletedRecords.size(), module.getDisplayName()),
                            null, AuditLogHandler.RecordType.MODULE, module.getDisplayName(), 0)
                            .setActionType(AuditLogHandler.ActionType.DELETE)
                    );
                } else {
                    ModuleBaseWithCustomFields deletedRecord = deletedRecords.get(0);
                    FacilioField primaryField = moduleBean.getPrimaryField(getModuleName());
                    Object value = FieldUtil.getValue(deletedRecord, primaryField);
                    String subject;
                    if (value != null) {
                        subject = String.format("Deleted <b>%s</b> of module %s", value, module.getDisplayName());
                    } else {
                        subject = String.format("Deleted 1 record of module %s", module.getDisplayName());
                    }
                    sendAuditLogs(new AuditLogHandler.AuditLogContext(subject,
                            null, AuditLogHandler.RecordType.MODULE, module.getDisplayName(), 0)
                            .setActionType(AuditLogHandler.ActionType.DELETE)
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String files() throws Exception {
    	 	addFiles();
        return SUCCESS;
    }

    public String count() throws Exception {
    		countHandler(this.getModuleName());
        return SUCCESS;
    }

    public Map<String, List<Object>> getQueryParameters() throws UnsupportedEncodingException {
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
            values.add(keyValuePair.length == 1 ? "" : URLDecoder.decode(keyValuePair[1], StandardCharsets.UTF_8.toString()));
            queryParameters.put(keyValuePair[0], values);
        }
        return queryParameters;
    }

    private HttpServletRequest httpServletRequest;
    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    protected void removeRestrictedFields(List<Map<String, Object>> dataList, String moduleName, Boolean validatePermissions) throws Exception{
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        for (Map<String, Object> dataMap : dataList) {
            removeRestrictedFields(dataMap, moduleName, validatePermissions);
        }
    }

    protected void removeRestrictedFields(Map<String, Object> dataMap, String moduleName, Boolean validatePermissions) throws Exception{
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
    private String _default;
    public String getDefault() {
        return _default;
    }
    public void setDefault(String _default) {
        this._default = _default;
    }


}
