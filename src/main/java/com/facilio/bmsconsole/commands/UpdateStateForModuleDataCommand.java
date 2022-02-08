package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
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
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UpdateStateForModuleDataCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(UpdateStateForModuleDataCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String qrCode = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		ModuleBean moduleBean = Constants.getModBean();
		List<? extends ModuleBaseWithCustomFields> wos = null;
		if (MapUtils.isNotEmpty(recordMap)) {
			wos = recordMap.get(moduleName);
		}
		// there is no transition info
		if (currentTransitionId == null || currentTransitionId == -1) {
			return false;
		}

		if (CollectionUtils.isNotEmpty(wos)) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
			if (stateflowTransition == null) {
				return false;
			}
			for (ModuleBaseWithCustomFields wo : wos) {
				if (wo.getApprovalFlowId() > -1 && wo.getApprovalFlowId() > 0) {
					throw new IllegalArgumentException("Cannot change state as it is in approval");
				}

				wo.setSubForm(null); // temp fix
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, wo, WorkflowRuleAPI.getOrgPlaceHolders());
				/*if (wo.getModuleState().getId() != stateflowTransition.getFromStateId()) {
					throw new IllegalArgumentException("Invalid transition");
				}*/
				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransition, moduleName, wo, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, wo.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					FacilioStatus newState = StateFlowRulesAPI.getStateContext(stateflowTransition.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					stateflowTransition.executeTrueActions(wo, context, recordPlaceHolders);
				}

				if(stateflowTransition.getQrFieldId() > 0){
					String qrValue = null;
					FacilioField qrField = moduleBean.getField(stateflowTransition.getQrFieldId());
					if(!(stateflowTransition.getModule().getExtendedModuleIds().contains(qrField.getModuleId()))) {
						Map<String,Object> lookupFieldValue = null;
						List<FacilioField> fields = moduleBean.getAllFields(moduleName);
						for(FacilioField field:fields){
							if(field.getDataTypeEnum() == FieldType.LOOKUP){
								if(((LookupField) field).getLookupModule().getExtendedModuleIds().contains(qrField.getModuleId())) {
									lookupFieldValue = FieldUtil.getAsProperties(FieldUtil.getValue(wo, field));
									break;
								}
							}
						}

						if(lookupFieldValue!=null && lookupFieldValue.containsKey(qrField.getName())){
							qrValue = (String) lookupFieldValue.get(qrField.getName());
						}
					}else{
						qrValue = qrField != null ? (String) FieldUtil.getValue(wo, qrField) : null;
					}

					if((qrValue!=null) && (qrCode==null)){
						throw new IllegalArgumentException("Qr is mandatory");
					}
					if ((qrValue!=null) && (!qrValue.equals(qrCode))) {
						throw new IllegalArgumentException("QR code doesn't match to this record");
					}
				}
			}
		}
		return false;
	}

}
