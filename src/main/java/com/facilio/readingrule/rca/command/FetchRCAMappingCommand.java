package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchRCAMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NewReadingRuleContext> ruleList = NewReadingRuleAPI.destructureRuleFromRecordMap(context);

        FacilioModule rcaMappingModule = ModuleFactory.getReadingRuleRCAMapping();
        List<FacilioField> fields = FieldFactory.getReadingRuleRCAMapping();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        for (NewReadingRuleContext rule : ruleList) {
            ReadingRuleRCAContext rca = rule.getRca();
            if (rca != null) {
                GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                        .select(fields)
                        .table(rcaMappingModule.getTableName())
                        .innerJoin(ModuleFactory.getNewReadingRuleModule().getTableName())
                        .on(rcaMappingModule.getTableName()+".RCA_RULE_ID"+"="+ModuleFactory.getNewReadingRuleModule().getTableName()+".ID ")
                        .andCondition(CriteriaAPI.getCondition(fieldsMap.get("rcaId"), Collections.singleton(rca.getId()), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(ModuleFactory.getNewReadingRuleModule().getTableName()+".SYS_DELETED","sys_deleted",null ,CommonOperators.IS_EMPTY));

                List<Map<String, Object>> props = selectRecordBuilder.get();
                List<Long> rcaRuleIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(props)) {
                    for (Map<String, Object> prop : props) {
                        rcaRuleIds.add((Long) prop.get("rcaRuleId"));
                    }
                }
                rca.setRcaRuleIds(rcaRuleIds);
            }
        }
        return false;
    }
}