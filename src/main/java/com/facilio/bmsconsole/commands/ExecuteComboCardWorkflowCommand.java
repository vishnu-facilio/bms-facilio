package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

public class ExecuteComboCardWorkflowCommand extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long cardId = (Long) context.get(FacilioConstants.ContextNames.CARD_ID);
        if (cardId != null && cardId > 0) {
            GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getWidgetCardModule().getTableName())
                    .select(FieldFactory.getWidgetCardFields())
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(cardId), NumberOperators.EQUALS));
            List<Map<String, Object>> props = select.get();
            if (props != null && !props.isEmpty()) {
                JSONObject cardFilters = (JSONObject) context.get(FacilioConstants.ContextNames.CARD_FILTERS);
                JSONObject cardUserFilters = (JSONObject) context.get(FacilioConstants.ContextNames.CARD_USER_FILTERS);
                List<Map<String,Object>> card = new ArrayList<>();
                for(Map<String, Object> prop :props) {
                    long childCardId = (long) prop.get("id");
                    Map<String,Object> childCard = new HashMap<>();
                    FacilioChain chain = ReadOnlyChainFactory.getExecuteCardWorkflowChain();
                    chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, childCardId);
                    if(cardFilters != null){
                        chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, cardFilters.get(String.valueOf(childCardId)));
                    }
                    if(cardUserFilters != null){
                        chain.getContext().put(FacilioConstants.ContextNames.CARD_USER_FILTERS, cardUserFilters.get(String.valueOf(childCardId)));
                    }
                    chain.execute();
                    childCard.put("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
                    childCard.put("data", chain.getContext().get(FacilioConstants.ContextNames.CARD_RETURN_VALUE));
                    if (chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE) != null) {
                        childCard.put("state", chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE));
                    } else {
                        childCard.put("state", ((WidgetCardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT)).getCardState());
                    }
                    card.add(childCard);
                }
                context.put("cardResult",card);
            }
            else{
                executeContext(context);
            }
        }
        else {
            executeContext(context);
        }

        return false;
    }
    public void executeContext(Context context) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getExecuteCardWorkflowChain();
        chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, context.get(FacilioConstants.ContextNames.CARD_CONTEXT));
        chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, context.get(FacilioConstants.ContextNames.CARD_ID));
        chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, context.get(FacilioConstants.ContextNames.CARD_FILTERS));
        chain.getContext().put(FacilioConstants.ContextNames.CARD_USER_FILTERS, context.get(FacilioConstants.ContextNames.CARD_USER_FILTERS));
        chain.getContext().put(FacilioConstants.ContextNames.CARD_CUSTOM_SCRIPT_FILTERS,context.get(FacilioConstants.ContextNames.CARD_CUSTOM_SCRIPT_FILTERS));
        chain.execute();
        Map<String,Object> cardResult = new HashMap<>();
        List<Map<String,Object>> card = new ArrayList<>();
        cardResult.put("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
        cardResult.put("data", chain.getContext().get(FacilioConstants.ContextNames.CARD_RETURN_VALUE));
        if (chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE) != null) {
            cardResult.put("state", chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE));
        } else {
            cardResult.put("state", ((WidgetCardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT)).getCardState());
        }
        card.add(cardResult);
        context.put("cardResult",card);
    }
}
