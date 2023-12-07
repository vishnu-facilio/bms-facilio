package com.facilio.telemetry.command;

import com.facilio.beans.NamespaceBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.telemetry.util.TelemetryCriteriaAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchNamespaceForTelemetryCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NamespaceBean bean = Constants.getNsBean();
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<TelemetryCriteriaContext> telemetryCriteriaList = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(telemetryCriteriaList)) {
            for(TelemetryCriteriaContext criteriaContext : telemetryCriteriaList) {
                criteriaContext.setNamespace(bean.getNamespace(criteriaContext.getNamespaceId()));
                Map<String,Object> map = new HashMap<>();
                criteriaContext.setData(map);
            }
        }
        return false;
    }
}
