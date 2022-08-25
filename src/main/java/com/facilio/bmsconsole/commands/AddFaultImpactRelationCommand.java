package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.AggregationType;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.util.V3Util;

public class AddFaultImpactRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean canDelete = (boolean) context.getOrDefault("canDeleteFaultImpact", false);
        if (!canDelete) {

            NewReadingRuleContext readingRule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);

            if (readingRule.getImpact() != null && readingRule.getImpact().getId() != -1) {

                FaultImpactContext impact = (FaultImpactContext) V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, readingRule.getImpact().getId(), null);

                Map<Long, ResourceContext> assetsMap = (Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.ASSETS);

                NameSpaceContext impactNameSpaceContext = new NameSpaceContext(NSType.FAULT_IMPACT_RULE, readingRule.getId(), readingRule.getNs().getExecInterval(), readingRule.getNs().getWorkflowId());

                Long impactNameSpaceId = NamespaceAPI.addNamespace(impactNameSpaceContext);

                List<NameSpaceField> fields = new ArrayList<NameSpaceField>();

                if (!Collections.isEmpty(impact.getFields())) {
                    for (FaultImpactNameSpaceFieldContext faultImpactField : impact.getFields()) {
                        NameSpaceField field = faultImpactField.getNameSpaceField();
                        field.setNsId(impactNameSpaceId);
                        fields.add(field);
                    }
                }

                fields.add(constructRuleNsFld(readingRule));

                NewReadingRuleAPI.addNamespaceFields(impactNameSpaceId, assetsMap, fields);
                readingRule.setImpactId(impact.getId());
            }
        }
        return  false;
    }

    private NameSpaceField constructRuleNsFld(NewReadingRuleContext rule) {
        NameSpaceField fld = new NameSpaceField();
//        fld.setResourceId(impFld.getResource().getId());
        fld.setFieldId(rule.getReadingFieldId());
        fld.setDataInterval(rule.getNs().getExecInterval());
        fld.setAggregationTypeI(AggregationType.FIRST.getIndex());
        fld.setModuleId(rule.getReadingModuleId());
        fld.setVarName("_RULE_");
        fld.setPrimary(false);
        return fld;
    }

}
