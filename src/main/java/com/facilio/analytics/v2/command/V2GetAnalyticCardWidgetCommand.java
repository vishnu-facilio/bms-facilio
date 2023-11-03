package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.context.V2AnalyticsCardWidgetContext;
import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class V2GetAnalyticCardWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long cardId = (Long) context.get("cardId");
        DashboardWidgetContext widget = DashboardUtil.getWidget(cardId);
        if(widget != null)
        {
            WidgetCardContext cardContext = (WidgetCardContext) widget;
            JSONObject cardParams = cardContext.getCardParams();
            if(cardParams != null)
            {
                V2CardContext v2_cardContext  = new V2CardContext();
                JSONObject conditionalFormatting = cardContext.getConditionalFormatting();
                V2AnalyticsCardWidgetContext v2_card_params = FieldUtil.getAsBeanFromJson(cardParams, V2AnalyticsCardWidgetContext.class);
                if(v2_card_params.getCriteriaType() == V2MeasuresContext.Criteria_Type.CRITERIA.getIndex() && v2_card_params.getCriteriaId() != null && v2_card_params.getCriteriaId() > 0)
                {
                    Criteria criteria = CriteriaAPI.getCriteria(v2_card_params.getCriteriaId());
                    v2_card_params.setCriteria(criteria);
                }
                v2_cardContext.setCardParams(v2_card_params);
                if(conditionalFormatting != null) {
                    v2_cardContext.setConditionalFormatting(conditionalFormatting);
                    v2_cardContext.setCardState(cardContext.getCardState());
                }
                context.put(FacilioConstants.ContextNames.CARD_CONTEXT, v2_cardContext);
            }
        }
        return false;
    }
}
