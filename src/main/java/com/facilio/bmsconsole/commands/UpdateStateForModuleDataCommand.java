package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.vividsolutions.jts.geom.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UpdateStateForModuleDataCommand extends FacilioCommand {
	private static final double meterConstant = 1d/1084/100;

	private static final Logger LOGGER = LogManager.getLogger(UpdateStateForModuleDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<? extends ModuleBaseWithCustomFields> records = null;
		if (MapUtils.isNotEmpty(recordMap)) {
			records = recordMap.get(moduleName);
		}
		// there is no transition info
		if (currentTransitionId == null || currentTransitionId == -1) {
			return false;
		}

		if (CollectionUtils.isNotEmpty(records)) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.STATE_FLOW_TRANSITION);

			if (stateflowTransition == null) {
				return false;
			}
			for (ModuleBaseWithCustomFields record : records) {
				if (!(stateflowTransition.getTypeEnum() == AbstractStateTransitionRuleContext.TransitionType.SCHEDULED) && record.getApprovalFlowId() > -1 && record.getApprovalFlowId() > 0) {
					throw new IllegalArgumentException("Cannot change state as it is in approval");
				}

				record.setSubForm(null); // temp fix
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
				/*if (wo.getModuleState().getId() != stateflowTransition.getFromStateId()) {
					throw new IllegalArgumentException("Invalid transition");
				}*/

				stateflowTransition.executeTrueActions(record, context, recordPlaceHolders);
			}
		}
		return false;
	}

}
