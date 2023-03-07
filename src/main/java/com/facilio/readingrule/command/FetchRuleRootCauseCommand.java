package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchRuleRootCauseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        Integer page = (Integer) context.getOrDefault(FacilioConstants.ContextNames.PAGE, 1);
        Integer perPage = (Integer) context.getOrDefault(FacilioConstants.ContextNames.PER_PAGE, 50);

        Integer offset = ((page - 1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        Long count = getRcaRuleCount(ruleId);
        List<Long> rcaRuleIds = getRcaRuleIds(ruleId, offset, perPage);
        List<NewReadingRuleContext> ruleDetails = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(rcaRuleIds)) {
            ruleDetails.addAll(NewReadingRuleAPI.getReadingRules(rcaRuleIds));
        }
        List<Map<String, Object>> rcaRuleDetails = new ArrayList<>();

        for (NewReadingRuleContext ruleDetail : ruleDetails) {
            Map<String, Object> prop = new HashMap<>();

            prop.put("id", ruleDetail.getAlarmDetails().getRuleId());
            prop.put("ruleName", ruleDetail.getName());
            prop.put("category", ruleDetail.getAssetCategory().getDisplayName());
            prop.put("severity", ruleDetail.getAlarmDetails().getSeverity());

            if(ruleDetail.getAlarmDetails().getFaultTypeEnum() != null){
                prop.put("faultType", ruleDetail.getAlarmDetails().getFaultTypeEnum().getValue());
            }

            rcaRuleDetails.add(prop);
        }

        context.put(FacilioConstants.ReadingRules.RCA.RCA_RULE_DETAILS, rcaRuleDetails);
        context.put(FacilioConstants.ContextNames.COUNT, count);

        return false;
    }
    private Long getRcaRuleCount(Long ruleId) throws Exception {
        SelectRecordsBuilder<ReadingRuleRCAContext> builder = getRuleRcaSelectBuilder(FieldFactory.getCountField(), ruleId);
        ReadingRuleRCAContext props = builder.fetchFirst();
        return (Long) props.getData().get("count");
    }
    private List<Long> getRcaRuleIds(Long ruleId, Integer offset, Integer perPage) throws Exception {
        List<FacilioField> rcaMappingFields = FieldFactory.getReadingRuleRCAMapping();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(rcaMappingFields);
        List<FacilioField> fields = new ArrayList<>();
        fields.add(fieldMap.get("rcaRuleId"));

        SelectRecordsBuilder<ReadingRuleRCAContext> builder = getRuleRcaSelectBuilder(fields, ruleId).limit(perPage).offset(offset);
        List<ReadingRuleRCAContext> props = builder.get();
        List<Long> rcaRuleIds = props.stream().map(prop-> (Long) prop.getData().get("rcaRuleId")).collect(Collectors.toList());

        return rcaRuleIds;
    }

    private SelectRecordsBuilder<ReadingRuleRCAContext> getRuleRcaSelectBuilder(List<FacilioField> fields, Long ruleId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rcaModule = modBean.getModule(FacilioConstants.ReadingRules.RCA.RCA_MODULE);
        FacilioModule rcaMappingModule = ModuleFactory.getReadingRuleRCAMapping();
        FacilioModule readingRuleModule = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);

        SelectRecordsBuilder<ReadingRuleRCAContext> builder = new SelectRecordsBuilder<ReadingRuleRCAContext>()
                .module(rcaModule)
                .beanClass(ReadingRuleRCAContext.class)
                .select(fields)
                .innerJoin(rcaMappingModule.getTableName())
                .on(rcaModule.getTableName()+".ID = "+ rcaMappingModule.getTableName() +".RCA_ID")
                .innerJoin(readingRuleModule.getTableName())
                .on(rcaMappingModule.getTableName()+".RCA_RULE_ID = "+ readingRuleModule.getTableName() +".ID")
                .andCondition(CriteriaAPI.getCondition(rcaModule.getTableName()+".RULE_ID","ruleId", String.valueOf(ruleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(readingRuleModule.getTableName()+".SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));

        return builder;
    }
}
