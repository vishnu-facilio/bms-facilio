package com.facilio.readingkpi.commands.create;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.List;
import java.util.Map;

public class SetFieldAndModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ReadingKPIContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (ReadingKPIContext kpi : list) {

                if (context.get(FacilioConstants.ContextNames.MODULE_ID) != null) {
                    FacilioModule module = Constants.getModBean().getModule((Long) context.get(FacilioConstants.ContextNames.MODULE_ID));
                    List moduleFieldIds = (List) context.get(FacilioConstants.ContextNames.MODULE_FIELD_IDS);

                    kpi.setReadingFieldId((Long) moduleFieldIds.get(0));
                    kpi.setReadingModuleId(module.getModuleId());
                }

                setKPILinkName(kpi);
                kpi.setReadingField(null);
                kpi.setStatus(BooleanUtils.toBooleanDefaultIfNull(kpi.getStatus(), Boolean.TRUE));
            }
        }
        return false;
    }

    private void setKPILinkName(ReadingKPIContext readingKPI) throws Exception {
        String linkName = DisplayNameToLinkNameUtil.getLinkName(readingKPI.getName(), FacilioConstants.ReadingKpi.READING_KPI, FacilioConstants.ContextNames.NAME);
        readingKPI.setLinkName(linkName);
    }

}
