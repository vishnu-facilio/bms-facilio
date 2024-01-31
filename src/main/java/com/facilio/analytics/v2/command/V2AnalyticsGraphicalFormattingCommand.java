package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.Predicate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class V2AnalyticsGraphicalFormattingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        V2ReportContext report_v2 = context.get("report_v2") != null ? (V2ReportContext) context.get("report_v2") : (V2ReportContext) context.get("v2_report");
        JSONObject reportData = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        JSONParser parser = new JSONParser();


        if(report_v2 != null)
        {
            for (V2MeasuresContext measure : report_v2.getMeasures())
            {
                List<String> alias_list = new ArrayList<>();
                alias_list.add(measure.getAliases().get("actual"));

                if(report.getBaseLines() != null && report.getBaseLines().size() > 0 && report.getBaseLines().get(0) != null)
                {
                    ReportBaseLineContext baseLine = report.getBaseLines().get(0);
                    if(baseLine != null && baseLine.getBaseLine() != null && baseLine.getBaseLine().getName() != null && measure.getAliases().containsKey(baseLine.getBaseLine().getName()))
                    {
                        alias_list.add(measure.getAliases().get(baseLine.getBaseLine().getName()));
                    }
                }
                if (measure.getFormatting() != null)
                {
                    JSONArray all_formatting = (JSONArray) parser.parse(measure.getFormatting());
                    JSONArray mainMeasure = new JSONArray();
                    JSONArray baselineMeasure = new JSONArray();
                    Iterator<JSONObject> iter = all_formatting.iterator();
                    while(iter.hasNext())
                    {
                        JSONObject format_json  = iter.next();
                        if(alias_list.size() > 0 && format_json.containsKey("alias") && alias_list.get(0).equals(format_json.get("alias")))
                        {
                            mainMeasure.add(format_json);
                        }
                        else if(alias_list.size() > 1 && format_json.containsKey("alias") && alias_list.get(1).equals(format_json.get("alias")))
                        {
                            baselineMeasure.add(format_json);
                        }
                    }
                    if(mainMeasure.size() > 0){
                        applyConditionalFormatting(mainMeasure, alias_list.get(0), reportData);
                    }
                    if(baselineMeasure.size() > 0) {
                        applyConditionalFormatting(baselineMeasure, alias_list.get(1), reportData);
                    }
                }
            }
        }
        return false;
    }

    private void applyConditionalFormatting(JSONArray all_formatting, String alias, JSONObject reportData)
    {
        ArrayList<Map<String,Object>> records = (ArrayList<Map<String, Object>>) reportData.get("data");
        for (Map<String,Object> paramMap: records)
        {
            Map<String,Object> paramFlatMap = getAliasMapToEvaluateCriteria(paramMap);
            Iterator<JSONObject> iter = all_formatting.iterator();
            while(iter.hasNext())
            {
                JSONObject formatting = iter.next();
                Map<String,Object> criteriaMap = (Map<String, Object>) formatting.get("criteria");
                Criteria criteria = FieldUtil.getAsBeanFromMap(criteriaMap, Criteria.class);
                if(evaluateGraphicalFormatting(criteria, paramFlatMap)){
                    paramMap.put(new StringBuilder().append(alias).append("_formating").toString(), formatting.get("linkName"));
                    break;
                }
            }
        }
    }
    private Map<String,Object> getAliasMapToEvaluateCriteria(Map<String,Object> paramMap){
        Map<String,Object> result = new HashMap<>();
        for(String alias: paramMap.keySet()){
            Object paramValue = (Object) paramMap.get(alias);
            if(paramValue != null) {
                result.put(alias, paramValue);
            }
        }
        return result;
    }

    public boolean evaluateGraphicalFormatting(Criteria criteria, Map<String,Object> data){
        try {
            Predicate predicate = criteria.computePredicate(data);
            return predicate.evaluate(data);
        } catch (Exception e){
            return false;
        }
    }
}
