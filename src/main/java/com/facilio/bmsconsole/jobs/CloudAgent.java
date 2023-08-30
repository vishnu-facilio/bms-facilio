package com.facilio.bmsconsole.jobs;

import java.time.ZonedDateTime;
import java.util.*;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.FacilioException;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class CloudAgent extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(CloudAgent.class.getName());
	private AgentBean agentBean;

	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			long jobId = jc.getJobId();
			agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
			FacilioAgent agent = agentBean.getAgent(jobId);
			if (agent.getWorkflowId() != -1) {
				getPayloadsFromWorkflowAndPushToMessageQueue(agent);

			}
		} catch (Exception e) {
			CommonCommandUtil.emailAlert("Exception occurred in Cloud Job", "Agent ID: "+ jc.getJobId());
			LOGGER.error("Exception occurred in Cloud Job" , e);
		}
	}

	private void pushToMessageQueue(FacilioAgent agent, List<JSONObject> results) throws Exception {
		String topic = AccountUtil.getCurrentOrg().getDomain();
		KafkaMessageSource source = (KafkaMessageSource) AgentUtilV2.getMessageSource(agent);
		for (JSONObject payload: results) {
			AgentUtilV2.publishToQueue(topic, agent.getName(), payload, source);
		}
	}

	private void getPayloadsFromWorkflowAndPushToMessageQueue(FacilioAgent agent) throws Exception {
		long currentTime = System.currentTimeMillis();
		long toTime = adjustTimestamp(currentTime, agent.getInterval());
		long interval = agent.getInterval() * 60 * 1000;

		long fromTime = toTime - interval;
		processResult(agent, fromTime, toTime);
	}

	private void processResult(FacilioAgent agent, long fromTime, long toTime) throws Exception {
		List<JSONObject> results = runWorkflow(agent.getWorkflowId(), fromTime, toTime, agent);
		LOGGER.info("results : " + results);
		if (results == null) {
			throw new FacilioException("Fetching data from cloud failed");
		}
		else if (!results.isEmpty()) {
			pushToMessageQueue(agent, results);
		}

		updateLastReceivedTime(agent, toTime);
	}

	private void updateLastReceivedTime(FacilioAgent agent, long lastDataReceivedTime) throws InstantiationException, IllegalAccessException {
		agent.setLastDataReceivedTime(lastDataReceivedTime);
		agentBean.updateAgentLastDataReceivedTime(agent);
	}

	private List<JSONObject> runWorkflow(long workflowId, long fromTime, long toTime, FacilioAgent agent) throws Exception {
		WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(workflowId);

		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();

		FacilioContext newContext = chain.getContext();

		List<Object> props = new ArrayList<>();
		props.add(fromTime);
		props.add(toTime);
		props.add(FieldUtil.getAsProperties(agent));


		newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
		newContext.put(WorkflowV2Util.WORKFLOW_PARAMS, props);

		chain.execute();

		List<JSONObject> payloads = null;

		List<Map<String, Object>> results = (List<Map<String, Object>>) workflowContext.getReturnValue();
		if (results != null) {
			payloads = new ArrayList<>();
			for (Map<String, Object> result: results) {
				JSONObject payload = new JSONObject();
				payloads.add(payload);

				payload.put("agent", agent.getName());
                Long timeStamp = (Long) result.get("timestamp");
                if (timeStamp == null) {
                    timeStamp = toTime;
                }
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                
                PublishType type = PublishType.TIMESERIES;
				if ((boolean) result.getOrDefault("event", false)) {
					// For alarms
					type = PublishType.EVENTS;
					payload.putAll(data);
				}
				else {
					// For timeseries or cov
					if ((boolean) result.getOrDefault("cov", false)) {
						type = PublishType.COV;
					}
					payload.put("actual_timestamp", timeStamp);

					int controllerType = FacilioUtil.parseInt(result.getOrDefault("controllerType", FacilioControllerType.MISC.asInt()));
					payload.put("controllerType", controllerType);
					Map<String, Object> controller;
					if (controllerType == FacilioControllerType.MISC.asInt()) {
						controller = Collections.singletonMap("name", result.remove("controller"));
					}
					else {
						controller = (HashMap) result.get("controller");
					}
					payload.put("controller", controller);

	                if (data != null) {
	                    payload.put("data", Collections.singletonList(data));
	                }
				}
				
				payload.put("publishType", type.asInt());
                payload.put("timestamp", timeStamp);
			}
		}
		return payloads;
	}

	private long adjustTimestamp(long time, long interval) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(time);
		zdt = zdt.truncatedTo(new SecondsChronoUnit(interval * 60));
		return DateTimeUtil.getMillis(zdt, true);
	}
}

