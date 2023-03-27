package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
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
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.*;

public class GetImpactForNewRuleCommand extends FacilioCommand {
    Long ruleId;
    Long parentId;
    Long lastMonthStartTime;
    Long lastMonthEndTime;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
        lastMonthStartTime = DateTimeUtil.getMonthStartTime(-1,false);
        lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
        Boolean isCostImpact = (Boolean) context.get("isCostImpact");
        Map<String, String> props;

        if (isCostImpact) {
            FacilioField costImpactField = FieldFactory.getField("costImpact", "Cost Impact", "COST_IMPACT", null, FieldType.DECIMAL);
            props = getCostOrEnergyImpact(costImpactField);
        }
        else {
            FacilioField energyImpactField = FieldFactory.getField("energyImpact", "Energy Impact", "ENERGY_IMPACT", null, FieldType.DECIMAL);
            props = getCostOrEnergyImpact(energyImpactField);
        }
        context.putAll(props);
        return false;
    }
    private Map<String, String> getCostOrEnergyImpact(FacilioField impactField) throws Exception {
        Double thisMonth = getImpact(false, impactField);
        Double lastMonth = getImpact(true, impactField);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("lastMonth", String.format("%.2f",lastMonth));
        resultMap.put("thisMonth", String.format("%.2f",thisMonth));
        return resultMap;
    }
    private Double getImpact(Boolean isLastMonth, FacilioField impactField) throws Exception {
        GenericSelectRecordBuilder builder = getSelectBuilder(impactField);
        if (isLastMonth) {
            builder.andCondition(CriteriaAPI.getCondition("TTIME","ttime", lastMonthStartTime + "," + lastMonthEndTime, DateOperators.BETWEEN));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("TTIME","ttime", String.valueOf(lastMonthEndTime),  NumberOperators.GREATER_THAN));
        }
        Map<String, Object> props = builder.fetchFirst();
        return (props != null) ? (Double) props.get(FacilioConstants.Reports.SUM_FUNC) : 0D;
    }
    private GenericSelectRecordBuilder getSelectBuilder(FacilioField impactField) throws Exception {
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
                .andCondition(CriteriaAPI.getCondition(ruleReadingsTableName+".PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getIdCondition(ruleId, newRuleModule));
        return builder;
    }
}
