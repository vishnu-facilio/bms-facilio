package com.facilio.bmsconsole.actions;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.RuleTemplateAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class RuleTemplateAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id ;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String result;
	public String getRes() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	private JSONObject placeHolder;
	
	public JSONObject getPlaceHolder() {
		return placeHolder;
	}
	public void setPlaceHolder(JSONObject placeHolder) {
		this.placeHolder = placeHolder;
	}
	
	@SuppressWarnings("unchecked")
	public String createRulefromTemplates () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE_ID, id);
		context.put(FacilioConstants.ContextNames.PLACE_HOLDER, placeHolder);
		context.put(FacilioConstants.ContextNames.IS_EDIT_TEMPLATE, false);
		FacilioChain createRuleChain = TransactionChainFactory.getAddTemplateToRules();
		createRuleChain.execute(context);
		setResult("rule", id);
		return SUCCESS;
	}
	
	
	
	public String getTemplatesRuleContext () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE_ID, id);
		context.put(FacilioConstants.ContextNames.PLACE_HOLDER, placeHolder);
		context.put(FacilioConstants.ContextNames.IS_EDIT_TEMPLATE, true);
		FacilioChain createRuleChain = TransactionChainFactory.getAddTemplateToRules();
		createRuleChain.execute(context);
		setResult("alarmRule", context.get(FacilioConstants.ContextNames.ALARM_RULE));
		return SUCCESS;
	}
	
	
	public String getDefaultRuleTemplates() throws Exception {
		setResult("templates", TemplateAPI.getAllRuleLibraryTemplate());
		return SUCCESS;
	}
	
	
	public String getActiveRuleTemplates() throws Exception {
		setResult("ruleTemplateRel", RuleTemplateAPI.getAppliedTemplates());
		return SUCCESS;
	}
}