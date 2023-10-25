package com.facilio.utility;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.util.JobPlanApi;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Log4j @Getter @Setter
public class UtilityAction extends V3Action {

    private static final long serialVersionUID = 1L;

    String referral;
    String state;

    String customerType;
    Long meterID;
    String frequency;
    String prepay;
    private Long recordId ;
    Long startTime;
    Long endTime;
    private String identifier;
    long id;
    List<Long>meterIds;
    String secretState;


    public String createUtilityAccounts() throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.createUtilityAccountChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY);

        chain.execute();
        setData("state",context.get("state"));

        return V3Action.SUCCESS;
    }
    public String fetchUtilityCustomerAndMeter() throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.getUtilityDataForReferralChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.REFERRALS, referral);
        context.put(FacilioConstants.STATE,state);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.UTILITY_INTEGRATION_CUSTOMER_ACTIVITY);
        chain.execute();
        setData("success","success");

        return V3Action.SUCCESS;

    }

    public String activateMeters() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getActivateMeterChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.METERID, meterID);
        chain.execute();

        return SUCCESS;
    }


    public String ongoingMonitoring() throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.scheduleOngoingMonitoringChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.METERID, meterID);
        context.put(FacilioConstants.FREQUENCY,frequency);
        context.put(FacilioConstants.PREPAY,prepay);

        chain.execute();

        return SUCCESS;
    }

    public String fetchBills() throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.getUtilityBillsChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.METERID, meterID);
        context.put(FacilioConstants.ContextNames.STARTTIME, startTime);
        context.put(FacilioConstants.ContextNames.ENDTIME, endTime);

        chain.execute();

        return V3Action.SUCCESS;

    }
    public String updateDisputeStatus() throws Exception {
        FacilioContext context = new FacilioContext();
        HashMap<String, String> successMsg = new HashMap<>();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        switch (getIdentifier()) {
            case FacilioConstants.RESOLVE_DISPUTE:
                FacilioChain chain = TransactionChainFactoryV3.getUtilityDisputeChain();
                chain.execute(context);
                successMsg.put("message", "Dispute Resolved Successfully");
                break;
        }
        setData(FacilioConstants.UTILITY_DISPUTE_STATUS,successMsg);
        return SUCCESS;
    }

    public String associateMeter() throws Exception{
        FacilioChain chain = TransactionChainFactoryV3.getAssociateMeterChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.RECORD_LIST, meterIds);
        context.put(FacilioConstants.ContextNames.RECORD_ID,id);
        chain.execute();

//        context.put(FacilioConstants.ContextNames.PARAMS,"isAssociate");
        return V3Action.SUCCESS;

    }

    public String removeDummyAccount() throws Exception{
        FacilioChain chain = TransactionChainFactoryV3.removeDummyAccountChain();
        FacilioContext context = chain.getContext();

        context.put("secretState", secretState);

        chain.execute();
        return V3Action.SUCCESS;
    }

}
