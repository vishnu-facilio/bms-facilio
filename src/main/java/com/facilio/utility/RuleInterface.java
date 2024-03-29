package com.facilio.utility;

import com.facilio.chain.FacilioContext;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.commons.chain.Context;

import java.util.List;

public interface RuleInterface {


     public UtilityDisputeContext execute(FacilioContext context) throws Exception ;

}
