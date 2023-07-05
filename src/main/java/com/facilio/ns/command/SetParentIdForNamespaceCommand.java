package com.facilio.ns.command;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetParentIdForNamespaceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<Object> list = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(list)) {
            for (Object fddObject : list) {
                if (fddObject instanceof NewReadingRuleContext) {
                    NewReadingRuleContext newReadingRuleContext = (NewReadingRuleContext) fddObject;
                    newReadingRuleContext.getNs().setParentRuleId(newReadingRuleContext.getId());
                    newReadingRuleContext.getNs().getWorkflowContext().setIsV2Script(true);
                } else if (fddObject instanceof ReadingKPIContext) {
                    ReadingKPIContext readingKPIContext = (ReadingKPIContext) fddObject;
                    readingKPIContext.getNs().setParentRuleId(readingKPIContext.getId());
                    readingKPIContext.getNs().getWorkflowContext().setIsV2Script(true);
                } else if (fddObject instanceof SensorRuleContext) {
                    SensorRuleContext sensorRuleContext = (SensorRuleContext) fddObject;
                    sensorRuleContext.getNs().setParentRuleId(sensorRuleContext.getId());
                }
            }
        }
        return false;
    }
}