package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.analytics.v2.context.V2CardResponseContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.cards.util.CardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class V2DashboardCardAction extends V3Action {

    public Long cardId;
    public V2CardContext cardContext;
    public String fetch()throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getCHCardAnalyticsCardData();
        chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
        chain.getContext().put("v2", true);
        chain.execute();

        V2CardResponseContext response = setCardResponse(chain.getContext());
        setData("result", response);
        return V3Action.SUCCESS;
    }
    public String fetchData()throws Exception
    {
        if(cardId == null || cardId <=0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid CardId");
        }
        List<WidgetCardContext> childCards =  CardUtil.getChildCards(cardId);
        List<V2CardResponseContext> response = new ArrayList<>();
        if(childCards != null && childCards.size() > 0){
            for(WidgetCardContext childCard : childCards){
                V2CardResponseContext result = executeCardFlow(childCard.getId());
                response.add(result);
            }
        }
       else{
            V2CardResponseContext result = executeCardFlow(cardId);
            response.add(result);
        }
        setData("result", response);
        return V3Action.SUCCESS;
    }
    private V2CardResponseContext setCardResponse(Context context) throws Exception
    {
        V2CardResponseContext response = new V2CardResponseContext();
        JSONObject json = new JSONObject();
        json.putAll(cardContext.getCardParams().getResult().get("value"));
        json.put("title",cardContext.getCardParams().getTitle());
        response.setValue(json);
        JSONObject baseline_json = new JSONObject();
        if(cardContext.getCardParams().getResult().get("baseline_value") != null)
        {
            baseline_json.putAll(cardContext.getCardParams().getResult().get("baseline_value"));
            response.setBaseline(baseline_json);
        }
        response.setTimeFilter(cardContext.getCardParams().getTimeFilter());
        if(context.containsKey(FacilioConstants.ContextNames.CARD_STATE)){
            response.setStyles((JSONObject) context.get(FacilioConstants.ContextNames.CARD_STATE));
        }
        if(cardContext.getCardParams().getCardStyle() != null){
            response.setCardStyle(cardContext.getCardParams().getCardStyle());
        }
        return response;
    }
    private V2CardResponseContext executeCardFlow(long cardId) throws Exception {
        FacilioChain chain = V2AnalyticsTransactionChain.getAnalyticCardDataChain();
        chain.getContext().put("cardId", cardId);
        chain.getContext().put("v2", true);
        chain.execute();
        cardContext = (V2CardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT);
        V2CardResponseContext result =  setCardResponse(chain.getContext());
        return result;
    }
}
