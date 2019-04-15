package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.endpoints.PubSubManager;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Iterator;
import java.util.Map;

public class PubSubPublishMessageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, ReadingDataMeta> currentReadingMap = (Map<String, ReadingDataMeta>) context.get(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META);
		
		if (currentReadingMap != null) {
			Iterator<String> readingKeys = currentReadingMap.keySet().iterator();
			while (readingKeys.hasNext()) {
				String readingKey = readingKeys.next();
				
				if (PubSubManager.getInstance().isReadingChangeSubscribed(AccountUtil.getCurrentOrg().getId(), readingKey)) {
					FacilioContext jobContext = new FacilioContext();
					jobContext.put(FacilioConstants.ContextNames.PUBSUB_TOPIC, "readingChange");
					jobContext.put(FacilioConstants.ContextNames.READING_KEY, readingKey);
					jobContext.put(FacilioConstants.ContextNames.CURRRENT_READING_DATA_META, currentReadingMap.get(readingKey));
					
					FacilioTimer.scheduleInstantJob("PubSubInstantJob", jobContext);
				}
			}
		}
		return false;
	}
	
}
