package com.facilio.agentv2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.agentv2.controller.Controller;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants.Services;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;
import com.facilio.util.ServiceHttpUtils;

public class CloudAgentUtil {
	
	private static class Urls {
		private static final String ADD_AGENT = "/api/v1/agent/add";
		private static final String EDIT_AGENT = "/api/v1/agent/edit";
		private static final String FETCH_AGENT_DETAILS = "/api/v1/agent/fetch";
		private static final String FETCH_MESSAGE_SOURCES = "/api/v1/agent/fetchSources";
		private static final String ADD_MESSAGE_SOURCE = "/api/v1/agent/addSource";
		private static final String RUN_WORKFLOW = "/api/v1/agent/runWorkflow";
		private static final String TOGGLE_JOB = "/api/v1/agent/toggleJob";
		private static final String ADD_CONTROLLER ="/api/v1/controller/add";
	}
	
	public static void addCloudServiceAgent(FacilioAgent agent) throws Exception {
		Map<String, Object> props = FieldUtil.getAsProperties(agent);
		doPost(Urls.ADD_AGENT, "agent", props);
		
	}
	public static void addController(Controller controller,int controllerType,String agentName)throws Exception{
		JSONObject params = new JSONObject();
		Map<String, Object> props = FieldUtil.getAsProperties(controller);
		params.put("controller",props);
		params.put("controllerType",controllerType);
		params.put("agentName",agentName);
		doPost(Urls.ADD_CONTROLLER,params);
	}
	public static void editServiceAgent(FacilioAgent agent) throws Exception{
		Map<String, Object> props = FieldUtil.getAsProperties(agent);
		doPost(Urls.EDIT_AGENT, "agent",props);
	}

	public static Map<String, Object> fetchAgentDetails(FacilioAgent agent) throws Exception{
		Map<String, Object> data = doGet(Urls.FETCH_AGENT_DETAILS, Collections.singletonMap("name", agent.getName()));
		if (data != null) {
			return (Map<String, Object>) data.get(AgentConstants.AGENT_PARAMS);
		}
		return null;
	}
	public static List fetchMessageSources() throws Exception{
		Map<String, Object> data = doGet(Urls.FETCH_MESSAGE_SOURCES,null);
		if (data != null) {
			return (List) data.get(AgentConstants.MESSAGE_SOURCES);
		}
		return null;
	}
	public static long addMessageSource(Map<String,Object> messageSource) throws Exception{
		Map<String, Object> data = doPost(Urls.ADD_MESSAGE_SOURCE, "messageSource", messageSource);
		long id = -1;
		if(data != null){
			if(data.containsKey(AgentConstants.ID)){
				id = (long) data.get("id");
			}
		}
		return id;
	}

	public static Map<String,Object> runWorkflow(WorkflowContext workflow) throws Exception{
		Map<String,Object> props = FieldUtil.getAsProperties(workflow);
		Map<String, Object> data = doPost(Urls.RUN_WORKFLOW,AgentConstants.WORKFLOW,props);
		Map<String,Object> resultMap = new HashMap<>();
		if (data != null) {
			if(data.containsKey(AgentConstants.WORKFLOW_RESPONSE)){
				resultMap.put(AgentConstants.WORKFLOW_RESPONSE,data.get(AgentConstants.WORKFLOW_RESPONSE));
				return resultMap;
			}
			else if(data.containsKey(AgentConstants.WORKFLOW_SYNTAX_ERROR)){
				resultMap.put(AgentConstants.WORKFLOW_SYNTAX_ERROR,data.get(AgentConstants.WORKFLOW_SYNTAX_ERROR));
				return resultMap;
			}
		}
		return null;
	}

	public static void toggleJob(List<String>agents,boolean isActiveUpdateValue) throws Exception{
		Map<String,Object> props = new HashMap<>();
		props.put("agents",agents);
		props.put(AgentConstants.IS_ACTIVE_UPDATE_VALUE,isActiveUpdateValue);
		doPost(Urls.TOGGLE_JOB,AgentConstants.JOB_PROPS,props);
	}
	
	
	/********************* HTTP Util *****************************/
	
	private static Map<String, Object> validateAndGetData(String response) throws Exception {
		if (StringUtils.isNotEmpty(response)) {
			JSONObject obj = FacilioUtil.parseJson(response);
			if (obj.containsKey("code")) {
				int code = FacilioUtil.parseInt(obj.get("code").toString());
				if (code != 0) {
					throw new FacilioException(obj.get("message").toString());
				}
			}
			JSONObject data = (JSONObject) obj.get("data");
			if (data != null) {
				return FacilioUtil.getAsMap(data);
			}
			return null;
		}
		else {
			throw new FacilioException("Error occurred. Please check the details again");
		}
	}
	
	private static Map<String, Object> doPost(String url, String key, Object props) throws Exception {
		JSONObject body = new JSONObject();
		body.put(key, props);
		String response = ServiceHttpUtils.doHttpPost(FacilioProperties.getRegion(), Services.AGENT_SERVICE, getUrl(url), getHeaders(), null, body);
		return validateAndGetData(response);
	}
	private static Map<String, Object> doPost(String url, JSONObject body) throws Exception {
		String response = ServiceHttpUtils.doHttpPost(FacilioProperties.getRegion(), Services.AGENT_SERVICE, getUrl(url), getHeaders(), null, body);
		return validateAndGetData(response);
	}
	
	private static Map<String, Object> doGet(String url, Map<String, String> params) throws Exception {
		String response = ServiceHttpUtils.doHttpGet(FacilioProperties.getRegion(), Services.AGENT_SERVICE, getUrl(url), getHeaders(), params);
		return validateAndGetData(response);
	}
	
	private static Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Org-Id", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
		return headers;
	}

	private static String getUrl(String url) throws Exception {
		String domain = getDomain();
		if (!domain.contains("://")) {
			return RequestUtil.getProtocol(ServletActionContext.getRequest()) + "://" + domain + url;
		}
		return domain + url;
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
