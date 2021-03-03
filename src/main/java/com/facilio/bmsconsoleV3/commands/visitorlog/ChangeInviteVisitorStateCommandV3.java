package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
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
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
		Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
		List<InviteVisitorContextV3> oldInvites = (List<InviteVisitorContextV3>) context.get(FacilioConstants.ContextNames.OLD_INVITES);
		Map<Long, InviteVisitorContextV3> oldInvitesMap = new HashMap<Long, InviteVisitorContextV3>();
		for(InviteVisitorContextV3 oldRecord:oldInvites) {
			oldInvitesMap.put(oldRecord.getId(), oldRecord);
		}
		List<InviteVisitorContextV3> oldRecords = (List<InviteVisitorContextV3>) context.get(FacilioConstants.ContextNames.INVITE_VISITOR_RECORDS);
		
		boolean isInviteEdit = false;
		if(queryParams.containsKey("isInviteEdit") && queryParams.get("isInviteEdit") != null && !queryParams.get("isInviteEdit").isEmpty() && queryParams.get("isInviteEdit").get(0) != null && StringUtils.isNotEmpty((String)queryParams.get("isInviteEdit").get(0)) && ((Boolean)Boolean.valueOf((String)queryParams.get("isInviteEdit").get(0)))){
			isInviteEdit = true;
		}
		
		if (MapUtils.isNotEmpty(changeSet)) {
			if (CollectionUtils.isNotEmpty(records)) {
				long time = System.currentTimeMillis();
				for (InviteVisitorContextV3 record : records) {		
					InviteVisitorContextV3 oldInvite = oldInvitesMap.get(record.getId());
					if (oldInvite.hasCheckedIn() && !isInviteEdit) {
						VisitorLogContextV3 vLogToBeAdded = FieldUtil.cloneBean(record, VisitorLogContextV3.class);
						VisitorLogContextV3 oldVLogToBeAdded = FieldUtil.cloneBean(oldRecords.get(0),
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
				}
			}
		}
		return false;
	}
}
