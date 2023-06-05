package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class FillTicketStatusInWorkOrderFeatureSettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("FillTicketStatusInWorkOrderFeatureSettingsCommand");

        List<Map<String, Object>> workOrderFeatureSettingsMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP);

        if(CollectionUtils.isEmpty(workOrderFeatureSettingsMap)){
            LOGGER.info("workOrderFeatureSettingsMap is empty.");
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioStatus> ticketStatusList = TicketAPI.getStatuses(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER),null); //TODO: QUERY-optimisation: 2 Query
        Map<Long, FacilioStatus> ticketStatusListMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(ticketStatusList)){
            ticketStatusListMap = FieldUtil.getAsMap(ticketStatusList);
        }

        List<V3WorkOrderFeatureSettingsContext> workOrderFeatureSettingsList = FieldUtil.getAsBeanListFromMapList(workOrderFeatureSettingsMap, V3WorkOrderFeatureSettingsContext.class);

        for (V3WorkOrderFeatureSettingsContext settingsContext: workOrderFeatureSettingsList){
            settingsContext.setAllowedTicketStatus(ticketStatusListMap.get(settingsContext.getAllowedTicketStatusId()));
        }

        workOrderFeatureSettingsMap = FieldUtil.getAsMapList(workOrderFeatureSettingsList, V3WorkOrderFeatureSettingsContext.class);

        context.put(FacilioConstants.ContextNames.WORK_ORDER_FEATURE_SETTINGS_LIST_MAP, workOrderFeatureSettingsMap);

        return false;
    }
}
