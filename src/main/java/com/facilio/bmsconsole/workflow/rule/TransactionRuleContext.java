package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class TransactionRuleContext extends WorkflowRuleContext{

    private Long transactionDateFieldId;
    private Long accountId;
    private Long transactionType;
    private Long transactionAmountFieldId;

    private JSONObject transactionConfigJson;

    public JSONObject getTransactionConfigJson() throws Exception{
        if(transactionConfigJson==null){
            transactionConfigJson=new JSONObject();
            if(this.transactionDateFieldId !=null && this.transactionAmountFieldId !=null) {
                ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField transactionDateField = moduleBean.getField(this.transactionDateFieldId, getModuleName());
                FacilioField transactionAmountField = moduleBean.getField(this.transactionAmountFieldId, getModuleName());
                transactionConfigJson.put("transactionDate", transactionDateField.getName());
                transactionConfigJson.put("transactionAmount",transactionAmountField.getName());
            }
            transactionConfigJson.put("account",String.valueOf(this.accountId));
            transactionConfigJson.put("transactionType",String.valueOf(this.transactionType));
        }
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
        if(jsonString!=null) {
            this.transactionConfigJson = (JSONObject) parser.parse(jsonString);
        }
    }

    @Override
    public void executeTrueActions(Object currentRecord, Context context, Map<String, Object> placeHolders) throws Exception {
        if (!(currentRecord instanceof ModuleBaseWithCustomFields)) {
            throw new Exception("Invalid record");
        }

        String creationModuleName = FacilioConstants.TransactionRule.CreationModuleName;
        Long sourceModId =  getModuleId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        ModuleBaseWithCustomFields record= (ModuleBaseWithCustomFields) currentRecord;
        if(FieldUtil.getValue(record,modBean.getField(this.transactionAmountFieldId))==null){
            return;
        }
        Map<String,Object> data=new HashMap<>();
        data.put("transactionDate",FieldUtil.getValue(record,modBean.getField(this.transactionDateFieldId)));
        data.put("transactionAmount",FieldUtil.getValue(record,modBean.getField(this.transactionAmountFieldId)));
        data.put("transactionType",transactionType);

        Map<String, Long> accountMap = new HashMap<>();
        accountMap.put("id", accountId);
        data.put("account", accountMap);

        data.put("transactionSourceRecordId",record.getId());
        data.put("transactionSourceModuleId",getModuleId());
        data.put("ruleId",this.getId());

        FacilioModule module = ChainUtil.getModule(creationModuleName);
        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        V3TransactionContext transactionRecord = V3RecordAPI.getTransactionRecord(creationModuleName, sourceModId, record.getId(),this.getId());

        if (CollectionUtils.isNotEmpty(eventTypes) && eventTypes.contains(EventType.DELETE)) {
            if (transactionRecord!=null) {
                Map<String,Object>recordId=new HashMap<>();
                ArrayList id=new ArrayList<>();
                id.add(transactionRecord.getId());
                recordId.put(creationModuleName,id);
                V3Util.deleteRecords(creationModuleName,recordId,null,null,false);
            }
        } else {
            if (CollectionUtils.isNotEmpty(eventTypes) && eventTypes.contains(EventType.CREATE)) {//create;
                V3Util.createRecord(module, data, null, null);
            }
            else {//update
                if (transactionRecord!=null) {
                    V3Util.processAndUpdateSingleRecord(creationModuleName,transactionRecord.getId(), data,null,null,null,null,null,null);
                }
            }

        }

        super.executeTrueActions(currentRecord, context, placeHolders);
    }

    public Boolean isValidated(){
        if(this.transactionDateFieldId == null || this.transactionAmountFieldId == null  || this.transactionType==null|| this.accountId ==null) {
            return false;
        }
        ModuleBean modBean = null;
        try {
            modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.TransactionRule.CreationModuleName);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.TransactionRule.CreationModuleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            if (!fieldMap.containsKey("transactionSourceModuleId") || !fieldMap.containsKey("transactionSourceRecordId")) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public void configToProp()throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField transactionDateField= modBean.getField((String) transactionConfigJson.get("transactionDate"),getModuleName());
        FacilioField transactionAmountField=modBean.getField((String) transactionConfigJson.get("transactionAmount"),getModuleName());

        if(transactionConfigJson!=null) {
            if (transactionConfigJson.containsKey("transactionDate")){
                this.setTransactionDateFieldId(transactionDateField.getFieldId());
            }
            if(transactionConfigJson.containsKey("account")){
                this.setAccountId(Long.parseLong(String.valueOf(transactionConfigJson.get("account"))));
            }
            if(transactionConfigJson.containsKey("transactionType")){
                this.setTransactionType(Long.parseLong(String.valueOf(transactionConfigJson.get("transactionType"))));
            }
            if(transactionConfigJson.containsKey("transactionAmount")){
                this.setTransactionAmountFieldId(transactionAmountField.getFieldId());
            }
        }
    }
}
