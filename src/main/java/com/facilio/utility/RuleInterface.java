package com.facilio.utility;

import com.facilio.chain.FacilioContext;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.commons.chain.Context;

import java.util.List;

public interface RuleInterface {

     public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception ;
     public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context,Double actualConsumption,Double billMeterConsumption,Double difference) throws Exception;
     public UtilityDisputeContext ValidateBillMissing(long billDate,UtilityIntegrationMeterContext list) throws Exception;

     public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception;

     public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception ;
}
