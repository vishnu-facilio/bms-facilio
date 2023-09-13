package com.facilio.services.pdf;

import com.facilio.fs.FileInfo;
import com.facilio.pdf.PdfUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.json.simple.JSONObject;

import java.io.File;

public class LocalPDFService extends PDFService {

    @Override
    public long exportPage(String fileName, String appLinkName, String pageName, JSONObject pageParams, ExportType exportType, ExportOptions exportOptions) throws Exception {
        String pageURL = constructPageURL(appLinkName, pageName, pageParams);
        FileInfo.FileFormat fileFormat = FileInfo.FileFormat.PDF;
        if (exportType == ExportType.SCREENSHOT) {
            fileFormat = FileInfo.FileFormat.IMAGE;
        }
        String pdfFileLocation = PdfUtil.convertUrlToPdf(pageURL, null, null, fileFormat);
        if (pdfFileLocation != null) {
            File pdfFile = new File(pdfFileLocation);

            FileStore fs = FacilioFactory.getFileStoreFromOrg(getOrgId(), getUserId());
            long fileId = fs.addFile(fileName, pdfFile, fileFormat.getContentType());
            return fileId;
        }
        return -1;
    }

    @Override
    public long exportURL(String fileName, String pageURL, ExportType exportType, ExportOptions exportOptions) throws Exception {
        pageURL = getAppBaseURL() + pageURL;
        FileInfo.FileFormat fileFormat = FileInfo.FileFormat.PDF;
        if (exportType == ExportType.SCREENSHOT) {
            fileFormat = FileInfo.FileFormat.IMAGE;
        }
        String pdfFileLocation = PdfUtil.convertUrlToPdf(pageURL, null, null, fileFormat);
        if (pdfFileLocation != null) {
            File pdfFile = new File(pdfFileLocation);

            FileStore fs = FacilioFactory.getFileStoreFromOrg(getOrgId(), getUserId());
            long fileId = fs.addFile(fileName, pdfFile, fileFormat.getContentType());
            return fileId;
        }
        return -1;
    }

    @Override
    public long exportWidget(String fileName, String widgetLinkName, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception {
        throw new Exception("Export widget not supported in Local PDF Service.");
    }

    @Override
    public long exportWidget(String fileName, long widgetId, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception {
        throw new Exception("Export widget not supported in Local PDF Service.");
    }

    @Override
    public long exportHTML(String fileName, String htmlContent, ExportType exportType, ExportOptions exportOptions) throws Exception {
        FileInfo.FileFormat fileFormat = FileInfo.FileFormat.PDF;
        if (exportType == ExportType.SCREENSHOT) {
            fileFormat = FileInfo.FileFormat.IMAGE;
        }
        String pdfFileLocation = PdfUtil.convertUrlToPdf(null, htmlContent, null, fileFormat);
        if (pdfFileLocation != null) {
            File pdfFile = new File(pdfFileLocation);

            FileStore fs = FacilioFactory.getFileStoreFromOrg(getOrgId(), getUserId());
            long fileId = fs.addFile(fileName, pdfFile, fileFormat.getContentType());
            return fileId;
        }
        return -1;
    }

    @Override
    public long exportTemplate(String fileName, String template, JSONObject data, ExportType exportType, ExportOptions exportOptions) throws Exception {
        throw new Exception("Export template not supported in Local PDF Service.");
    }
}
