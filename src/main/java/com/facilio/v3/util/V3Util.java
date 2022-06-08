package com.facilio.v3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class V3Util {
    public static void throwRestException (boolean condition, ErrorCode errorCode, String msg) throws RESTException {
        if (condition) {
            throw new RESTException(errorCode, msg);
        }
    }

    public static FacilioContext createRecord(FacilioModule module, Map<String, Object> data) throws Exception {
        return createRecord(module, data, null, null);
    }

    /***
     * Create record with V3 configuration. This executes all the workflows.
     *
     * @param module
     * @param data, can be empty map, but should not be null
     * @param bodyParams
     * @param queryParams
     * @return FacilioContext, with all parameter, or returns NULL if the data is null.
     * @throws Exception
     */
    public static FacilioContext createRecord(FacilioModule module, Map<String, Object> data, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams) throws Exception {
        if (data == null) {
            return null;
        }
        return createRecord(module, data, false, bodyParams, queryParams,false);
    }

    public static FacilioContext createRecord(FacilioModule module, Object data, boolean bulkOp,
                                               Map<String, Object> bodyParams,
                                               Map<String, List<Object>> queryParams,boolean restrictredAction) throws Exception {
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        FacilioChain createRecordChain = ChainUtil.getCreateChain(module.getName(), bulkOp);
        FacilioContext contextNew = createRecordChain.getContext();

        if (module.isCustom()) { // TODO move this check inside command
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField localIdField = modBean.getField("localId", module.getName());
            if (localIdField != null) {
                contextNew.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            }
        }

        Constants.setV3config(contextNew, v3Config);
        contextNew.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        Constants.setModuleName(contextNew, module.getName());
        contextNew.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        if (bulkOp) {
            Constants.setBulkRawInput(contextNew, (Collection<Map<String, Object>>) data);
        } else {
            Constants.setRawInput(contextNew, (Map<String, Object>) data);
        }
        Constants.setBodyParams(contextNew, bodyParams);
        contextNew.put(Constants.QUERY_PARAMS, queryParams);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        contextNew.put(Constants.BEAN_CLASS, beanClass);

        contextNew.put(FacilioConstants.ContextNames.ONLY_PERMITTED_ACTIONS, restrictredAction);

        createRecordChain.execute();
        return contextNew;
    }

    /**
     * Bulk create records with V3 configuration. This executes all the workflows for all the records.
     *
     * @param module
     * @param recordList
     * @param bodyParams
     * @param queryParams
     * @return FacilioContext, that contains all the values, or returns NULL if the recordList is null or empty
     * @throws Exception
     */
    public static FacilioContext createRecordList(FacilioModule module, List<Map<String, Object>> recordList, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams) throws Exception {
        if (CollectionUtils.isEmpty(recordList)) {
            return null;
        }
        return createRecord(module, recordList, true, bodyParams, queryParams,false);
    }

    public static FacilioContext updateBulkRecords(String moduleName,Map<String, Object> rawRecord,List<Long> ids,boolean restrictredAction) throws Exception {

    	FacilioContext summaryContext = V3Util.getSummary(moduleName, ids);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summaryContext, moduleName);

        List<Map<String, Object>> values = new ArrayList<>();
        JSONObject jsonObject;
		for (ModuleBaseWithCustomFields record: moduleBaseWithCustomFields) {

			jsonObject =  FieldUtil.getAsJSON(record);

			Set<String> keys = rawRecord.keySet();
            for (String key : keys) {
                jsonObject.put(key, rawRecord.get(key));
            }

            values.add(jsonObject);
        }

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        FacilioContext context = V3Util.updateBulkRecords(module, v3Config, moduleBaseWithCustomFields, values,
                ids, null, null, null,
                null, null,null,restrictredAction);

        return context;
    }


    public static FacilioContext updateBulkRecords(FacilioModule module, V3Config v3Config,
                                                   List<ModuleBaseWithCustomFields> oldRecords,
                                                   List<Map<String, Object>> recordList, List<Long> ids,
                                                   Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                                   Long stateTransitionId, Long customButtonId, Long approvalTransitionId,
                                                   String qrValue,boolean onlyPermittedActions
    ) throws Exception {
        return updateRecords(module, v3Config, true, oldRecords, recordList, ids,
                bodyParams, queryParams, stateTransitionId, customButtonId, approvalTransitionId,qrValue,onlyPermittedActions);
    }

    private static FacilioContext updateRecords(FacilioModule module, V3Config v3Config, boolean bulkOp,
                                                List<ModuleBaseWithCustomFields> oldRecords,
                                                List<Map<String, Object>> recordList, List<Long> ids,
                                                Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                                Long stateTransitionId, Long customButtonId, Long approvalTransitionId,
                                                String qrValue,boolean onlyPermittedActions
    ) throws Exception {
        FacilioChain patchChain = bulkOp ? ChainUtil.getBulkPatchChain(module.getName()) : ChainUtil.getPatchChain(module.getName());

        FacilioContext context = patchChain.getContext();
        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);

        if (!bulkOp) {
            if (ids.size() > 0) {   // assuming we will have at least one id
                context.put(Constants.RECORD_ID, ids.get(0));
            }
        }
        Constants.setRecordIds(context, ids);

        Constants.setModuleName(context, module.getName());

        if (!bulkOp) {
            if (recordList.size() > 0) {    // assuming we will have at least one record
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
        context.put(FacilioConstants.ContextNames.ONLY_PERMITTED_ACTIONS, onlyPermittedActions);

        if (customButtonId != null && customButtonId > 0) {
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST, Collections.singletonList(customButtonId));
            CommonCommandUtil.addEventType(EventType.CUSTOM_BUTTON, context);
        }

        context.put(Constants.QUERY_PARAMS, queryParams);

        patchChain.execute();
        return context;
    }

    public static FacilioContext processAndUpdateBulkRecords(FacilioModule module, List<ModuleBaseWithCustomFields> oldRecords, List<Map<String, Object>> rawRecords,
                                                             Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                                             Long stateTransitionId, Long customButtonId, Long approvalTransitionId, String qrValue,
                                                             boolean restrictedActions) throws Exception {
        Map<Long, Map<String, Object>> idVsRecordMap = new HashMap<>();
        for (ModuleBaseWithCustomFields record: oldRecords) {
            idVsRecordMap.put(record.getId(), FieldUtil.getAsJSON(record));
        }

        for (Map<String, Object> rec: rawRecords) {
            Map<String, Object> jsonObject = idVsRecordMap.get((long) rec.get("id"));
            Set<String> keys = rec.keySet();
            for (String key : keys) {
                jsonObject.put(key, rec.get(key));
            }
        }
        List<Map<String, Object>> values = new ArrayList<>(idVsRecordMap.values());
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        List<Long> ids = oldRecords.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

        FacilioContext context = V3Util.updateBulkRecords(module, v3Config, oldRecords, values, ids, bodyParams,
                queryParams, stateTransitionId, customButtonId, approvalTransitionId, qrValue, restrictedActions);
        return context;
    }

    public static JSONObject processAndUpdateSingleRecord(String moduleName, long id, Map<String, Object> patchObj, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                        Long stateTransitionId, Long customButtonId, Long approvalTransitionId,String qrValue) throws Exception {
        Object record = getRecord(moduleName, id, queryParams);
        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        List<Map<String, Object>> converted = FieldUtil.getAsMapList(Collections.singletonList(record), beanClass);

        JSONObject summaryRecord = FieldUtil.getAsJSON(converted.get(0));

        Map<String, FacilioField> fileFields = getFileFields(moduleName);

        Set<String> keys = patchObj.keySet();
        for (String key: keys) {
            FacilioField fileField = fileFields.get(key);
            if (fileField != null && !fileField.isDefault() && patchObj.get(key) == null) {
                summaryRecord.put(fileField.getName(), patchObj.get(key));
            }
            summaryRecord.put(key, patchObj.get(key));
        }

        if(record != null) {
            id = ((ModuleBaseWithCustomFields)record).getId();
        }

        FacilioContext context = V3Util.updateSingleRecord(module, v3Config, (ModuleBaseWithCustomFields) record, summaryRecord, id, bodyParams, queryParams, stateTransitionId, customButtonId, approvalTransitionId,qrValue);

        ModuleBaseWithCustomFields updatedRecord = Constants.getRecord(context, moduleName, id);
        JSONObject result = new JSONObject();
        JSONObject prop = FieldUtil.getAsJSON(updatedRecord);
        result.put(moduleName, prop);

        return result;
    }

    public static Map<String, FacilioField> getFileFields(String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Map<String, FacilioField> fieldMap = new HashMap<>();
        List<FacilioField> fileFields = new ArrayList<>();
        for (FacilioField f : fields) {
            if (f instanceof FileField) {
                fieldMap.put(f.getName()+"Id", f);
            }
        }
        return fieldMap;
    }

    public static Map<String, List<Object>> getQueryParameters(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        Map<String, List<Object>> queryParameters = new HashMap<>();
        String queryString = httpServletRequest.getQueryString();

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

    public static Object getRecord(String moduleName, long id, Map<String, List<Object>> queryParams) throws Exception {
        FacilioContext context = V3Util.getSummary(moduleName, Collections.singletonList(id), queryParams);

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List list = recordMap.get(moduleName);

        if (CollectionUtils.isEmpty(list)) {
            FacilioModule module = ChainUtil.getModule(moduleName);
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, module.getDisplayName() + " with id: " + id + " does not exist.");
        }
        return list.get(0);
    }

    public static FacilioContext updateSingleRecord(FacilioModule module, V3Config v3Config,
                                           ModuleBaseWithCustomFields oldRecord,
                                           JSONObject record, long id,
                                           Map<String, Object> bodyParams, Map<String, List<Object>> queryParams,
                                           Long stateTransitionId, Long customButtonId, Long approvalTransitionId,String qrValue
    ) throws Exception {
        return updateRecords(module, v3Config, false, Collections.singletonList(oldRecord),
                Collections.singletonList(record), Collections.singletonList(id),
                bodyParams, queryParams, stateTransitionId, customButtonId, approvalTransitionId,qrValue,false);
    }

    public static FacilioContext getSummary(String moduleName, List<Long> ids) throws Exception {
        return getSummary(moduleName, ids, null);
    }

    public static FacilioContext getSummary(String moduleName, List<Long> ids, Map<String, List<Object>> queryParams) throws Exception {
        FacilioChain fetchRecordChain = ChainUtil.getFetchRecordChain(moduleName);
        FacilioContext context = fetchRecordChain.getContext();

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config config = ChainUtil.getV3Config(moduleName);

        Constants.setRecordIds(context, ids);
        context.put(Constants.QUERY_PARAMS, queryParams);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);
        Class beanClass = ChainUtil.getBeanClass(config, module);
        context.put(Constants.BEAN_CLASS, beanClass);
        Constants.setModuleName(context, moduleName);
        Constants.setV3config(context, config);

        fetchRecordChain.execute();

        return context;
    }

    /**
     * Deletes module record of a particular module.
     * @param moduleName, whose data to be deleted.
     * @param deleteObj, moduleName as a key, with List of recordIds as value
     * @param bodyParams, additional parameters to be sent from client
     * @return
     * @throws Exception
     */
    public static FacilioContext deleteRecords(String moduleName, Map<String, Object> deleteObj,
                                               Map<String, Object> bodyParams, Map<String, List<Object>> queryParams, boolean restrictredAction) throws Exception {
        FacilioChain deleteChain = ChainUtil.getDeleteChain(moduleName);

        FacilioContext context = deleteChain.getContext();
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        Constants.setV3config(context, v3Config);

        Constants.setModuleName(context, moduleName);
        Constants.setRawInput(context, deleteObj);
        Constants.setBodyParams(context, bodyParams);
        context.put(Constants.QUERY_PARAMS, queryParams);

        context.put(FacilioConstants.ContextNames.ONLY_PERMITTED_ACTIONS, restrictredAction);

        deleteChain.execute();

        return context;
    }

    // Need to be changed
    public static FacilioContext createRecord(FacilioModule module,List<ModuleBaseWithCustomFields> records) throws Exception {
    	
        if(CollectionUtils.isNotEmpty(records)) {
            Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
            recordMap.put(module.getName(), records);
            
            V3Config v3Config = ChainUtil.getV3Config(module.getName());
            FacilioChain createRecordChain = ChainUtil.getCreateChain(module.getName());
            FacilioContext createContext = createRecordChain.getContext();
            
            if (module.isCustom()) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField localIdField = modBean.getField("localId", module.getName());
                if (localIdField != null) {
                	createContext.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
                }
            }

            
            Constants.setV3config(createContext, v3Config);
            Constants.setRecordMap(createContext, recordMap);
            
            createContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            Constants.setModuleName(createContext, module.getName());
            createContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            
            createContext.put(Constants.BEAN_CLASS, v3Config.getBeanClass());
            createRecordChain.execute();
            
            Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(createContext);
            
            return createContext;
        }
        
        return null;
    }

}
