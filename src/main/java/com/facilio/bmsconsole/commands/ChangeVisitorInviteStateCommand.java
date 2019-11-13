package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

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
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSet = (Map<String, Map<Long, List<UpdateChangeSet>>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		List<VisitorLoggingContext> records = (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records) && changeSet != null && !changeSet.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(MapUtils.isNotEmpty(changeSet)) {
				Map<Long, List<UpdateChangeSet>> moduleChangeSet = changeSet.get(FacilioConstants.ContextNames.VISITOR_LOGGING);
				if(MapUtils.isNotEmpty(moduleChangeSet)) {
					long time = System.currentTimeMillis();
					for(VisitorLoggingContext record : records) {
						List<UpdateChangeSet> updatedSet = moduleChangeSet.get(record.getId());
						if(updatedSet != null && !updatedSet.isEmpty()) {
							for(UpdateChangeSet changes : updatedSet) {
								FacilioField field = modBean.getField(changes.getFieldId()) ;
								if(field != null) {
									if(field.getName().equals("moduleState")) {
										FacilioStatus status = StateFlowRulesAPI.getStateContext((long)changes.getNewValue());
										if(status.getStatus().toString().trim().equals("CheckedIn")) {
											VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record, true, time);
											VisitorManagementAPI.updateVisitorRollUps(record);
											if(record.getInvite() != null) {
												VisitorManagementAPI.updateVisitorInviteStateToArrived(record.getVisitor().getId(), record.getInvite().getId(), "Arrived");
											}
										}
										else if(status.getStatus().toString().trim().equals("CheckedOut")) {
											VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record, false, time);
											VisitorManagementAPI.updateVisitorLastVisitRollUps(record);
											if(record.getInvite() != null && !record.getInvite().isRecurring()) {
												VisitorManagementAPI.updateVisitorInviteStateToArrived(record.getVisitor().getId(), record.getInvite().getId(), "CheckedOut");
											}
										}
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
