package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2AnalyticsCardWidgetContext;
import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.bmsconsole.commands.FetchReportDataCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class V2FetchAnalyticDataCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(V2FetchAnalyticDataCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        V2CardContext cardContext = (V2CardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
        if( cardContext != null && cardContext.getCardParams() != null )
        {
            FacilioChain card_chain = V2AnalyticsTransactionChain.getAnalyticsCardDataChain();
            card_chain.setContext((FacilioContext) context);
            card_chain.execute();
            context.put("result", card_chain.getContext().get("result"));
            return false;
        }
        try {
            FacilioChain chain = V2AnalyticsTransactionChain.getCHAnalyticsDataChain();
            context.put("isClickHouseEnabled", AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE));//AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE));
            chain.setContext((FacilioContext) context);
            chain.execute();
        }
        catch (Exception e){
            LOGGER.debug("executing in mysql because of error in clickhouse", e);
        }
        return false;
    }
}
