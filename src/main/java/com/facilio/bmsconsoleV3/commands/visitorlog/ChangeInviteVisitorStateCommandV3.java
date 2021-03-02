package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

public class ChangeInviteVisitorStateCommandV3 extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<InviteVisitorContextV3> records = recordMap.get(moduleName);
		Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);

		List<InviteVisitorContextV3> oldRecords = (List<InviteVisitorContextV3>) context.get(FacilioConstants.ContextNames.INVITE_VISITOR_RECORDS);
		Map<Long, InviteVisitorContextV3> oldRecordMap = new HashMap<Long, InviteVisitorContextV3>();
		for(InviteVisitorContextV3 oldRecord:oldRecords) {
			oldRecordMap.put(oldRecord.getId(), oldRecord);
		}

		if (MapUtils.isNotEmpty(changeSet)) {
			if (CollectionUtils.isNotEmpty(records)) {
				long time = System.currentTimeMillis();
				for (InviteVisitorContextV3 record : records) {
//              	List<UpdateChangeSet> updatedSet = changeSet.get(record.getId());
//                    if(updatedSet != null && !updatedSet.isEmpty()) {
//                     for(UpdateChangeSet changes : updatedSet) {
//                        FacilioField field = modBean.getField(changes.getFieldId());
//                        if (field != null) {
//                         	if (changes.getNewValue() != null) {
//                          FacilioStatus status = StateFlowRulesAPI.getStateContext((Long)changes.getNewValue());
//                          if (status.getStatus().toString().trim().equals("Invited") || status.getStatus().toString().trim().equals("Upcoming")) {
//                             V3VisitorManagementAPI.updateInviteVisitorInvitationStatus(record, true);
//                          }
					
					InviteVisitorContextV3 oldRecord = oldRecordMap.get(record.getId());
					if (oldRecord.hasCheckedIn()) {
						VisitorLogContextV3 vLogToBeAdded = FieldUtil.cloneBean(record, VisitorLogContextV3.class);
						VisitorLogContextV3 oldVLogToBeAdded = FieldUtil.cloneBean(oldRecord,
								VisitorLogContextV3.class);

						if (vLogToBeAdded.getCheckInTime() == null || vLogToBeAdded.getCheckInTime() <= 0) {
							V3VisitorManagementAPI.updateVisitorLogCheckInCheckoutTimeFromInviteVisitor(vLogToBeAdded,
									true, time, record);
							V3VisitorManagementAPI.updateVisitorRollUps(vLogToBeAdded, oldVLogToBeAdded);
							V3VisitorManagementAPI.updateVisitorLastVisitRollUps(vLogToBeAdded);
						}

						vLogToBeAdded.setModuleState(null);
						vLogToBeAdded.setStateFlowId(-1);
						vLogToBeAdded.setChildVisitType(BaseVisitContextV3.ChildVisitType.VISIT);
						vLogToBeAdded.setInvite(record);

						FacilioChain addVisitorLogChain = ChainUtil
								.getCreateRecordChain(FacilioConstants.ContextNames.VISITOR_LOG);
						FacilioContext addVisitorLogChainContext = addVisitorLogChain.getContext();
						Constants.setModuleName(addVisitorLogChainContext, FacilioConstants.ContextNames.VISITOR_LOG);
						Constants.setRawInput(addVisitorLogChainContext, FieldUtil.getAsJSON(vLogToBeAdded));
						addVisitorLogChainContext.put(Constants.BEAN_CLASS, VisitorLogContextV3.class);
						addVisitorLogChainContext.put(FacilioConstants.ContextNames.EVENT_TYPE,
								com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
						addVisitorLogChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE,
								FieldPermissionContext.PermissionType.READ_WRITE);
						addVisitorLogChain.execute();

					}
//                                    }
//                                }
//                            }
//                        }
				}
			}
		}
		return false;
	}
}
