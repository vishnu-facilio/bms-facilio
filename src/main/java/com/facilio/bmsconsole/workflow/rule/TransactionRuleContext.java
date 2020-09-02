package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.V3CustomModuleData;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TransactionRuleContext extends WorkflowRuleContext{


    private JSONObject transactionConfigJson;

    public JSONObject getTransactionConfigJson() {
        return transactionConfigJson;
    }

    public void setTransactionConfigJson(JSONObject transactionConfigJson) {
        this.transactionConfigJson = transactionConfigJson;
    }

    public String getConfigJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if(transactionConfigJson != null) {
            return FieldUtil.getAsJSON(transactionConfigJson).toJSONString();
        }
        return null;
    }
    public void setConfigJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        this.transactionConfigJson = (JSONObject)parser.parse(jsonString);
    }

    @Override
    public void executeTrueActions(Object currentRecord, Context context, Map<String, Object> placeHolders) throws Exception {
        if (!(currentRecord instanceof ModuleBaseWithCustomFields)) {
            throw new Exception("Invalid record");
        }
        JSONObject obj = getTransactionConfigJson();
        if(obj == null){
            throw new Exception("Invalid config for transaction rule");
        }
        String moduleName = (String) obj.get("creationModuleName");
        String sourceModName = (String) obj.get("transactionSourceModuleName");
        obj.keySet().forEach(keyStr ->
        {
            String keyvalue = (String) obj.get(keyStr);
            try {
                obj.put(keyStr, BeanUtils.getNestedProperty(currentRecord, keyvalue));
            }
            catch(Exception e){
                obj.put(keyStr, keyvalue);
            }

        });
        obj.put("transactionSourceRecordId", ((ModuleBaseWithCustomFields) currentRecord).getId());

        Class beanClassName = V3CustomModuleData.class;
        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromJson(obj, beanClassName);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();

        FacilioChain chain = null;
        FacilioContext recordContext = null;
        FacilioModule module = ChainUtil.getModule(moduleName);
        Class beanClass = FacilioConstants.ContextNames.getClassFromModule(module);
        recordMap.put(moduleName, Collections.singletonList(record));

        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        List<? extends V3Context> list = V3RecordAPI.getTransactionRecordsList(moduleName, sourceModName, ((ModuleBaseWithCustomFields) currentRecord).getId());

        if (CollectionUtils.isNotEmpty(eventTypes) && eventTypes.contains(EventType.DELETE)) {
            if(CollectionUtils.isNotEmpty(list)) {
                chain = ChainUtil.getDeleteChain(moduleName);
                recordContext = chain.getContext();
                Constants.setRecordIds(recordContext, Collections.singletonList(list.get(0).getId()));
                Constants.setRawInput(recordContext, FieldUtil.getAsProperties(list.get(0)));
            }
        }
        else {
            if (CollectionUtils.isEmpty(list)) {//creation
                chain = ChainUtil.getCreateRecordChain(moduleName);
                recordContext = chain.getContext();
                Constants.setRecordMap(recordContext, recordMap);
                recordContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            } else {//updation
                chain = ChainUtil.getUpdateChain(moduleName);
                recordContext = chain.getContext();
                recordContext.put(Constants.RECORD_ID, list.get(0).getId());
                Constants.setRawInput(recordContext, obj);
                recordContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
            }
            recordContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);

        }

        if(recordContext != null) {
            Constants.setModuleName(recordContext, moduleName);
            recordContext.put(Constants.BEAN_CLASS, beanClass);
        }
        if(chain != null){
            chain.execute();
        }

        super.executeTrueActions(record, context, placeHolders);
    }

    public Boolean isValidated(){
        if(transactionConfigJson == null || StringUtils.isEmpty((String)transactionConfigJson.get("transactionDate")) || StringUtils.isEmpty((String)transactionConfigJson.get("transactionName")) || StringUtils.isEmpty((String)transactionConfigJson.get("transactionSourceModuleName")) || StringUtils.isEmpty((String)transactionConfigJson.get("account"))
                || StringUtils.isEmpty((String)transactionConfigJson.get("creationModuleName")) || StringUtils.isEmpty((String)transactionConfigJson.get("transactionAmount")) || StringUtils.isEmpty((String)transactionConfigJson.get("transactionType"))) {
            return  false;
        }
        ModuleBean modBean = null;
        try {
            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule((String) transactionConfigJson.get("creationModuleName"));
            List<FacilioField> fields = modBean.getAllFields((String) transactionConfigJson.get("creationModuleName"));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            if (!fieldMap.containsKey("transactionSourceModuleName") || !fieldMap.containsKey("transactionSourceRecordId")) {
                return false;
            }
        }
        catch(Exception e){
            return  false;
        }
        return true;
    }
}
