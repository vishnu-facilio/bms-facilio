package com.facilio.services.pdf;

import com.facilio.aws.util.FacilioProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PDFServiceFactory {

    private static final Logger LOGGER = LogManager.getLogger(PDFServiceFactory.class.getName());

    private static final String PDF_SERVICE_CONFIG = "service.pdf";
    private static final String PDF_SERVICE_URL_CONFIG = "service.pdf.url";

    private static PDFService pdfService = null;

    public static PDFService getPDFService() {
        if (pdfService != null) {
            return pdfService;
        }
        String pdfServiceProp = FacilioProperties.getConfig(PDF_SERVICE_CONFIG);
        switch (pdfServiceProp){
            case "external":
                String pdfServiceUrl = FacilioProperties.getConfig(PDF_SERVICE_URL_CONFIG);
                if (StringUtils.isEmpty(pdfServiceUrl)) {
                    LOGGER.error("service.pdf.url property is not configured, hence local puppeteer service is taken.");
                    pdfService = new LocalPDFService();
                }
                else {
                    pdfService = new ExternalPDFService(pdfServiceUrl);
                }
                break;
            default: pdfService = new LocalPDFService(); break;
        }
        return pdfService;
    }
}
