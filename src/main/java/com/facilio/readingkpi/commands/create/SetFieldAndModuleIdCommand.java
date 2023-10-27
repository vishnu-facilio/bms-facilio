package com.facilio.readingkpi.commands.create;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.readingkpi.context.KPIType;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetFieldAndModuleIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {
                kpi.setOrgId(AccountUtil.getCurrentOrg().getId());
                if (kpi.getKpiTypeEnum() != KPIType.DYNAMIC) {
                    FacilioChain addReadingChain = TransactionChainFactory.getAddFDDReadingModuleChain(kpi.getResourceTypeEnum().equals(ResourceType.METER_CATEGORY));
                    addReadingChain.execute(context);
                    List<FacilioModule> modules = (List) context.get(FacilioConstants.ContextNames.MODULE_LIST);
                    kpi.setReadingModuleId(modules.get(0).getModuleId());
                    List moduleFieldIds = (List) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);
                    kpi.setReadingFieldId((Long) moduleFieldIds.get(0));
                }
                setKPILinkName(kpi);
                kpi.setReadingField(null);
                kpi.setStatus(true);
            }
        }
        return false;
    }

    private void setKPILinkName(ReadingKPIContext readingKPI) throws Exception {
        String linkName = DisplayNameToLinkNameUtil.getLinkName(readingKPI.getName(), FacilioConstants.ReadingKpi.READING_KPI, FacilioConstants.ContextNames.LINK_NAME);
        readingKPI.setLinkName(linkName);
    }
}
