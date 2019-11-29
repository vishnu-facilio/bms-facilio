package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.UpdateStateCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class StateFlowRuleContext extends WorkflowRuleContext {
	private static final long serialVersionUID = 1L;
	
	private long defaultStateId;
	public long getDefaultStateId() {
		return defaultStateId;
	}
	public void setDefaultStateId(long defaultStateId) {
		this.defaultStateId = defaultStateId;
	}
	
	private Boolean defaltStateFlow;
	public Boolean getDefaltStateFlow() {
		return defaltStateFlow;
	}
	public void setDefaltStateFlow(Boolean defaltStateFlow) {
		this.defaltStateFlow = defaltStateFlow;
	}
	public Boolean isDefaltStateFlow() {
		if (defaltStateFlow == null) {
			return false;
		}
		return defaltStateFlow;
	}

	private String diagramJson;
	public String getDiagramJson() {
		return diagramJson;
	}
	public void setDiagramJson(String diagramJson) {
		this.diagramJson = diagramJson;
	}

	private long draftParentId = -1;
	public long getDraftParentId() {
		return draftParentId;
	}
	public void setDraftParentId(long draftParentId) {
		this.draftParentId = draftParentId;
	}

	private Boolean draft;
	public Boolean getDraft() {
		return draft;
	}
	public void setDraft(Boolean draft) {
		this.draft = draft;
	}
	public Boolean isDraft() {
		if (draft != null) {
			return draft;
		}
		return false;
	}

	private long publishedDate = -1;
	public long getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(long publishedDate) {
		this.publishedDate = publishedDate;
	}

	private boolean formLevel = false;
	public boolean isFormLevel() {
		return formLevel;
	}
	public void setFormLevel(boolean formLevel) {
		this.formLevel = formLevel;
	}

	private boolean matchedFormLevel;

	@Override
	public boolean evaluateCriteria(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		if (matchedFormLevel) {
			matchedFormLevel = false;
			return true;
		}
		return super.evaluateCriteria(moduleName, record, placeHolders, context);
	}

	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
		// Don't evaluate draft stateflow in the workflow evaluation.
		if (isDraft()) {
			return false;
		}

		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		if (isFormLevel()) {
			if (moduleRecord.getFormId() > 0) {
				Criteria criteria = new Criteria();
				criteria.addAndCondition(CriteriaAPI.getIdCondition(moduleRecord.getFormId(), ModuleFactory.getFormModule()));
				List<FacilioForm> dbFormList = FormsAPI.getDBFormList(moduleName, (FacilioForm.FormType) null, criteria, null, false);
				if (CollectionUtils.isNotEmpty(dbFormList)) {
					FacilioForm facilioForm = dbFormList.get(0);
					long stateFlowId = facilioForm.getStateFlowId();

					if (stateFlowId == getId()) {
						matchedFormLevel = true;
					}

					return stateFlowId == getId();
				}
			}
			return false;
		}

		return super.evaluateMisc(moduleName, record, placeHolders, context);
	}

	@Override
	public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		if (!(record instanceof ModuleBaseWithCustomFields)) {
			throw new Exception("Invalid record");
		}
		
		ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
		StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(getId());
		
		long parentModuleId = stateFlowContext.getModuleId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(parentModuleId);
		
		FacilioContext c = new FacilioContext();
		c.put(FacilioConstants.ContextNames.RECORD, moduleRecord);
		c.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		c.put("default_state_id", stateFlowContext.getDefaultStateId());
		c.put("default_state", true);
		c.put("default_state_flow_id", getId());
		
		FacilioChain chain = FacilioChain.getTransactionChain();
		chain.addCommand(new UpdateStateCommand());
		chain.execute(c);
		
		super.executeTrueActions(record, context, placeHolders);
	}
}
