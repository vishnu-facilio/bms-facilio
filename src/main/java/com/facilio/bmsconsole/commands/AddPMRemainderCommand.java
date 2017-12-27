package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMRemainder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPMRemainderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PMRemainder> remainders = (List<PMRemainder>) context.get(FacilioConstants.ContextNames.PM_REMAINDERS);
		if(remainders != null && !remainders.isEmpty()) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			if(pm != null && pm.getId() != -1) {
				List<ActionContext> actions = new ArrayList<>();
				for(PMRemainder remainder : remainders) {
					remainder.setPmId(pm.getId());
					ActionContext action = remainder.getAction();
					if(action == null) {
						action = new ActionContext();
						action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
						
						switch(remainder.getTypeEnum()) {
							case BEFORE: action.setDefaultTemplateId(10);break;
							case AFTER: action.setDefaultTemplateId(11);break;
						}
						remainder.setAction(action);
					}
					actions.add(action);
				}
				ActionAPI.addActions(actions);
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.fields(FieldFactory.getPMRemainderFields())
																.table(ModuleFactory.getPMRemainderModule().getTableName());
				for(PMRemainder remainder : remainders) {
					remainder.setOrgId(AccountUtil.getCurrentOrg().getId());
					remainder.setActionId(remainder.getAction().getId());
					insertBuilder.addRecord(FieldUtil.getAsProperties(remainder));
				}
				insertBuilder.save();
			}
		}
		return false;
	}
}
