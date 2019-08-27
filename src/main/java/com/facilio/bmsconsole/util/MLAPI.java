package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;

public class MLAPI {
	public static MLContext getSubMeterDetails (long mlAnomalyAlarmId) throws Exception {
		
		
		MLUtil.getMLContext(mlAnomalyAlarmId);
		return null;
		
	}
}
