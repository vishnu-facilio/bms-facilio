/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.ReadingToolsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.tasker.FacilioTimer;

/**
 * @author facilio
 *
 */
public class ReadingToolsDuplicateRemoveCommand extends FacilioCommand {

	/* (non-Javadoc)
	 * @see org.apache.commons.chain.Command#execute(org.apache.commons.chain.Context)
	 */

	private static final Logger LOGGER = LogManager.getLogger(ReadingToolsDuplicateRemoveCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			long orgId = (long) context.get(ContextNames.ADMIN_DELTA_ORG);
			long fieldId = (long) context.get(ContextNames.FIELD_ID);
			long assetId = (long) context.get(ContextNames.ASSET_ID);
			long startTtime = (long) context.get(ContextNames.START_TTIME);
			long endTtime = (long) context.get(ContextNames.END_TTIME);
			String email = (String)context.get(ContextNames.ADMIN_USER_EMAIL);
			long fieldOptionType = (long) context.get(ContextNames.FIELD_OPTION_TYPE);
			long readingToolsjobId = -1;

			Map<String,Object> prop = new HashMap<>();

			prop.put("orgId", orgId);
			prop.put("email", email);
			prop.put("fieldId", fieldId);
			prop.put("assetId", assetId);
			prop.put("startTtime", startTtime);
			prop.put("endTtime", endTtime);
			prop.put("fieldOptionType", fieldOptionType);
			Map<String,Object> props = ReadingToolsAPI.getReadinToolData(fieldId, assetId,fieldOptionType);

			if(props!=null) {
				readingToolsjobId = (long) props.get("id");
				ReadingToolsAPI.updateReadingToolsJob ( readingToolsjobId,  startTtime,  endTtime,  email);
				FacilioTimer.deleteJob(readingToolsjobId, FacilioConstants.ContextNames.ADMIN_DUPLICATES_REMOVE);

			}
			else {
				readingToolsjobId = ReadingToolsAPI.insertReadingTools(prop);

			}

			FacilioTimer.scheduleOneTimeJobWithDelay(readingToolsjobId, FacilioConstants.ContextNames.ADMIN_DUPLICATES_REMOVE, 30,"priority");

		}
		catch(Exception e) {
			LOGGER.info("#####ReadingToolsDuplicateRemove Exception Occurred"+e);
		}
		
		
		
		
		return false;
	}

}
