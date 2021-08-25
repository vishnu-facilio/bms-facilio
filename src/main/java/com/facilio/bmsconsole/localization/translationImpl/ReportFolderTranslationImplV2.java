package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ReportFolderTranslationImplV2 implements TranslationIfc {

    public static String REPORT_FOLDER = "reportFolder";
    public static String REPORT = "report";

    public static String getTranslationKey ( String prefix,String value ) {
        return prefix + "." + value + "." + TranslationConstants.DISPLAY_NAME;
    }

    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject resultObject = (JSONObject)json.get("result");
        JSONArray reportFolders = (JSONArray)resultObject.get("reportFolders");

        if(reportFolders != null && !reportFolders.isEmpty()) {
            for (int i = 0; i < reportFolders.size(); i++) {

                JSONObject reportFolder = (JSONObject)reportFolders.get(i);
                String reportFolderId = String.valueOf(reportFolder.get("id"));
                String reportFolderKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT_FOLDER,reportFolderId);
                reportFolder.put(TranslationConstants.NAME,getTranslation(translationFile,reportFolderKey,(String)reportFolder.get(TranslationConstants.NAME)));

                JSONArray reports = (JSONArray)reportFolder.get("reports");

                if(reports != null && !reports.isEmpty()) {

                    for (int j = 0; j < reports.size(); j++) {
                        JSONObject report = (JSONObject)reportFolders.get(j);
                        String reportId = String.valueOf(report.get("id"));
                        String reportKey = ReportFolderTranslationImplV2.getTranslationKey(ReportFolderTranslationImplV2.REPORT,reportId);
                        report.put(TranslationConstants.NAME,getTranslation(translationFile,reportKey,(String)report.get(TranslationConstants.NAME)));
                    }
                }
            }
        }
        return json;
    }
}
