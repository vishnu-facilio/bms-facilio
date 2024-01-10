package com.facilio.common.reading.add;

import com.facilio.bmsconsole.context.MarkedReadingContext;
import com.facilio.bmsconsole.util.MarkingUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;

@Log4j
@Deprecated
public class AddMarkedReadingValuesCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {

		List<MarkedReadingContext> markedList = (List<MarkedReadingContext>) context.get(FacilioConstants.ContextNames.MARKED_READINGS);

		if (markedList == null || markedList.isEmpty()) {
			return false;
		}
		MarkingUtil.addMarkedreadings(markedList);
		LOGGER.info("AddMarkedReadingValuesCommand is used"); //TODO: added for finding usage of this command. should be delete this class if nowhere used.
		return false;
	}


}
