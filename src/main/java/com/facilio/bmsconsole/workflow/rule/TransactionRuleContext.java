package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
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
import java.util.*;

@Getter @Setter
public class TransactionRuleContext extends WorkflowRuleContext{

    private Long transactionDateFieldId;
    private Long accountId;
    private Long transactionType;
    private Long transactionAmountFieldId;

    private Long resourceFieldId;

    private JSONObject transactionConfigJson;

    public JSONObject getTransactionConfigJson() throws Exception{
        if(transactionConfigJson==null){
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            transactionConfigJson=new JSONObject();
            if(this.transactionDateFieldId !=null && this.transactionAmountFieldId !=null) {
                FacilioField transactionDateField = moduleBean.getField(this.transactionDateFieldId, getModuleName());
                FacilioField transactionAmountField = moduleBean.getField(this.transactionAmountFieldId, getModuleName());
                transactionConfigJson.put("transactionDate", transactionDateField.getName());
                transactionConfigJson.put("transactionAmount",transactionAmountField.getName());
            }
            transactionConfigJson.put("account",String.valueOf(this.accountId));
            transactionConfigJson.put("transactionType",String.valueOf(this.transactionType));

            if (this.resourceFieldId != null){
                transactionConfigJson.put("transactionResource",this.resourceFieldId);
            }
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
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Object transactionAmountFieldValue = FieldUtil.getValue((ModuleBaseWithCustomFields) record, modBean.getField(this.transactionAmountFieldId));
        Object transactionDateFieldValue = FieldUtil.getValue((ModuleBaseWithCustomFields) record, modBean.getField(this.transactionDateFieldId));
        if(transactionDateFieldValue == null || transactionAmountFieldValue == null ){
            return false;
        }

        return super.evaluateMisc(moduleName, record, placeHolders, context);
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
        Map<String,Object> data=new HashMap<>();
        data.put("transactionDate",FieldUtil.getValue(record,modBean.getField(this.transactionDateFieldId)));
        data.put("transactionAmount",FieldUtil.getValue(record,modBean.getField(this.transactionAmountFieldId)));
        data.put("transactionType",transactionType);

        Map<String, Long> accountMap = new HashMap<>();
        accountMap.put("id", accountId);
        data.put("account", accountMap);

        Set<Long> resourceIds = new HashSet<>();

        if (this.resourceFieldId != null) {
          Object resourceValue =  FieldUtil.getValue(record, modBean.getField(this.resourceFieldId));
          resourceValue = FacilioUtil.isEmptyOrNull(resourceValue) ? null : resourceValue;
          data.put("transactionResource", resourceValue);
          if (resourceValue == null) {
              resourceIds.add(-1l);
          } else {
              if (resourceValue instanceof Map) {
                  Map<String, Object> resourceValueMap = (Map<String, Object>) resourceValue;
                  resourceIds.add((Long) resourceValueMap.get("id"));
              }
          }
        } else {
            resourceIds.add(-1l);
        }

        JSONObject bodyParam = new JSONObject();
        bodyParam.put("Resource_ids", resourceIds);

        data.put("transactionSourceRecordId",record.getId());
        data.put("transactionSourceModuleId",getModuleId());
        data.put("ruleId",this.getId());


        FacilioModule module = ChainUtil.getModule(creationModuleName);
        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        V3TransactionContext transactionRecord = V3RecordAPI.getTransactionRecord(creationModuleName, sourceModId, record.getId(),this.getId());
        if (transactionRecord != null) {
            ResourceContext oldResource = transactionRecord.getTransactionResource();
            if (oldResource == null) {
                resourceIds.add(-1l);
            } else {
                resourceIds.add(oldResource.getId());
            }
        }

        boolean shouldContinue = true;

        if (this.resourceFieldId != null ){
            Object resourceValue =  FieldUtil.getValue(record, modBean.getField(this.resourceFieldId));
            resourceValue = FacilioUtil.isEmptyOrNull(resourceValue) ? null : resourceValue;
            if (resourceValue == null) {
                shouldContinue = false;
                if (transactionRecord != null) {
                    Map<String, Object> recordId = new HashMap<>();
                    ArrayList id = new ArrayList<>();
                    id.add(transactionRecord.getId());
                    recordId.put(creationModuleName, id);
                    V3Util.deleteRecords(creationModuleName, recordId, bodyParam, null, false);
                }
            }
        }

        if (shouldContinue) {
            if (CollectionUtils.isNotEmpty(eventTypes) && eventTypes.contains(EventType.DELETE)) {
                if (transactionRecord != null) {
                    Map<String, Object> recordId = new HashMap<>();
                    ArrayList id = new ArrayList<>();
                    id.add(transactionRecord.getId());
                    recordId.put(creationModuleName, id);
                    V3Util.deleteRecords(creationModuleName, recordId, bodyParam, null, false);
                }
            } else {
                if (CollectionUtils.isNotEmpty(eventTypes) && eventTypes.contains(EventType.CREATE)) {//create;
                    V3Util.createRecord(module, data, bodyParam, null);
                } else {//update
                    if (transactionRecord != null) {
                        V3Util.processAndUpdateSingleRecord(creationModuleName, transactionRecord.getId(), data, bodyParam, null, null, null, null, null, null, null,null);
                    }else{
                        V3Util.createRecord(module, data, bodyParam, null);
                    }
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
            if ((transactionConfigJson.containsKey("transactionResource")) && (transactionConfigJson.get("transactionResource") != null)){
                Long resourceFieldId = Long.parseLong(String.valueOf(transactionConfigJson.get("transactionResource")));
                FacilioField resourceField = modBean.getField(resourceFieldId);
                Long resourceModuleId = modBean.getModule(FacilioConstants.ContextNames.RESOURCE).getModuleId();
                if ((!(resourceField.getName().equals("siteId")) && !(resourceField.getName().equals("site")) &&
                   !((resourceField instanceof LookupField) && ((LookupField) resourceField).getLookupModule().getExtendedModuleIds().contains(resourceModuleId)))){
                    throw new IllegalArgumentException("This resource field doesn't belong to the current module");
                }
                this.setResourceFieldId(resourceFieldId);
            }
        }
    }
}
