package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.bmsconsole.commands.FetchReportDataCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class V2FetchAnalyticDataCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(V2FetchAnalyticDataCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLICKHOUSE))
        {
            try {
                FacilioChain chain = V2AnalyticsTransactionChain.getCHAnalyticsDataChain();
                chain.setContext((FacilioContext) context);
                chain.execute();
            }
            catch (Exception e){
                LOGGER.debug("executing in mysql because of error in clickhouse", e);
                this.executeMysqlFetchData((FacilioContext) context);
            }
        }
        else
        {
           this.executeMysqlFetchData((FacilioContext) context);
        }
        return false;
    }

    private void executeMysqlFetchData(FacilioContext context)throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getMySqlAnalyticsDataChain();
        chain.setContext(context);
        chain.execute();
    }
}
