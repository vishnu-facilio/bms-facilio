package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeforeSavePMPlannerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(FacilioConstants.ContextNames.PMPLANNER);

        List<ModuleBaseWithCustomFields> createTriggerRecords = new ArrayList<>();
        List<ModuleBaseWithCustomFields> updateTriggerRecords = new ArrayList<>();

        for (ModuleBaseWithCustomFields moduleBaseWithCustomField : moduleBaseWithCustomFields) {
            PMPlanner pmPlanner = (PMPlanner) moduleBaseWithCustomField;
            PMTriggerV2 trigger = pmPlanner.getTrigger();
            if (trigger == null) {
                continue;
            }
            long id = trigger.getId();
            if (id > 0) {
                updateTriggerRecords.add(trigger);
            }  else {
                createTriggerRecords.add(trigger);
            }
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmTriggerV2 = modBean.getModule("pmTriggerV2");

        if (!createTriggerRecords.isEmpty()) {
            V3Util.createRecord(pmTriggerV2, createTriggerRecords);
        }

        if (!updateTriggerRecords.isEmpty()) {
            for(ModuleBaseWithCustomFields triggerV2: updateTriggerRecords){
                Map<String, Object> triggerMap = FieldUtil.getAsProperties(triggerV2);
                V3Util.updateBulkRecords("pmTriggerV2",triggerMap, Collections.singletonList(triggerV2.getId()),false);
            }
            //V3RecordAPI.batchUpdateRecords("pmTriggerV2", updateTriggerRecords, pmTriggerV2Fields);
        }
        return false;
    }
}
