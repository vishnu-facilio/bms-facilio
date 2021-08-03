package com.facilio.v3.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.json.simple.JSONObject;

public class V3Util {
    public static void throwRestException (boolean condition, ErrorCode errorCode, String msg) throws RESTException {
        if (condition) {
            throw new RESTException(errorCode, msg);
        }
    }

    public static FacilioContext createRecord(FacilioModule module, Map<String, Object> data) throws Exception {
        return createRecord(module, data, null, null);
    }

    public static FacilioContext createRecord(FacilioModule module, Map<String, Object> data, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams) throws Exception {
        return createRecord(module, data, false, bodyParams, queryParams);
    }

    private static FacilioContext createRecord(FacilioModule module, Object data, boolean bulkOp, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams) throws Exception {
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
            Constants.setBulkRawInput(contextNew, (Collection<JSONObject>) data);
        } else {
            Constants.setRawInput(contextNew, (Map<String, Object>) data);
        }
        Constants.setBodyParams(contextNew, bodyParams);
        contextNew.put(Constants.QUERY_PARAMS, queryParams);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        contextNew.put(Constants.BEAN_CLASS, beanClass);

        createRecordChain.execute();
        return contextNew;
    }

    public static FacilioContext createRecordList(FacilioModule module, List<Map<String, Object>> recordList, Map<String, Object> bodyParams, Map<String, List<Object>> queryParams) throws Exception {
        return createRecord(module, recordList, true, bodyParams, queryParams);
    }
    
    
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
