package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.actions.GetModuleFromReportContextCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.ReportFolderTranslationImplV2;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Map;
import java.util.Properties;

public class GetReportTranslationFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext webTabContext,Map<String, String> filters,Properties properties ) throws Exception {

        FacilioUtil.throwIllegalArgumentException(!WebTabContext.Type.REPORT.equals(WebTabContext.Type.valueOf(webTabContext.getType())),"Invalid webTab Type for fetching Report Fields");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(filters.get("reportId")),"Report id is mandatory param for fetching Report fields");

        JSONArray fields = new JSONArray();
        long reportId = Long.parseLong(filters.get("reportId"));

        ReportContext reportContext = ReportUtil.getReport(reportId);
        FacilioUtil.throwIllegalArgumentException(reportContext == null,"Report not found");

        String reportKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,String.valueOf(reportId));
        fields.add(TranslationsUtil.constructJSON(reportContext.getName(),ReportFolderTranslationImplV2.REPORT,TranslationConstants.DISPLAY_NAME,String.valueOf(reportId),reportKey,properties));

        FacilioChain chain = FacilioChain.getNonTransactionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT,reportContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,reportContext.getModule().getName());
        chain.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        chain.addCommand(new GetModuleFromReportContextCommand());
        chain.execute();

        ReportContext report = (ReportContext)context.get("report");
        if(report != null) {
            JSONParser parser = new JSONParser();
            String chartState = report.getChartState();
            if(chartState != null) {
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
                                    fields.add(TranslationsUtil.constructJSON(text,ReportFolderTranslationImplV2.REPORT,TranslationConstants.DISPLAY_NAME,reportId + ".xAxis",xAxisKey,properties));
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
                                    fields.add(TranslationsUtil.constructJSON(text,ReportFolderTranslationImplV2.REPORT,TranslationConstants.DISPLAY_NAME,reportId + ".yAxis",yAxisKey,properties));
                                }
                            }
                        }
                    }
                    JSONObject benchMark = (JSONObject)chartStateObject.get("benchMark");
                    if(benchMark != null && !benchMark.isEmpty()){
                        String label = (String)benchMark.get("label");
                        if(label != null) {
                            String benchMarkKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId + ".benchMark");
                            fields.add(TranslationsUtil.constructJSON(label,ReportFolderTranslationImplV2.REPORT,TranslationConstants.DISPLAY_NAME,reportId + ".benchMark",benchMarkKey,properties));
                        }
                    }
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",fields);

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
