package com.facilio.ns.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.ns.NamespaceConstants;
import com.facilio.ns.context.AggregationType;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactContext;
import com.facilio.readingrule.faultimpact.FaultImpactNameSpaceFieldContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrepareNamespaceFromFaultImpactCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long impactTemplateId = (Long) context.get(FacilioConstants.ContextNames.FAULT_IMPACT_TEMPLATE_ID);
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);

        NewReadingRuleContext rule = NewReadingRuleAPI.getRule(ruleId);

        FaultImpactContext impactContext = (FaultImpactContext) V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, impactTemplateId, new HashMap<>());

        NameSpaceContext nameSpaceContext = constructNamespaceContext(impactContext, rule);
        List<NameSpaceField> nameSpaceFields = constructNamespaceFields(impactContext, rule);

        List fields = new ArrayList();
        fields.add(FieldFactory.getField("energyImpact", "Energy Impact", "ENERGY_IMPACT", null, FieldType.DECIMAL));
        fields.add(FieldFactory.getField("costImpact", "Cost Impact", "COST_IMPACT", null, FieldType.DECIMAL));

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = bean.getModule(rule.getReadingModuleId());

        context.put(FacilioConstants.ContextNames.MODULE, module);
//        context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, NewReadingRuleAPI.READING_RULE_FIELD_TABLE_NAME);
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        context.put(NamespaceConstants.NAMESPACE, nameSpaceContext);
        context.put(NamespaceConstants.NAMESPACE_FIELDS, nameSpaceFields);

        return false;
    }

    private NameSpaceContext constructNamespaceContext(FaultImpactContext impactContext, NewReadingRuleContext rule) {
        NameSpaceContext ns = new NameSpaceContext();
        ns.setOrgId(impactContext.getOrgId());
        ns.setParentRuleId(impactContext.getId());
        ns.setExecInterval(rule.getNs().getExecInterval());
        ns.setType(NSType.FAULT_IMPACT_RULE.getIndex());
        return ns;
    }

    private List<NameSpaceField> constructNamespaceFields(FaultImpactContext impactContext, NewReadingRuleContext rule) {
        List<NameSpaceField> nsFields = new ArrayList<>();
        List<FaultImpactNameSpaceFieldContext> impFields = impactContext.getFields();
        for (FaultImpactNameSpaceFieldContext impFld : impFields) {
            NameSpaceField nsFld = constructNsFld(impFld);
            nsFields.add(nsFld);
        }
        nsFields.add(constructRuleNsFld(rule));
        return nsFields;
    }

    private NameSpaceField constructNsFld(FaultImpactNameSpaceFieldContext impFld) {
        NameSpaceField fld = new NameSpaceField();
        fld.setResourceId(impFld.getResource().getId());
        fld.setFieldId(impFld.getFieldId());
        fld.setDataInterval(impFld.getDataInterval());
        fld.setAggregationTypeI(impFld.getAggregationTypeEnum());
        fld.setModuleId(impFld.getModuleId());
        fld.setVarName(impFld.getVarName());
        return fld;
    }

    private NameSpaceField constructRuleNsFld(NewReadingRuleContext rule) {
        NameSpaceField fld = new NameSpaceField();
//        fld.setResourceId(impFld.getResource().getId());
        fld.setFieldId(rule.getReadingFieldId());
        fld.setDataInterval(rule.getNs().getExecInterval());
        fld.setAggregationTypeI(AggregationType.FIRST.getIndex());
        fld.setModuleId(rule.getReadingModuleId());
        fld.setVarName("_RULE_");
        return fld;
    }
}
