package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import java.util.Map;

public class StateflowTransitionContext extends AbstractStateTransitionRuleContext {

	private static final long serialVersionUID = 1L;
	public long qrFieldId = -1;
	public long getQrFieldId() {
		return qrFieldId;
	}
	public void setQrFieldId(long qrFieldId) {
		this.qrFieldId = qrFieldId;
	}

	public FacilioField qrField;
	public void setQrField(FacilioField qrField) {
		this.qrField = qrField;
	}
	public FacilioField getQrField() {
		return qrField;
	}

	public Long qrLookupFieldId;

	public void setQrLookupFieldId(Long qrLookupFieldId) {
		this.qrLookupFieldId = qrLookupFieldId;
	}

	public Long getQrLookupFieldId() {
		return qrLookupFieldId;
	}
	@Getter @Setter
	public long locationFieldId = -1;
	@Getter @Setter
	public FacilioField locationField;
	@Getter @Setter
	public Long radius;

	@Getter@Setter
	public long locationLookupFieldId = -1;

	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
								FacilioContext context) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		return evaluateStateFlow(moduleRecord.getStateFlowId(), moduleRecord.getModuleState(), moduleName, record, placeHolders, context);
	}

	@Override
	public String getSuggestedFormName() {
		return "__state_transition_" + getId();
	}

	@Override
	protected DelegationType getDelegationType() {
		return DelegationType.STATE_FLOW;
	}

	@Override
	protected void executeTrue(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(getModuleId());
		StateFlowRulesAPI.updateState(moduleRecord, module, StateFlowRulesAPI.getStateContext(getToStateId()), false, context);
	}

	@JsonIgnore
	@JSON(serialize = false)
	public String getAuditLogDescription(boolean add) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("State Transition '%s' has been %s with", getName(), (add ? "added" : "edited")));
		return builder.toString();
	}
}
