package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateVisitorLogTriggerCommand extends FacilioCommand {

	public AddOrUpdateVisitorLogTriggerCommand() {}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<VisitorLoggingContext> vLogs;
		vLogs = (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isEmpty(vLogs)) {
			return false;
		}
		
		for(VisitorLoggingContext vLog: vLogs) {
			if (vLog.getTrigger() != null) {
				vLog.getTrigger().setTriggerExecutionSource(TriggerExectionSource.SCHEDULE);
				vLog.getTrigger().setName(vLog.getId()+ "-Trigger");
				if(vLog.getTrigger().getId() > 0) {
					VisitorManagementAPI.updateTrigger(vLog.getTrigger());
				}
				else {
					addTriggers(vLog);
				}
			}
		}
		return false;
	}
	
	private void addTriggers(VisitorLoggingContext vLog) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getVisitorLogTriggersModule().getTableName()).fields(FieldFactory.getVisitorLogTriggerFields());

			PMTriggerContext trigger = vLog.getTrigger();
			trigger.setPmId(vLog.getId());
			trigger.setOrgId(AccountUtil.getCurrentOrg().getId());
			insertBuilder.addRecord(FieldUtil.getAsProperties(trigger));
			insertBuilder.save();
	}

	

}
