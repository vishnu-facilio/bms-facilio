package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.chain.Context;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

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
					WmsBroadcaster.getBroadcaster().sendMessage(msg);
				} catch (Exception e) {
					LOGGER.log(Level.WARN, "Exception while send wms message for live reading update. readingKey: "+readingKey, e);
				} catch (Error err) {
					LOGGER.log(Level.WARN, "NoClassDefFoundError while send wms message for live reading update. readingKey: " + readingKey, err);
				}
			}
			LOGGER.debug(MessageFormat.format("Total number of data points checked : {0}. Total time taken to check : {1}. Total time taken to add instant job : {2}", i, totalChangeCheckTime, totalInstantJobAddTime));
		}
		return false;
	}
	
}
