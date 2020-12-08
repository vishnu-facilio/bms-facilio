package com.facilio.bmsconsole.commands;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.endpoints.PubSubManager;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class PubSubPublishMessageCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(PubSubPublishMessageCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, ReadingDataMeta> currentReadingMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META);

		if (currentReadingMap != null) {
			int i=0;
			Iterator<String> readingKeys = currentReadingMap.keySet().iterator();
			long totalChangeCheckTime = 0;
			long totalInstantJobAddTime = 0;
			while (readingKeys.hasNext()) {
				String readingKey = readingKeys.next();
				
				if (AccountUtil.getCurrentOrg().getId() == 146 || AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 343 || AccountUtil.getCurrentOrg().getId() == 324) {
					try {
						ReadingDataMeta rdm = currentReadingMap.get(readingKey);

						JSONObject content = new JSONObject();
						content.put("resourceId", rdm.getResourceId());
						content.put("fieldId", rdm.getFieldId());
						content.put("value", rdm.getValue());

						Message msg = new Message();
						msg.setOrgId(AccountUtil.getCurrentOrg().getId());
						msg.setTopic("__livereading__/" + rdm.getResourceId() + "/" + rdm.getFieldId());
						msg.setContent(content);
						SessionManager.getInstance().sendMessage(msg);
					} catch (Exception e) {
						LOGGER.log(Level.WARN, "Exception while send wms message for live reading update. readingKey: "+readingKey, e);
					}
				}
				/*else {
					long startTime = System.currentTimeMillis();
					boolean readingChangeSubscribed = PubSubManager.getInstance().isReadingChangeSubscribed(AccountUtil.getCurrentOrg().getId(), readingKey);
					long timeTaken = System.currentTimeMillis() - startTime;
					LOGGER.debug(MessageFormat.format("Time taken to check {0} is {1}", readingKey, timeTaken));
					totalChangeCheckTime += timeTaken;
					if (readingChangeSubscribed) {
						FacilioContext jobContext = new FacilioContext();
						jobContext.put(FacilioConstants.ContextNames.PUBSUB_TOPIC, "readingChange");
						jobContext.put(FacilioConstants.ContextNames.READING_KEY, readingKey);
						jobContext.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, currentReadingMap.get(readingKey));
	
						startTime = System.currentTimeMillis();
						FacilioTimer.scheduleInstantJob("PubSubInstantJob", jobContext);
						timeTaken = System.currentTimeMillis() - startTime;
						LOGGER.debug(MessageFormat.format("Time taken to add instant job for {0} is {1}", readingKey, timeTaken));
						totalInstantJobAddTime += timeTaken;
					}
					i++;
				}*/
			}
			LOGGER.debug(MessageFormat.format("Total number of data points checked : {0}. Total time taken to check : {1}. Total time taken to add instant job : {2}", i, totalChangeCheckTime, totalInstantJobAddTime));
		}
		return false;
	}
	
}
