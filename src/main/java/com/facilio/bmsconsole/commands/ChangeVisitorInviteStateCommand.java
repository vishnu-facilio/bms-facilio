package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.google.common.base.Objects;

public class ChangeVisitorInviteStateCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		List<VisitorLoggingContext> records = (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records) && changeSet != null && !changeSet.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(!changeSet.isEmpty()) {
				long time = System.currentTimeMillis();
				for(VisitorLoggingContext record : records) {
					List<UpdateChangeSet> updatedSet = changeSet.get(record.getId());
					if(updatedSet != null && !updatedSet.isEmpty()) {
						for(UpdateChangeSet changes : updatedSet) {
							FacilioField field = modBean.getField(changes.getFieldId()) ;
							if(field != null) {
								if(field.getName().equals("moduleState")) {
									VisitorLoggingContext log = VisitorManagementAPI.getVisitorLogging(record.getId(), false);
									FacilioStatus status = StateFlowRulesAPI.getStateContext((long)changes.getNewValue());
									if(status.getStatus().toString().trim().equals("CheckedIn")) {
										VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record.getId(), true, time);
										if(log.getInvite() != null) {
											VisitorManagementAPI.updateVisitorInviteStateToArrived(log.getVisitor().getId(), log.getInvite().getId());
										}
									}
									else if(status.getStatus().toString().trim().equals("CheckedOut")) {
										VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record.getId(), false, time);
										VisitorManagementAPI.updateVisitorRollUps(log);
									}
								}
							}
						}
					}
				}
			}
		}
			
		return false;
	}

}
