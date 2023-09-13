package com.facilio.services.pdf;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.modules.FieldUtil;
import com.facilio.services.FacilioHttpUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ExternalPDFService extends PDFService {

    private String serviceURL;
    public ExternalPDFService(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    private String getApiURL(ExportType exportType, String exportFor) {
        return serviceURL + "/generate/" + exportType.name().toLowerCase() + "/" + exportFor;
    }


    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", getUserToken());
        headers.put("X-Org-Id", getOrgId() + "");
        headers.put("X-Is-Export", "true");
        if (AccountUtil.getCurrentSiteId() > 0) {
            headers.put("X-current-site", AccountUtil.getCurrentSiteId() + "");
        }

        try {
            HttpServletRequest req = ServletActionContext.getRequest();
            if (req != null) {
                String value = FacilioCookie.getUserCookie(req, "fc.sandbox");
                if (value != null && "true".equals(value)) {
                    headers.put("fc.sandbox", "true");
                }
            }
        } catch (Exception e) {}
        return headers;
    }

    @Override
    public long exportPage(String fileName, String appLinkName, String pageName, JSONObject pageParams, ExportType exportType, ExportOptions exportOptions) throws Exception {
        String pageURL = constructPageURL(appLinkName, pageName, pageParams);
        String apiURL = getApiURL(exportType, "page");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();

        Map<String, String> params = new HashMap<String, String>();
        params.put("url", pageURL);
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, getAuthHeaders(), params, null, null, fileName, null);
    }

    @Override
    public long exportURL(String fileName, String pageURL, ExportType exportType, ExportOptions exportOptions) throws Exception {
        pageURL = getAppBaseURL() + pageURL;
        String apiURL = getApiURL(exportType, "page");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();

        Map<String, String> params = new HashMap<String, String>();
        params.put("url", pageURL);
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, getAuthHeaders(), params, null, null, fileName, null);
    }

    @Override
    public long exportWidget(String fileName, String widgetLinkName, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception {
        String apiURL = getApiURL(exportType, "widget");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();
        context = (context == null) ? new JSONObject() : context;

        JSONObject account = new JSONObject();
        account.put("org", FieldUtil.getAsJSON(AccountUtil.getCurrentOrg()));
        account.put("user", FieldUtil.getAsJSON(AccountUtil.getCurrentUser()));

        Map<String, String> params = new HashMap<String, String>();
        params.put("baseURL", getAppBaseURL());
        params.put("widgetLinkName", widgetLinkName);
        params.put("context", context.toJSONString());
        params.put("account", account.toJSONString());
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, getAuthHeaders(), params, null, null, fileName, null);
    }

    @Override
    public long exportWidget(String fileName, long widgetId, ExportType exportType, ExportOptions exportOptions, JSONObject context) throws Exception {
        String apiURL = getApiURL(exportType, "widget");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();
        context = (context == null) ? new JSONObject() : context;

        JSONObject account = new JSONObject();
        account.put("org", FieldUtil.getAsJSON(AccountUtil.getCurrentOrg()));
        account.put("user", FieldUtil.getAsJSON(AccountUtil.getCurrentUser()));

        Map<String, String> params = new HashMap<String, String>();
        params.put("baseURL", getAppBaseURL());
        params.put("widgetId", widgetId+"");
        params.put("context", context.toJSONString());
        params.put("account", account.toJSONString());
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, getAuthHeaders(), params, null, null, fileName, null);
    }

    @Override
    public long exportHTML(String fileName, String htmlContent, ExportType exportType, ExportOptions exportOptions) throws Exception {
        String apiURL = getApiURL(exportType, "html");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();

        Map<String, String> params = new HashMap<String, String>();
        params.put("html", htmlContent);
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, null, params, null, null, fileName, null);
    }

    @Override
    public long exportTemplate(String fileName, String template, JSONObject data, ExportType exportType, ExportOptions exportOptions) throws Exception {
        String apiURL = getApiURL(exportType, "template");
        JSONObject options = (exportOptions != null) ? FieldUtil.getAsJSON(exportOptions) : new JSONObject();

        Map<String, String> params = new HashMap<String, String>();
        params.put("template", template);
        params.put("data", data.toJSONString());
        params.put("options", options.toJSONString());

        return FacilioHttpUtils.doHttpPostWithFileResponse(apiURL, null, params, null, null, fileName, null);
    }
}
