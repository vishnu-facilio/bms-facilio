package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenerateCriteriaFromFilterForNonModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
        if(filters != null && !filters.isEmpty()) {
            Iterator<String> filterIterator = filters.keySet().iterator();
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            if (StringUtils.isEmpty(moduleName)) {
                return false;
            }

            List<FacilioField> allFields = getAllFields(moduleName);
            if (CollectionUtils.isEmpty(allFields)) {
                return false;
            }
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

            Criteria criteria = new Criteria();
            while(filterIterator.hasNext())
            {
                String fieldName = filterIterator.next();
                Object fieldJson = filters.get(fieldName);
                List<Condition> conditionList = new ArrayList<>();
                if(fieldJson!=null && fieldJson instanceof JSONArray) {
                    JSONArray fieldJsonArr = (JSONArray) fieldJson;
                    for(int i=0;i<fieldJsonArr.size();i++) {
                        JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
                        setConditions(moduleName, fieldName, fieldMap, fieldJsonObj, conditionList);
                    }
                }
                else if(fieldJson!=null && fieldJson instanceof JSONObject) {
                    JSONObject fieldJsonObj = (JSONObject) fieldJson;
                    setConditions(moduleName, fieldName, fieldMap, fieldJsonObj, conditionList);
                }
                criteria.groupOrConditions(conditionList);
            }
            if (!criteria.isEmpty()) {
                context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
            }
        }
        return false;
    }

    private void setConditions(String moduleName, String fieldName, Map<String, FacilioField> fieldMap, JSONObject fieldJson, List<Condition> conditionList) throws Exception {

        int operatorId;
        String operatorName;

        FacilioField field = fieldMap.get(fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field is not found for: " + fieldName + " : " + moduleName);
        }

        Operator operator;
        if (fieldJson.containsKey("operatorId")) {
            operatorId = (int) (long) fieldJson.get("operatorId");
            operator = Operator.getOperator(operatorId);
            operatorName = operator.getOperator();
        } else {
            operatorName = (String) fieldJson.get("operator");
            operator = field.getDataTypeEnum().getOperator(operatorName);
            operatorId = operator.getOperatorId();
        }
        JSONArray value = (JSONArray) fieldJson.get("value");

        Condition condition = new Condition();
        condition.setField(field);
        condition.setOperatorId(operatorId);

        if(operator.isValueNeeded() && (value!=null && value.size()>0)) {
            StringBuilder values = new StringBuilder();
            boolean isFirst = true;
            Iterator<String> iterator = value.iterator();
            while(iterator.hasNext())
            {
                String obj = iterator.next();
                if(!isFirst) {
                    values.append(",");
                }
                else {
                    isFirst = false;
                }
                if (operator instanceof StringOperators) {
                    obj = obj.replace(",", StringOperators.DELIMITED_COMMA);
                }
                values.append(ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), obj.trim()));
            }
            String valuesString = values.toString();
            if (condition.getOperator() instanceof FieldOperator) {
                condition.setValueField(fieldMap.get(valuesString));
            } else {
                condition.setValue(valuesString);
            }
            if (fieldJson.containsKey("orFilters")) {	// To have or condition for different fields..eg: (space=1 OR purposeSpace=1)
                JSONArray orFilters = (JSONArray) fieldJson.get("orFilters");
                for(int i=0;i<orFilters.size();i++) {
                    JSONObject fieldJsonObj = (JSONObject) orFilters.get(i);
                    if (!fieldJsonObj.containsKey("operator")) {
                        fieldJsonObj.put("operator", operatorName);
                    }
                    if (!fieldJsonObj.containsKey("value")) {
                        fieldJsonObj.put("value", value);
                    }
                    setConditions(moduleName, (String)fieldJsonObj.get("field"), fieldMap, fieldJsonObj, conditionList);
                }
            }
        }
        condition.validateValue();
        conditionList.add(condition);
    }

    private List<FacilioField> getAllFields(String moduleName) throws Exception {
        switch (moduleName) {
            case FacilioConstants.ContextNames.USER_DELEGATION:
                return FieldFactory.getUserDelegationFields();
            case FacilioConstants.ContextNames.READING_IMPORT_APP:
                return FieldFactory.getReadingImportFields();
            case FacilioConstants.ContextNames.MULTI_MODULE_IMPORT:
                return FieldFactory.getImportDataDetailsFields();
            case FacilioConstants.ContextNames.SFG20.SYNC_HISTORY:
                return FieldFactory.getSFG20SyncHistoryFields();

        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        if (module.getTypeEnum() == FacilioModule.ModuleType.TIME_LOG ){
            return moduleBean.getAllFields(moduleName);
        }
        return null;
    }
}
