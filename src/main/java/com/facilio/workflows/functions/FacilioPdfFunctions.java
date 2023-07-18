package com.facilio.workflows.functions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.services.filestore.FileStoreFactory;
import com.facilio.services.pdf.PDFOptions;
import com.facilio.services.pdf.PDFService;
import com.facilio.services.pdf.PDFServiceFactory;
import com.facilio.services.pdf.ScreenshotOptions;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.PDF)
public class FacilioPdfFunctions {

    public static final List<String> reportsName = Arrays.asList(FacilioConstants.ContextNames.READING_REPORT,FacilioConstants.ContextNames.MODULE_REPORT,FacilioConstants.ContextNames.ALARM_REPORT);
    public Object pageToPDF(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);

        String pageName = (String) objects[0];

        Map<String, Object> pageParams = (Map<String, Object>) objects[1];
        if (reportsName.contains(pageName)) {
            pageParamCheck(pageParams);
        }

        JSONObject obj = new JSONObject();
        obj.putAll(pageParams);
        String fileName = (String) objects[2];
        if (fileName == null){
            fileName = "download-" + System.currentTimeMillis() + ".pdf";
        }

        Map<String,Object> options = (Map<String, Object>) objects[3];

        if (options == null) {
            options = new HashMap<>();
        }

        PDFOptions pdfOptions = FieldUtil.getAsBeanFromMap(options, PDFOptions.class);

        String appLinkName = null;

        if (AccountUtil.getCurrentApp() == null) {
            appLinkName = SignupUtil.maintenanceAppSignup() ? FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP : FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
        }else {
            appLinkName = AccountUtil.getCurrentApp().getLinkName();
        }

        long fileId = PDFServiceFactory.getPDFService().exportPage(fileName,appLinkName, pageName,obj, PDFService.ExportType.PDF, pdfOptions);

        String fileUrl = null;
        if (fileId > 0) {
            if(pageName == FacilioConstants.ContextNames.READING_REPORT_EDIT){
                return fileId;
            }
            fileUrl = FileStoreFactory.getInstance().getFileStore().getDownloadUrl(fileId);
        }

        return fileUrl;
    }

    public Object pageToScreenshot(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);

        String pageName = (String) objects[0];

        Map<String,Object> pageParams = (Map<String, Object>) objects[1];
        if(reportsName.contains(pageName)){
            pageParamCheck(pageParams);
        }

        JSONObject obj = new JSONObject();
        obj.putAll(pageParams);
        String fileName = (String) objects[2];
        if (fileName == null){
            fileName = "download-" + System.currentTimeMillis() + ".jpeg";
        }

        Map<String,Object> options = (Map<String, Object>) objects[3];

        if (options == null) {
            options = new HashMap<>();
        }

        ScreenshotOptions screenshotOptions = FieldUtil.getAsBeanFromMap(options, ScreenshotOptions.class);
        String appLinkName = null;

        if (AccountUtil.getCurrentApp() == null) {
            appLinkName = SignupUtil.maintenanceAppSignup() ? FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP : FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
        }else {
            appLinkName = AccountUtil.getCurrentApp().getLinkName();
        }
        long fileId = PDFServiceFactory.getPDFService().exportPage(fileName,appLinkName, pageName,obj, PDFService.ExportType.SCREENSHOT, screenshotOptions);

        String fileUrl = null;
        if (fileId > 0) {
            fileUrl = FileStoreFactory.getInstance().getFileStore().getDownloadUrl(fileId);
        }

        return fileUrl;

    }


    private void checkParam(Object[] objects) {
        if (objects == null || objects.length <= 0){
            throw new RuntimeException("Required parameters missing");
        }
        else if (objects[0] == null) {
            throw new RuntimeException("Page name missing");
        }
        else if (objects[1] == null) {
            throw new RuntimeException("Page params missing");
        }
    }
    private void pageParamCheck(Map<String,Object> pageParams){
        for(String param : pageParams.keySet()){
            if(pageParams.get(param) instanceof Map){
                JSONObject Object = new JSONObject();
                Object.putAll((Map) pageParams.get(param));
                pageParams.put(param, ReportsUtil.encodeURIComponent(Object.toJSONString()));
            }
        }
    }

}
