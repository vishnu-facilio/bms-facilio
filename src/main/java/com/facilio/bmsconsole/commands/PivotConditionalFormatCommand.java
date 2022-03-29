package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.Predicate;
import org.json.simple.JSONObject;

import java.util.*;

public class PivotConditionalFormatCommand extends FacilioCommand {
    List<String> pivotAliases = new ArrayList<>();
    JSONObject templateJson;
    HashMap<String, Object> columnFormatting;
    HashMap<String, Object> pivotTableData;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        templateJson = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        columnFormatting = (HashMap<String, Object>) templateJson.get("columnFormatting");

        pivotAliases.addAll((Collection<? extends String>) context.get(FacilioConstants.ContextNames.ROW_HEADERS));
        pivotAliases.addAll((Collection<? extends String>) context.get(FacilioConstants.ContextNames.DATA_HEADERS));
        pivotAliases.addAll((Collection<? extends String>) context.get(FacilioConstants.ContextNames.FORMULA_HEADERS));

        pivotTableData = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA);
        executeConditionalFormatting();
        return false;
    }


    private ArrayList<Map<String,Object>> getConditionalFormatList(String alias) {
        ArrayList<Map<String,Object>> conditionalFormat = new ArrayList<>();
        Map<String, Object> columnConfig = (Map<String, Object>) columnFormatting.get(alias);
        if (columnConfig.containsKey("conditionalFormat")) {
            conditionalFormat = (ArrayList<Map<String,Object>>) columnConfig.get("conditionalFormat");
        }
        return conditionalFormat;
    }

    private void executeConditionalFormatting(){
        for(String alias : pivotAliases){
            ArrayList<Map<String,Object>> conditionalFormatList = getConditionalFormatList(alias);
            if(!conditionalFormatList.isEmpty()){
                applyConditionalFormatting(conditionalFormatList, alias);
            }
        }
    }

    private void applyConditionalFormatting(ArrayList<Map<String,Object>> conditionalFormatList, String alias){
        ArrayList<Map<String,Object>> pivotRecords = (ArrayList<Map<String, Object>>) pivotTableData.get("records");
        for (Map<String,Object> paramMap: pivotRecords) {
            Map<String,Object> paramFlatMap = getParamMap(paramMap);
            for(Map<String,Object> conditionFormat: conditionalFormatList){
                Map<String,Object> criteriaMap = (Map<String, Object>) conditionFormat.get("criteria");
                Criteria criteria = FieldUtil.getAsBeanFromMap(criteriaMap, Criteria.class);
                if(evaluateCriteria(criteria, paramFlatMap)){
                    Map<String,Object> style = (Map<String, Object>) conditionFormat.get("styles");
                    applyStyle(style, paramMap, alias);
                    break;
                }
            }
        }
    }

    private void applyStyle(Map<String, Object> style, Map<String,Object> paramMap, String alias){
        Map<String,Object> paramValue = (Map<String, Object>) paramMap.get(alias);
        Map<String,Object> columnStyleObj = (Map<String, Object>) paramValue.get("style");
        if(style.containsKey("displayValue") && style.get("displayValue") != null && !style.get("displayValue").equals("")){
            paramValue.put("formattedValue", style.get("displayValue"));
        }
        columnStyleObj.putAll(style);
        columnStyleObj.remove("displayValue");
        paramValue.put("style", columnStyleObj);
        paramMap.put(alias,paramValue);
    }

    private Map<String,Object> getParamMap(Map<String,Object> paramMap){
        Map<String,Object> result = new HashMap<>();
        for(String alias: paramMap.keySet()){
            Map<String, Object> paramValue = (Map<String, Object>) paramMap.get(alias);
            result.put(alias, paramValue.get("value"));
        }
        return result;
    }

    public boolean evaluateCriteria(Criteria criteria, Map<String,Object> data){
        if(criteria!= null && data != null) {
            Predicate predicate = criteria.computePredicate(data);
            if(predicate != null) {
                return predicate.evaluate(data);
            }
            return false;
        }
        return false;
    }

}