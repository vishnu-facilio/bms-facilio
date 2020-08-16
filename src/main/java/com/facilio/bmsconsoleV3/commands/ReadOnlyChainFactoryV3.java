package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.commands.facility.GetFacilityAvailabilityCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.AddDefaultCriteriaForQuoteFetchCommandV3;
import com.facilio.bmsconsoleV3.commands.quotation.QuotationFillLookupFields;
import com.facilio.bmsconsoleV3.commands.tenant.LoadTenantLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.SetTenantSpaceAndContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.usernotification.AddUserCriteriaMyNotification;
import com.facilio.bmsconsoleV3.commands.usernotification.FetchUnSeenNotificationCommand;
import com.facilio.bmsconsoleV3.commands.visitorlog.LoadRecordIdForPassCodeCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlog.SetInviteStatusConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.SetInviteConditionForVisitsListCommandV3;
import com.facilio.bmsconsoleV3.commands.workorder.*;
import com.facilio.chain.FacilioChain;

public class ReadOnlyChainFactoryV3 {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getVisitorLoggingBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getInviteVisitorLogBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetInviteStatusConditionForVisitsListCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
    
    public static FacilioChain getVisitorLogBeforeFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }
 
    public static FacilioChain getBaseVisitBeforeFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadRecordIdForPassCodeCommandV3());
        c.addCommand(new LoadVisitorLoggingLookupCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        c.addCommand(new SetTenantSpaceAndContactsCommandV3());
        return c;
    }

    public static FacilioChain getWorkorderAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadWorkorderLookupsAfterFetchcommandV3());
        c.addCommand(new LoadWorkorderRelatedModulesCommandV3());
        c.addCommand(new GetworkorderHazardsCommandV3());
        //c.addCommand(new GetAvailableStateCommand());
        //c.addCommand(new GetTaskInputDataCommand());
        //c.addCommand(new FetchApprovalRulesCommand());
        c.addCommand(new FetchSourceTypeDetailsForWorkorderCommandV3());
        c.addCommand(new FetchStateflowAndSlaCommandV3());

        return c;
    }

    public static FacilioChain getQuoteBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultCriteriaForQuoteFetchCommandV3());
        c.addCommand(new QuotationFillLookupFields());
        return c;
    }

    public static FacilioChain getUserNotificationBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchUnSeenNotificationCommand());
        c.addCommand(new AddUserCriteriaMyNotification());
        return c;
    }

    public static FacilioChain getFacilityAfterFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GetFacilityAvailabilityCommandV3());
        return c;
    }


}
