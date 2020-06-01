package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AggregationMetaContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AggregationAction extends FacilioAction {

    private AggregationMetaContext aggregationMeta;
    public AggregationMetaContext getAggregationMeta() {
        return aggregationMeta;
    }
    public void setAggregationMeta(AggregationMetaContext aggregationMeta) {
        this.aggregationMeta = aggregationMeta;
    }

    public String addOrUpdateAggregation() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateAggregation();
        Context context = chain.getContext();
        context.put(FacilioConstants.ContextNames.AGGREGATION_META, aggregationMeta);
        chain.execute();

        setResult(FacilioConstants.ContextNames.AGGREGATION_META, context.get(FacilioConstants.ContextNames.AGGREGATION_META));

        return SUCCESS;
    }
}
