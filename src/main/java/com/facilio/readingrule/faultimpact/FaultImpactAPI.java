package com.facilio.readingrule.faultimpact;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.util.V3Util;

import java.util.Collections;
import java.util.Map;

public class FaultImpactAPI {

    public static FaultImpactContext getFaultImpactContext(Long id) throws Exception {
        return (FaultImpactContext) V3Util.getRecord(FacilioConstants.FaultImpact.MODULE_NAME, id, null);
    }
    public static Double getCostImpact(Long resourceId, Long ruleId, Long startTime, Long endTime) throws Exception {
        FacilioField impactField = FieldFactory.getField("costImpact", "Cost Impact", "COST_IMPACT", null, FieldType.DECIMAL);
        return getImpact(resourceId, ruleId, impactField, startTime, endTime);
    }
    public static Double getEnergyImpact(Long resourceId, Long ruleId, Long startTime, Long endTime) throws Exception {
        FacilioField impactField = FieldFactory.getField("energyImpact", "Energy Impact", "ENERGY_IMPACT", null, FieldType.DECIMAL);
        return getImpact(resourceId, ruleId, impactField, startTime, endTime);
    }
    private static Double getImpact(Long resourceId, Long ruleId, FacilioField impactField, Long startTime, Long endTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule newRuleModule = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);
        String newRuleTableName = newRuleModule.getTableName();
        String ruleReadingsTableName = NewReadingRuleAPI.READING_RULE_FIELD_TABLE_NAME;
        StringBuilder impactSumColumn = new StringBuilder("SUM(COALESCE(")
                .append(ruleReadingsTableName).append(".")
                .append(impactField.getColumnName()).append(",").append("0))");
        FacilioField sumField = new FacilioField();
        sumField.setName(FacilioConstants.Reports.SUM_FUNC);
        sumField.setColumnName(String.valueOf(impactSumColumn));
        sumField.setDataType(FieldType.NUMBER);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(sumField))
                .table(ruleReadingsTableName)
                .innerJoin(newRuleTableName)
                .on(ruleReadingsTableName+".MODULEID = "+newRuleTableName+".READING_MODULE_ID")
                .andCondition(CriteriaAPI.getCondition(ruleReadingsTableName+".PARENT_ID", "parentId", String.valueOf(resourceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getIdCondition(ruleId, newRuleModule))
                .andCondition(CriteriaAPI.getCondition("TTIME","ttime", startTime + "," + endTime, DateOperators.BETWEEN));
        Map<String, Object> props = builder.fetchFirst();
        return (props == null) ? null : (Double) props.get("sum");
    }
}
