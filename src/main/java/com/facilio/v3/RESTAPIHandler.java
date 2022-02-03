package com.facilio.v3;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.timeline.context.TimelineRequest;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.commands.AttachmentCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.TimelineViewUtil;
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
            record = V3Util.getRecord(moduleName, id, this.httpServletRequest);
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
        FacilioChain listChain = ChainUtil.getListChain(moduleName);
        FacilioContext context = listChain.getContext();

        api currentApi = currentApi();
        if (currentApi == api.v4) {
            Constants.setIsV4(context, true);
        }

        context.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);

        if (currentApi == api.v3) {
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

            if (getSearch() != null) {
                context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
            }

            JSONObject pagination = new JSONObject();
            pagination.put("page", this.getPage());
            pagination.put("perPage", this.getPerPage());

            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
            context.put(Constants.WITH_COUNT, getWithCount());
            context.put(Constants.QUERY_PARAMS, getQueryParameters());
        } else if (currentApi == api.v4) {
            Map<String, List<Object>> queryParameters = getQueryParameters();
            context.put(Constants.QUERY_PARAMS, getQueryParameters());

            List<Object> sort_by = queryParameters.get("sort_by");
            JSONObject sorting = new JSONObject();
            if (CollectionUtils.isNotEmpty(sort_by)) {
                this.setOrderBy((String) sort_by.get(0));
                sorting.put("orderBy", sort_by.get(0));
            }

            List<Object> sort_type = queryParameters.get("sort_type");
            if (CollectionUtils.isNotEmpty(sort_type)) {
                this.setOrderType((String) sort_type.get(0));
                sorting.put("orderType", sort_type.get(0));
            }

            List<Object> page = queryParameters.get("page");
            JSONObject pagination = new JSONObject();
            if (CollectionUtils.isNotEmpty(page)) {
                try {
                    this.setPage(Integer.valueOf((String) page.get(0)));
                    pagination.put("page", this.getPage());
                } catch (Exception ex) {
                    this.setPage(1);
                    pagination.put("page", 1);
                }
            }

            List<Object> limit = queryParameters.get("limit");
            if (CollectionUtils.isNotEmpty(limit)) {
                try {
                    this.setPerPage(Integer.valueOf((String) limit.get(0)));
                    pagination.put("perPage", this.getPerPage());
                } catch (Exception ex) {
                    this.setPerPage(50);
                    pagination.put("perPage", 50);
                }
            }

            List<Object> filter_by = queryParameters.get("filter_by");
            Constants.setFilterBy(context, CollectionUtils.isEmpty(filter_by) ? null : (String) filter_by.get(0));

            context.put(FacilioConstants.ContextNames.PAGINATION, pagination.isEmpty() ? null : pagination);

            if (MapUtils.isNotEmpty(sorting)) {
                context.put(FacilioConstants.ContextNames.SORTING, sorting);
            }

            context.put(FacilioConstants.ContextNames.OVERRIDE_SORTING, true);
        }


        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        if (v3Config != null) {
            V3Config.ListHandler listHandler = v3Config.getListHandler();
            if (listHandler != null) {
                Map<String, List<String>> lookupFieldCriteriaMap = listHandler.getLookupFieldCriteriaMap();
                if (!MapUtils.isEmpty(lookupFieldCriteriaMap)) {
                    Constants.setListRelationCriteria(context, lookupFieldCriteriaMap);
                }
            }
        }

        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        listChain.execute();
        Map<String, Object> meta = new HashMap<>();

        if (currentApi == api.v3) {
            if (getWithCount()) {
                Map<String, Object> paging = new HashMap<>();
                paging.put("totalCount", context.get(Constants.COUNT));
                meta.put("pagination", paging);
            }

            Map<String, List<WorkflowRuleContext>> stateFlows = (Map<String, List<WorkflowRuleContext>>) context.get(Constants.STATE_FLOWS);
            if (MapUtils.isNotEmpty(stateFlows)) {
                meta.put("stateflows", stateFlows);
            }

            Collection<WorkflowRuleContext> customButtons = (Collection<WorkflowRuleContext>) context.get(Constants.CUSTOM_BUTTONS);
            if (CollectionUtils.isNotEmpty(customButtons)) {
                meta.put(Constants.CUSTOM_BUTTONS, customButtons);
            }

            Object supplMap = Constants.getSupplementMap(context);
            if (supplMap != null) {
                meta.put("supplements", supplMap);
            }

            Map<String, Map<String, Object>> listRelationRecords = Constants.getListRelationRecords(context);
            if (listRelationRecords != null) {
                meta.put("submoduleRelations", listRelationRecords);
            }
        } else if (currentApi == api.v4) {
            Map<String, Object> paging = new HashMap<>();
            meta.put("pagination", paging);
            paging.put("page", this.getPage());
            paging.put("per_page", this.getPerPage());
            paging.put("sort_type", this.getOrderType());
            paging.put("sort_by", this.getOrderBy());
            paging.put("has_more_page", Constants.hasMoreRecords(context));
        }

        JSONObject recordJSON = Constants.getJsonRecordMap(context);
        this.setData(recordJSON);

        if (MapUtils.isNotEmpty(meta)) {
            this.setMeta(FieldUtil.getAsJSON(meta));
        }
    }

    private void bulkPatchHandler(Map<String, Object> dataMap, String moduleName, Map<String, Object> bodyParams) throws Exception{
        List<Map<String, Object>> rawRecords = (List<Map<String, Object>>) dataMap.get(moduleName);
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> record: rawRecords) {
            ids.add((long) record.get("id"));
        }
        FacilioContext summaryContext = V3Util.getSummary(moduleName, ids);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summaryContext, moduleName);

        Map<Long, JSONObject> idVsRecordMap = new HashMap<>();
        for (ModuleBaseWithCustomFields record: moduleBaseWithCustomFields) {
            idVsRecordMap.put(record.getId(), FieldUtil.getAsJSON(record));
        }

        for (Map<String, Object> rec: rawRecords) {
            JSONObject jsonObject = idVsRecordMap.get((long) rec.get("id"));
            Set<String> keys = rec.keySet();
            for (String key : keys) {
                jsonObject.put(key, rec.get(key));
            }
        }
        List<JSONObject> values = new ArrayList<>(idVsRecordMap.values());

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        FacilioContext context = V3Util.updateBulkRecords(module, v3Config, moduleBaseWithCustomFields, values,
                ids, bodyParams, getQueryParameters(), getStateTransitionId(),
                getCustomButtonId(), getApprovalTransitionId(),getQrValue());

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

    public String list() throws Exception {
    	 	handleListRequest(this.getModuleName());
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
                AuditLogHandler.ActionType.ADD,
                this.getData(), true);

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

    private void addAuditLog(List<JSONObject> props, String moduleName, String message,
                             AuditLogHandler.ActionType actionType, JSONObject inputData, boolean addLinkConfig) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        FacilioField primaryField = modBean.getPrimaryField(moduleName);
        if (primaryField == null || module == null) {
            return;
        }
        for (Map<String, Object> prop : props) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(prop);
            Object primaryValue = prop.get(primaryField.getName());
            if (primaryValue instanceof Map) {
                primaryValue = ((Map<?, ?>) primaryValue).get("primaryValue");
            }
            if (primaryValue == null) {
                continue;
            }
            Long recordId = (Long) prop.get("id");
            AuditLogHandler.AuditLogContext auditLogContext = new AuditLogHandler.AuditLogContext(String.format(message, primaryValue, module.getDisplayName()),
                    null, inputData.toJSONString(), AuditLogHandler.RecordType.MODULE, moduleName,
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

    public String patch() throws Exception {
    		//removing permission restricted fields
        Map<String, Object> data = this.getData();
        if (data == null) {
            data = new HashMap<>();
        }
        removeRestrictedFields(data, this.getModuleName(), true);

        JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(), this.httpServletRequest, this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(),this.getQrValue());

        addAuditLog(Collections.singletonList((JSONObject)result.get(getModuleName())), getModuleName(), "Record {%s} of module %s has been updated",
                    AuditLogHandler.ActionType.UPDATE,
                    this.getData(), true);
        this.setData(result);

        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioContext context = V3Util.deleteRecords(getModuleName(), getData(), getParams());
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
                JSONObject json = new JSONObject();
                json.put("count", countMap.get(getModuleName()));
                if (deletedRecords.size() > 1) {
                    sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Deleted %s records of module %s", deletedRecords.size(), module.getDisplayName()),
                            null, json.toJSONString(), AuditLogHandler.RecordType.MODULE, module.getDisplayName(), 0)
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
                            null, json.toJSONString(), AuditLogHandler.RecordType.MODULE, module.getDisplayName(), 0)
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

    private void removeRestrictedFields(List<Map<String, Object>> dataList, String moduleName, Boolean validatePermissions) throws Exception{
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        for (Map<String, Object> dataMap : dataList) {
            removeRestrictedFields(dataMap, moduleName, validatePermissions);
        }
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

    private TimelineRequest timelineRequest;
    public TimelineRequest getTimelineRequest() {
        return timelineRequest;
    }
    public void setTimelineRequest(TimelineRequest timelineRequest) {
        this.timelineRequest = timelineRequest;
    }

    public String timelineData() throws Exception {
        FacilioChain chain = ChainUtil.getTimelineChain();
        FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
        chain.execute();
        setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
        return SUCCESS;
    }

    public String timelinePatch() throws Exception {
        /* Validation to be added
        timelineRequest = new TimelineRequest();
        timelineRequest.setViewName(this.getViewName());
        FacilioChain chain = ChainUtil.getTimelineValidationChain(timelineRequest);
        chain.execute();
        FacilioContext context = chain.getContext();
        */

        //removing permission restricted fields
        Map<String, Object> data = this.getData();
        if (data == null) {
            data = new HashMap<>();
        }
        removeRestrictedFields(data, this.getModuleName(), true);

        JSONObject result = V3Util.processAndUpdateSingleRecord(this.getModuleName(), this.getId(), data, this.getParams(), this.httpServletRequest, this.getStateTransitionId(), this.getCustomButtonId(), this.getApprovalTransitionId(),this.getQrValue());
        this.setData(result);

        addAuditLog(Collections.singletonList((JSONObject)this.getData().get(getModuleName())), getModuleName(), "Record {%s} of module %s has been updated through TimelineView",
                AuditLogHandler.ActionType.UPDATE,
                (JSONObject) data, true);

        if(this.getTimelineRequest() != null) {
            FacilioChain chain = ChainUtil.getTimelineChain();
            FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest());
            chain.execute();
            setData(FacilioConstants.ContextNames.TIMELINE_DATA, context.get(FacilioConstants.ContextNames.TIMELINE_DATA));
        }

        return SUCCESS;
    }

    public String timelineSelectiveList() throws Exception {
        FacilioChain chain = ChainUtil.getTimelineListChain();
        FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
                this.getPage(), this.getPerPage(), false);
        chain.execute();
        setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP));
        return SUCCESS;
    }

    public String timelineUnScheduledList() throws Exception {
        FacilioChain chain = ChainUtil.getTimelineListChain();
        FacilioContext context = TimelineViewUtil.getTimelineContext(chain, this.getTimelineRequest(), true,
                this.getPage(), this.getPerPage(), true);
        chain.execute();
        setData(timelineRequest.getModuleName(), context.get(FacilioConstants.ContextNames.TIMELINE_V3_DATAMAP));
        return SUCCESS;
    }
}
