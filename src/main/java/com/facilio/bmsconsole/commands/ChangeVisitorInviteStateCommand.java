package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

public class ChangeVisitorInviteStateCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>)context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(CollectionUtils.isNotEmpty(recordIds) && changeSet != null && !changeSet.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(!changeSet.isEmpty()) {
				for(Long recordId : recordIds) {
					List<UpdateChangeSet> updatedSet = changeSet.get(recordId);
					if(updatedSet != null && !updatedSet.isEmpty()) {
						for(UpdateChangeSet changes : updatedSet) {
							FacilioField field = modBean.getField(changes.getFieldId()) ;
							if(field != null) {
								if(field.getName().equals("moduleState")) {
									VisitorLoggingContext log = VisitorManagementAPI.getVisitorLogging(recordId, false);
									FacilioStatus status = StateFlowRulesAPI.getStateContext((long)changes.getNewValue());
									if(status.getStatus().equals("CheckedIn")) {
										VisitorManagementAPI.updateVisitorInviteStateToArrived(log.getVisitor().getId(), log.getInvite().getId());
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
