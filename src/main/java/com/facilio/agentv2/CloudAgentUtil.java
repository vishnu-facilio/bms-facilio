package com.facilio.agentv2;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldUtil;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;

public class CloudAgentUtil {
	
	private static class Urls {
		private static final String ADD_AGENT = "/api/v1/agent/add";
	}
	
	public static void addCloudServiceAgent(FacilioAgent agent) throws Exception {
		String url = getUrl(Urls.ADD_AGENT);

		Map<String, Object> props = FieldUtil.getAsProperties(agent);
		JSONObject body = new JSONObject();
		body.put("agent", props);

		String response = FacilioHttpUtils.doHttpPost(url, getSecretKeyHeader(), null, body);
		if (StringUtils.isNotEmpty(response)) {
			JSONObject obj = FacilioUtil.parseJson(response);
			if (obj.containsKey("code")) {
				int code = FacilioUtil.parseInt(obj.get("code").toString());
				if (code != 0) {
					throw new FacilioException(obj.get("message").toString());
				}
			}
		}
	}

	private static Map<String, String> getSecretKeyHeader() {
		// TODO add the auth header once the fw is done
		return null;
	}

	private static String getUrl(String url) {
		return RequestUtil.getProtocol(ServletActionContext.getRequest()) + "://" + FacilioProperties.getCloudAgentUrl() + url;
	}
}
