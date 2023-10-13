package com.facilio.report.module.v2.chain;

import com.facilio.bmsconsole.commands.AddOrUpdateReportCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.reports.ConstructReportDeleteCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.report.module.v2.command.V2AddNewModuleReportCommand;
import com.facilio.report.module.v2.command.V2ConstructModuleReportCommand;
import com.facilio.report.module.v2.command.V2GetFolderListCommand;
import com.facilio.report.module.v2.command.V2GetModuleReportCommand;

public class V2TransactionChainFactory {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getCreateModuleReportChain()throws Exception
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V2ConstructModuleReportCommand());
        c.addCommand(new AddOrUpdateReportCommand());
        c.addCommand(new V2AddNewModuleReportCommand());
        return c;
    }
    public static FacilioChain getV2FetchReportChain()throws Exception
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V2ConstructModuleReportCommand());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        return c;
    }
    public static FacilioChain getV2FolderListChain()throws Exception
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V2GetFolderListCommand());
        return c;
    }
    public static FacilioChain getReportDataChain()throws Exception
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V2GetModuleReportCommand());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        return c;
    }
    public static FacilioChain getDeleteModuleReportChain()throws Exception
    {
        FacilioChain c = getDefaultChain();
        c.addCommand(new V2AddNewModuleReportCommand());
        c.addCommand(new ConstructReportDeleteCommand());
        return c;
    }
}
