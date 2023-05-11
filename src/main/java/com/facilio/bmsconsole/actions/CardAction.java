package com.facilio.bmsconsole.actions;

import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DashboardCustomScriptFilter;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
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
		try{
		FacilioChain chain = ReadOnlyChainFactory.getExecuteCardWorkflowChain();
		chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
		chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, cardId);
		chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, getCardFilterJson());
		chain.getContext().put(FacilioConstants.ContextNames.CARD_USER_FILTERS, getCardUserFiltersJson());
		chain.getContext().put(FacilioConstants.ContextNames.CARD_CUSTOM_SCRIPT_FILTERS,getCustomScriptFilter());

		
		chain.execute();
			
		setResult("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
		setResult("data", chain.getContext().get(FacilioConstants.ContextNames.CARD_RETURN_VALUE));
		if (chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE) != null) {
			setResult("state", chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE));
		}
		else {
			setResult("state", ((WidgetCardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT)).getCardState());
		}
		}
		catch(Exception e){
			LOGGER.info("Error Occurred on getCardData",e);
			throw new Exception("Error Occurred on getCardData",e);
		}
		return SUCCESS;
	}
}
