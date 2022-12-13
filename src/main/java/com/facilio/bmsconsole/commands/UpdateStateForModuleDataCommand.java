package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
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
		boolean isfromV2 = context.containsKey(FacilioConstants.ContextNames.IS_FROM_V2) ? (boolean) context.get(FacilioConstants.ContextNames.IS_FROM_V2) : false;
		ModuleBean moduleBean = Constants.getModBean();
		List<? extends ModuleBaseWithCustomFields> records = null;
		if (MapUtils.isNotEmpty(recordMap)) {
			records = recordMap.get(moduleName);
		}
		// there is no transition info
		if (currentTransitionId == null || currentTransitionId == -1) {
			return false;
		}

		if (CollectionUtils.isNotEmpty(records)) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
			if (stateflowTransition == null) {
				return false;
			}
			for (ModuleBaseWithCustomFields record : records) {
				if (record.getApprovalFlowId() > -1 && record.getApprovalFlowId() > 0) {
					throw new IllegalArgumentException("Cannot change state as it is in approval");
				}

				record.setSubForm(null); // temp fix
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
				/*if (wo.getModuleState().getId() != stateflowTransition.getFromStateId()) {
					throw new IllegalArgumentException("Invalid transition");
				}*/
				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransition, moduleName, record, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, record.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					FacilioStatus newState = StateFlowRulesAPI.getStateContext(stateflowTransition.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					stateflowTransition.executeTrueActions(record, context, recordPlaceHolders);
				}

				if(stateflowTransition.getQrFieldId() > 0){
					String qrValue = null;
					FacilioField qrField = moduleBean.getField(stateflowTransition.getQrFieldId());

					Object value = FieldUtil.getValue(record, qrField);
					if (value != null) {
						if (qrField instanceof LookupField) {
							FacilioField qrLookupField = moduleBean.getField(stateflowTransition.getQrLookupFieldId());
							if (value instanceof ModuleBaseWithCustomFields) {
								if (qrLookupField != null) {
									Map<String,Object> qrLookupFieldValue;
									if ( isfromV2 && ((ModuleBaseWithCustomFields) value).getId() > 0){
										FacilioModule qrModule = moduleBean.getModule(qrLookupField.getModuleId());
										qrLookupFieldValue =  FieldUtil.getAsProperties(RecordAPI.getRecord(qrModule.getName(),((ModuleBaseWithCustomFields) value).getId()));
										if (qrLookupFieldValue != null){
											qrValue = (String) qrLookupFieldValue.get(qrLookupField.getName());
										}
									}else {
										qrValue = (String) FieldUtil.getValue((ModuleBaseWithCustomFields) value, qrLookupField);
									}
								}
							} else if (value instanceof Map) {
								qrValue = (String) ((Map) value).get(qrLookupField.getName());
							}
						} else {
							qrValue = (String) value;
						}
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
