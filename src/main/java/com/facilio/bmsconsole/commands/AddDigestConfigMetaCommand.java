package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddDigestConfigMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ScheduledActionContext schAction = (ScheduledActionContext)context.get(FacilioConstants.ContextNames.SCHEDULED_ACTION);
		DigestConfigContext config = (DigestConfigContext)context.get(FacilioConstants.ContextNames.CONFIG);
		if(schAction != null) {
			FacilioModule module = ModuleFactory.getDigestConfigModule();
			if(config.getId() > 0) {
				Long oldSchActionId = config.getScheduledActionId();
				config.setScheduledActionId(schAction.getId());
				Map<String, Object> prop = FieldUtil.getAsProperties(config);
				DigestConfigAPI.updateConfig(prop, Collections.singletonList(config.getId()));
				ScheduledActionAPI.deleteScheduledAction(oldSchActionId,FacilioConstants.Job.DIGEST_JOB_NAME);
			}
			else {
				config.setIsActive(true);
				config.setScheduledActionId(schAction.getId());
				Map<String, Object> prop = FieldUtil.getAsProperties(config);
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(module.getTableName())
						.fields(FieldFactory.getDigestConfigFields())
						.addRecord(prop);
				insertBuilder.save();
				
				config.setId((long) prop.get("id"));
			}
			context.put(FacilioConstants.ContextNames.CONFIG,config.getId());
		}
		return false;
	}

}
