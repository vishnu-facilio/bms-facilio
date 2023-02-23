package com.facilio.services.pdf;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.iam.accounts.util.IAMUserUtil;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;

public abstract class PDFService {

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
     * Export any facilio app url as PDF/Screenshot
     *
     * @return fileId
     */
    public abstract long exportAppURL(String fileName, String pageURL, ExportType exportType, ExportOptions exportOptions) throws Exception;

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
