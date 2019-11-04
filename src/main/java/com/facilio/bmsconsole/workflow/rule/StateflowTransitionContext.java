package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class StateflowTransitionContext extends ApproverWorkflowRuleContext implements FormRuleInterface {

	private static final long serialVersionUID = 1L;
	
	private long fromStateId = -1;
	public long getFromStateId() {
		return fromStateId;
	}
	public void setFromStateId(long fromStateId) {
		this.fromStateId = fromStateId;
	}
	
	private long toStateId = -1;
	public long getToStateId() {
		return toStateId;
	}
	public void setToStateId(long toStateId) {
		this.toStateId = toStateId;
	}
	
	private long stateFlowId = -1;
	public long getStateFlowId() {
		return stateFlowId;
	}
	public void setStateFlowId(long stateFlowId) {
		this.stateFlowId = stateFlowId;
	}
	
	private FacilioForm form;
	public FacilioForm getForm() {
		return form;
	}
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	private long formId = -1; // check whether it is good to have
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private int buttonType = -1;
	public int getButtonType() {
		return buttonType;
	}
	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}
	
	private TransitionType type;
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public TransitionType getTypeEnum() {
		return type;
	}
	public void setType(TransitionType type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = TransitionType.valueOf(type);
	}

	private int scheduleTime = -1;
	public int getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(int scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		boolean result;
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		
		Boolean shouldCheckOnlyConditioned = (Boolean) context.get(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK);
		if (shouldCheckOnlyConditioned == null) {
			shouldCheckOnlyConditioned = false;
		}
		
		if (shouldCheckOnlyConditioned && type != TransitionType.CONDITIONED) {
			return false;
		}
		
		// this is old records
		if (moduleRecord.getModuleState() == null || moduleRecord.getStateFlowId() <= 0) {
			return false;
		}

		if (moduleRecord.getModuleState() != null && moduleRecord.getStateFlowId() > 0) {
			// don't execute if it different stateflow
			if (moduleRecord.getStateFlowId() != getStateFlowId()) {
				return false;
			}
			
			// don't execute if fromStateId is different from record module state
			if (getFromStateId() != moduleRecord.getModuleState().getId()) {
				return false;
			}
		}
		
		result = super.evaluateMisc(moduleName, record, placeHolders, context);
		return result;
	}
	
	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		boolean shouldExecuteTrueActions = true;
		shouldExecuteTrueActions = super.validateApproversForTrueAction(record);
		
		if (shouldExecuteTrueActions) {
			boolean isValid = super.validationCheck(moduleRecord);
			
			if (isValid) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(getModuleId());
				StateFlowRulesAPI.updateState(moduleRecord, module, StateFlowRulesAPI.getStateContext(getToStateId()), false, context);
				
				super.executeTrueActions(record, context, placeHolders);
			}
		}
	}
	
	public enum TransitionType {
		NORMAL,
		SCHEDULED,
		CONDITIONED,
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static TransitionType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
