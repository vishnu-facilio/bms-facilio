package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;

public class ExecuteCardWorkflowCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WidgetCardContext cardContext = (WidgetCardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
		Long cardId = (Long) context.get(FacilioConstants.ContextNames.CARD_ID);
		
		
		if (cardId != null && cardContext == null) {
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getWidgetCardModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(), cardId + "", NumberOperators.EQUALS));
			
			builder.select(FieldFactory.getWidgetCardFields());
			
			List<Map<String,Object>> props = builder.get();
			if (props != null && !props.isEmpty()) {
				cardContext = FieldUtil.getAsBeanFromMap(props.get(0), WidgetCardContext.class);
				
				context.put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);				
			}
			else {
				throw new IllegalArgumentException("No such card found with the given id.");
			}
		}
		
			JSONObject cardFilters = (JSONObject) context.get(FacilioConstants.ContextNames.CARD_FILTERS);
			cardContext.setCardFilters(cardFilters);
			JSONObject cardUserFilters = (JSONObject) context.get(FacilioConstants.ContextNames.CARD_USER_FILTERS);
			cardContext.setCardUserFilters(cardUserFilters);
			
			JSONObject cardParams = cardContext.getCardParams();
			if (cardParams!=null) {
				cardParams.put("cardFilters", cardContext.getCardFilters());
				cardParams.put("cardUserFilters", cardContext.getCardUserFilters());
			}
		
		
		CardLayout cl = CardLayout.getCardLayout(cardContext.getCardLayout());
		if (cl != null) {
			Object cardValue = cl.getResult(cardContext);
		
			context.put(FacilioConstants.ContextNames.CARD_RETURN_VALUE, cardValue);
		}
		else {
			throw new IllegalArgumentException("Invalid card layout.");
		}
		
		return false;
	}
}
