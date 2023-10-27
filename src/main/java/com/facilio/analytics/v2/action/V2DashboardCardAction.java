package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.analytics.v2.context.V2CardResponseContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

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

        setCardResponse(chain.getContext());
        return V3Action.SUCCESS;
    }
    private void setCardResponse(Context context) throws Exception
    {
        V2CardResponseContext response = new V2CardResponseContext();
        JSONObject json = new JSONObject();
        json.putAll(cardContext.getCardParams().getResult().get("value"));
        response.setValue(json);
        JSONObject baseline_json = new JSONObject();
        if(cardContext.getCardParams().getResult().get("baseline_value") != null)
        {
            baseline_json.putAll(cardContext.getCardParams().getResult().get("value"));
            response.setBaseline(baseline_json);
        }
        response.setTimeFilter(cardContext.getCardParams().getTimeFilter());
        if(context.containsKey(FacilioConstants.ContextNames.CARD_STATE)){
            response.setStyles((JSONObject) context.get(FacilioConstants.ContextNames.CARD_STATE));
        }
        setData("result", response);
    }
}
