package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DashboardCustomScriptFilter;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class CardAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WidgetCardContext cardContext;
	
	public void setCardContext(WidgetCardContext cardContext) {
		this.cardContext = cardContext;
	}
	
	public WidgetCardContext getCardContext() {
		return this.cardContext;
	}
	
	private Long cardId;
	
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	
	public Long getCardId() {
		return this.cardId;
	}
	
	
	private String cardFilter;
	
	public String getCardFilters()
	{
		
		return this.cardFilter;
	}
	
	public  JSONObject getCardFilterJson() throws ParseException
	{
		if(this.cardFilter!=null)
		{
			return FacilioUtil.parseJson(this.cardFilter);
		}
		else 
		{
			return null;
		}
	}
	
	public void setCardFilters(String cardFilter)
	{
		this.cardFilter=cardFilter;
	}
	
	private DashboardCustomScriptFilter customScriptFilter;
	
public DashboardCustomScriptFilter getCustomScriptFilter() {
		return customScriptFilter;
	}

	public void setCustomScriptFilter(DashboardCustomScriptFilter customScriptFilter) {
		this.customScriptFilter = customScriptFilter;
	}

private String cardUserFilters;
	
	public String getCardUserFilters()
	{
		
		return this.cardUserFilters;
	}
	
	public  JSONObject getCardUserFiltersJson() throws ParseException
	{
		if(this.cardUserFilters!=null)
		{
			return FacilioUtil.parseJson(this.cardUserFilters);
		}
		else 
		{
			return null;
		}
	}
	
	public void setCardUserFilters(String cardUserFilters)
	{
		this.cardUserFilters=cardUserFilters;
	}
	


	public String getCardData() throws Exception {
		try {
			if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DASHBOARD_V2)){
				FacilioChain chain = ReadOnlyChainFactory.getComboCardWorkflowChain();
				chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
				chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, cardId);
				chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, getCardFilterJson());
				chain.getContext().put(FacilioConstants.ContextNames.CARD_USER_FILTERS, getCardUserFiltersJson());
				chain.getContext().put(FacilioConstants.ContextNames.CARD_CUSTOM_SCRIPT_FILTERS, getCustomScriptFilter());
				chain.execute();
				List<Map<String,Object>> cardResult = (List<Map<String, Object>>) chain.getContext().get("cardResult");
				if(cardResult.size() > 1){
					setResult("cardResult",chain.getContext().get("cardResult"));
				}
				else {
					setResult("cardContext", cardResult.get(0).get("cardContext"));
					setResult("data", cardResult.get(0).get("data"));
					setResult("state", cardResult.get(0).get("state"));
				}
			}
			else{
				executeContext();
			}
		}
		catch(Exception e){
				LOGGER.info("Error Occurred on getCardData", e);
				throw new Exception("Error Occurred on getCardData", e);
			}
			return SUCCESS;
		}
		public void executeContext() throws Exception {
			FacilioChain chain = ReadOnlyChainFactory.getExecuteCardWorkflowChain();
			chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
			chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, cardId);
			chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, getCardFilterJson());
			chain.getContext().put(FacilioConstants.ContextNames.CARD_USER_FILTERS, getCardUserFiltersJson());
			chain.getContext().put(FacilioConstants.ContextNames.CARD_CUSTOM_SCRIPT_FILTERS, getCustomScriptFilter());
			chain.execute();
			setResult("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
			setResult("data", chain.getContext().get(FacilioConstants.ContextNames.CARD_RETURN_VALUE));
			if (chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE) != null) {
				setResult("state", chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE));
			} else {
				setResult("state", ((WidgetCardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT)).getCardState());
			}
		}

}
