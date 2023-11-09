package com.facilio.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.util.DBConf;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class FacilioIndexJsp {
    public static final Logger LOGGER = LogManager.getLogger(FacilioIndexJsp.class.getName());
    public static final String NEW_SERVER_QUERY_PARAM_KEY = "version", NEW_SERVER_QUERY_PARAM_VAL = "revive";
    public static final String NEW_VERSION_GROUP_NAME = "v2";

    public static boolean redirectToNewVersion(HttpServletRequest request) {
        if (!FacilioProperties.isDevelopment() && !DBConf.getInstance().isNewVersion() && request.getAttribute(RequestUtil.REQUEST_APP_NAME) == null) {
            Organization org = (Organization) request.getAttribute(RequestUtil.REQUEST_CURRENT_ORG);
            if (org != null) {
                return NEW_VERSION_GROUP_NAME.equals(org.getGroupName());
            }
        }
        return false;
    }

    @SneakyThrows
    public static void setOrgAttribute (HttpServletRequest request) {
        String currentOrg = RequestUtil.getCookieValue(request, FacilioCookie.CURRENT_ORG_COOKIE);
        if (StringUtils.isNotEmpty(currentOrg)) {
            Organization org = IAMOrgUtil.getOrg(currentOrg);
            if (org != null) {
                request.setAttribute(RequestUtil.REQUEST_CURRENT_ORG, org);
            }
        }
    }

    public static String constructRequestHeaders (HttpServletRequest request) {
        Organization org = (Organization) request.getAttribute(RequestUtil.REQUEST_CURRENT_ORG);
        JSONObject headers = new JSONObject();
        if (org != null) {
            if (StringUtils.isNotEmpty(org.getGroupName())) {
                headers.put("X-Org-Group", org.getGroupName());
            }
            headers.put("X-Org-Id", org.getOrgId());
        }
        return headers.toJSONString();
    }
}
