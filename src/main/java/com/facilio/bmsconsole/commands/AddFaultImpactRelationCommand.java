package com.facilio.bmsconsole.commands;

import com.facilio.beans.NamespaceBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.context.*;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFaultImpactRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean canDelete = (boolean) context.getOrDefault("canDeleteFaultImpact", false);
        boolean addImpact = (boolean) context.getOrDefault("addFaultImpact", true);
        if (addImpact) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<NewReadingRuleContext> list = recordMap.get(moduleName);

            if (CollectionUtils.isNotEmpty(list)) {
                for (NewReadingRuleContext readingRule : list) {
                    if (readingRule.getImpact() != null && readingRule.getImpact().getId() != -1) {

                        Long categoryId = readingRule.getCategory().fetchId();
                        FaultImpactContext impact = (FaultImpactContext) V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, readingRule.getImpact().getId(), null);

                        readingRule.setImpact(FaultImpactAPI.getFaultImpactContext(readingRule.getImpact().getId()));
                        NameSpaceContext impactNameSpaceContext = new NameSpaceContext(NSType.FAULT_IMPACT_RULE, readingRule.getId(), readingRule.getNs().getExecInterval(), readingRule.getImpact().getWorkflowId(), categoryId);
                        impactNameSpaceContext.setStatus(Boolean.TRUE);
                        NamespaceBean nsBean = Constants.getNsBean();
                        Long impactNameSpaceId = nsBean.addNamespace(impactNameSpaceContext);

                        List<NameSpaceField> fields = new ArrayList<>();

                        if (!Collections.isEmpty(impact.getFields())) {
                            for (FaultImpactNameSpaceFieldContext faultImpactField : impact.getFields()) {
                                NameSpaceField field = faultImpactField.getNameSpaceField();
                                field.setNsId(impactNameSpaceId);
                                fields.add(field);
                            }
                        }

                        fields.add(constructRuleNsFld(readingRule));

                        nsBean.addNamespaceFields(impactNameSpaceId, fields);
                        readingRule.setImpactId(impact.getId());
                    }
                }
            }
        }
        return false;
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
        fld.setNsFieldType(NsFieldType.ASSET_READING);
        return fld;
    }

}
