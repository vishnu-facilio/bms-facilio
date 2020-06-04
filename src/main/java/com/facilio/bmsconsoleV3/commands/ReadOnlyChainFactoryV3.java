package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.GetInventoryDetailsCommand;
import com.facilio.bmsconsole.commands.LookupPrimaryFieldHandlingCommand;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsoleV3.LookUpPrimaryFieldHandlingCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.LoadTenantLookUpsCommandV3;
import com.facilio.bmsconsoleV3.commands.tenant.SetTenantSpaceAndContactsCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.LoadVisitorLoggingLookupCommandV3;
import com.facilio.bmsconsoleV3.commands.visitorlogging.SetInviteConditionForVisitsListCommandV3;
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

    public static FacilioChain getTenantsAfterFetchOnListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        c.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return c;
    }

    public static FacilioChain getTenantsAfterFetchOnSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new LoadTenantLookUpsCommandV3());
        c.addCommand(new SetTenantSpaceAndContactsCommandV3());
        c.addCommand(new LookUpPrimaryFieldHandlingCommandV3());
        return c;
    }



}
