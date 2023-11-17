package com.facilio.bmsconsole.commands;

import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.*;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class ConstructNsFieldsForSensorCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SensorRuleContext sensorRule= (SensorRuleContext) context.get(FacilioConstants.ContextNames.SENSOR_RULE_MODULE);

        NameSpaceContext ns = constructNameSpace(sensorRule);
        context.put(NamespaceConstants.NAMESPACE, ns);
        context.put(NamespaceConstants.NAMESPACE_FIELDS, ns.getFields());
        context.put(WorkflowV2Util.WORKFLOW_CONTEXT, ns.getWorkflowContext());
        return false;
    }

    private static NameSpaceContext constructNameSpace(SensorRuleContext sensorRule) {
        NameSpaceContext ns=new NameSpaceContext();
        ns.setWorkflowContext(null);
        ns.setType(NSType.SENSOR_RULE.getIndex());
        ns.setStatus(Boolean.TRUE);
        ns.setExecModeEnum(NSExecMode.CODE_EXEC);
        //ns.setExecInterval() - intentionally avoid this attribute
        sensorRule.setNs(ns);
        constructNameSpaceFields(sensorRule);
        return sensorRule.getNs();
    }

    private static void constructNameSpaceFields(SensorRuleContext sensorRule) {
        List<NameSpaceField> nsFields = new ArrayList<>();
        NameSpaceField nsField = new NameSpaceField();
        nsField.setVarName(String.valueOf('A'));
        nsField.setFieldId(sensorRule.getSensorFieldId());
        nsField.setAggregationType(AggregationType.FIRST);
        nsField.setNsFieldType(NsFieldType.ASSET_READING);
//        nsField.setDataInterval - intentionally avoid this attr
        nsFields.add(nsField);
        sensorRule.getNs().setFields(nsFields);
    }
}
