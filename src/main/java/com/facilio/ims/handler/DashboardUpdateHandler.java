package com.facilio.ims.handler;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fms.message.Message;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.pdf.PdfUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.File;
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
                long fileId = 0;
                String fileName = "dashboardThumbNail " + System.currentTimeMillis();
                FileInfo.FileFormat format = FileInfo.FileFormat.IMAGE;
                JSONObject additionalInfo = new JSONObject();
                String pdfFileLocation = PdfUtil.convertUrlToPdf(dashboardUrl, null, additionalInfo, format);
                File pdfFile = new File(pdfFileLocation);
                if(pdfFileLocation != null) {
                    FileStore fs = FacilioFactory.getFileStore();
                    fileId = fs.addFile(fileName + format.getExtention(), pdfFile, format.getContentType());
                }
                DashboardContext dashboard = DashboardUtil.getDashboard(dashboardId);
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
