package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class CallToControlActionGenerationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null || recordMap.size() == 0){
            return false;
        }
        List<V3ControlActionTemplateContext> controlActionTemplateContextList = (List<V3ControlActionTemplateContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionTemplateContextList)){
            return false;
        }
        Long currentTime = System.currentTimeMillis();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), DBConf.getInstance().getCurrentZoneId());
        ZonedDateTime startTime = zonedDateTime.plusDays(1);
        zonedDateTime = zonedDateTime.plusMonths(1);
        for(V3ControlActionTemplateContext controlActionTemplateContext : controlActionTemplateContextList){
            ControlActionAPI.generateControlActionFromTemplateWms(controlActionTemplateContext.getId(),startTime.toEpochSecond()*1000,zonedDateTime.toEpochSecond()*1000);
        }
        return false;
    }
}
