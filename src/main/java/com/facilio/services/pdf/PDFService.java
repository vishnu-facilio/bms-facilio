package com.facilio.services.pdf;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class PDFService {

    private static final String PDF_CONFIG_FILE = FacilioUtil.normalizePath("conf/pdfconfig.yml");

    private static Map<String, Map<String,String>> pdfPages = new HashMap<>();

    static  {
        init();
    }

    public static void init() {
        Map<String, Object> configs = null;
        try {
            configs = FacilioUtil.loadYaml(PDF_CONFIG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfPages = (Map<String, Map<String,String>>) configs.get("pdf_pages");
    }

    private long orgId;
    private long userId;
    public PDFService() {
        this.orgId = AccountUtil.getCurrentOrg().getId();
        this.userId = AccountUtil.getCurrentUser().getId();
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    protected String constructPageURL(String appLinkName, String pageName, JSONObject pageParams) throws Exception {
        if (StringUtils.isEmpty(pageName) || !pdfPages.containsKey(pageName)) {
            throw new IllegalArgumentException("No pdf page has been configured for pageName: "+pageName);
        }
        if (pageParams == null) {
            pageParams = new JSONObject();
        }
        String templatePageURL = getAppBaseURL() + "/" + appLinkName + pdfPages.get(pageName).get("url");
        String pageURL = StringSubstitutor.replace(templatePageURL, pageParams);
        return pageURL;
    }
    protected String getAppBaseURL() {
        try {
            HttpServletRequest req = ServletActionContext.getRequest();
            if (req != null) {
                return FacilioProperties.getAppProtocol() + "://" + req.getServerName();
            }
        } catch (Exception e) {}
        return FacilioProperties.getAppProtocol() + "://" + FacilioProperties.getAppDomain();
    }

    protected String getUserToken() {
        if (AccountUtil.getCurrentUser().isPortalUser()) {
            HttpServletRequest request = ServletActionContext.getRequest();
            String facilioToken = null;
            if(!FacilioProperties.isProduction()){
                facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
            }
            else {
                facilioToken = FacilioCookie.getUserCookie(request, "fc.idToken.facilioportal");
            }
            return facilioToken;
        }
        return IAMUserUtil.createJWT("id", "auth0", String.valueOf(AccountUtil.getCurrentUser().getUid()), System.currentTimeMillis()+60*60000);
    }

    /**
     * Export any facilio app page as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportPage(String fileName, String appLinkName, String pageName, JSONObject pageParams, ExportType exportType, ExportOptions exportOptions) throws Exception;

    /**
     * Export any facilio app page url (only facilio app page url)
     *
     * @return fileId
     */
    public abstract long exportURL(String fileName, String pageURL, ExportType exportType, ExportOptions exportOptions) throws Exception;

    /**
     * Export connected app widget as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportWidget(String fileName, String widgetLinkName, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception;

    /**
     * Export connected app widget as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportWidget(String fileName, long widgetId, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception;

    /**
     * Export html content as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportHTML(String fileName, String htmlContent, ExportType exportType, ExportOptions exportOptions) throws Exception;

    /**
     * Export handlebars template with data json as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportTemplate(String fileName, String template, JSONObject data, ExportType exportType, ExportOptions exportOptions) throws Exception;

    public enum ExportType {
        PDF,
        SCREENSHOT;
    }
}
