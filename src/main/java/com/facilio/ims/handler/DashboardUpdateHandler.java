package com.facilio.ims.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fms.message.Message;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.pdf.PDFOptions;
import com.facilio.services.pdf.PDFService;
import com.facilio.services.pdf.PDFServiceFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class DashboardUpdateHandler extends ImsHandler {
    private static final Logger LOGGER = LogManager.getLogger(DashboardUpdateHandler.class.getName());

    public static String KEY = "__dashboard_update__";
    public void processMessage(Message message) {
        try {
            if (message != null && message.getContent() != null) {
                Map<String,Object> messageContent = (Map<String, Object>) message.getContent();
                Long dashboardId = Long.parseLong(messageContent.get("dashboardId").toString());
                String dashboardUrl = (String) messageContent.get("dashboardUrl");
                String fileName = "dashboardThumbNail " + System.currentTimeMillis();
                DashboardContext dashboard = DashboardUtil.getDashboard(dashboardId);
                JSONObject pageParams = new JSONObject();
                pageParams.put("linkname",dashboard.getLinkName());
                JSONObject options = new JSONObject();
                PDFOptions screenshotOptions = FieldUtil.getAsBeanFromJson(options, PDFOptions.class);
                long fileId = PDFServiceFactory.getPDFService().exportPage(fileName, AccountUtil.getCurrentApp().getLinkName(),"dashboard", pageParams, PDFService.ExportType.SCREENSHOT, screenshotOptions);
                GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getDashboardModule().getTableName())
                        .fields(FieldFactory.getDashboardFields())
                        .andCustomWhere("ID = ?", dashboardId);

                dashboard.setFileId(fileId);
                Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
                updateBuilder.update(props);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating dashboard Thumbnail : ", e);
        }
    }
}
