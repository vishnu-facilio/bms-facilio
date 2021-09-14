package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Properties;

public class ReportTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject jsonObject,@NonNull Properties properties ) throws Exception {

        JSONObject result = (JSONObject)jsonObject.get("result");
        if(result != null && !result.isEmpty()) {
            JSONObject report = (JSONObject)result.get("report");
            if(report != null && !report.isEmpty()) {
                String reportId = String.valueOf(report.get("id"));
                String reportKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId);
                report.put(TranslationConstants.NAME,getTranslation(properties,reportKey,(String)report.get(TranslationConstants.NAME)));
                String chartState = (String)report.get("chartState");
                if(chartState != null) {
                    JSONParser parser = new JSONParser();
                    JSONObject chartStateObject = (JSONObject)parser.parse(chartState);
                    if(chartStateObject != null && !chartStateObject.isEmpty()) {
                        JSONObject axis = (JSONObject)chartStateObject.get("axis");
                        if(axis != null && !axis.isEmpty()) {
                            JSONObject xAxis = (JSONObject)axis.get("x");
                            if(xAxis != null && !xAxis.isEmpty()) {
                                JSONObject label = (JSONObject)xAxis.get("label");
                                if(label != null && !label.isEmpty()) {
                                    String text = (String)label.get("text");
                                    if(text != null) {
                                        String xAxisKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId + ".xAxis");
                                        label.put(TranslationConstants.NAME,getTranslation(properties,xAxisKey,text));
                                    }
                                }
                            }
                            JSONObject yAxis = (JSONObject)axis.get("y");
                            if(yAxis != null && !yAxis.isEmpty()) {
                                JSONObject label = (JSONObject)yAxis.get("label");
                                if(label != null && !label.isEmpty()) {
                                    String text = (String)label.get("text");
                                    if(text != null) {
                                        String yAxisKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId + ".yAxis");
                                        label.put(TranslationConstants.NAME,getTranslation(properties,yAxisKey,text));
                                    }
                                }
                            }
                        }
                        JSONObject benchMark = (JSONObject)chartStateObject.get("benchMark");
                        if(benchMark != null && !benchMark.isEmpty()) {
                            String label = (String)benchMark.get("label");
                            if(label != null) {
                                String benchMarkKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId + ".benchMark");
                                benchMark.put("label",getTranslation(properties,benchMarkKey,label));
                            }
                        }
                    }
                }
            }
        }
        return jsonObject;
    }
}
