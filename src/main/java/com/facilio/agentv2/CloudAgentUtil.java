package com.facilio.agentv2;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.AuthUtils;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants.Services;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;
import com.facilio.util.ServiceHttpUtils;

public class CloudAgentUtil {
	
	private static class Urls {
		private static final String ADD_AGENT = "/api/v1/agent/add";
	}
	
	public static void addCloudServiceAgent(FacilioAgent agent) throws Exception {
		
		Map<String, Object> props = FieldUtil.getAsProperties(agent);

		String response = doPost(Urls.ADD_AGENT, "agent", props);
		if (StringUtils.isNotEmpty(response)) {
			JSONObject obj = FacilioUtil.parseJson(response);
			if (obj.containsKey("code")) {
				int code = FacilioUtil.parseInt(obj.get("code").toString());
				if (code != 0) {
					throw new FacilioException(obj.get("message").toString());
				}
			}
		}
		else {
			throw new FacilioException("Agent not added. Please check the details again");
		}
	}
	
	private static String doPost(String url, String key, Map<String, Object> props) throws Exception {
		JSONObject body = new JSONObject();
		body.put(key, props);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Org-Id", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
		return ServiceHttpUtils.doHttpPost(FacilioProperties.getRegion(), Services.AGENT_SERVICE, getUrl(url), headers, null, body);
	}

	private static String getUrl(String url) throws Exception {
		String domain = getDomain();
		return RequestUtil.getProtocol(ServletActionContext.getRequest()) + "://" + domain + url;
	}
	
	private static String getDomain() throws Exception {
		// In case multiple cloud agents are there for an org, agent/site would be mapped with cloud domainId and needs to get domain based on agent/site
		GenericSelectRecordBuilder builder = DBUtil.getSelectBuilderWithJoin(ModuleFactory.getCloudAgentDomainModule(), FieldFactory.getCloudAgentDomainFields());
		Map<String, Object> prop = builder.fetchFirst();
		if (MapUtils.isEmpty(prop)) {
			throw new FacilioException("No cloud agent domain mapped for the org. Please contact admin team.");
		}
		return (String) prop.get("domain");
	}
	
}
