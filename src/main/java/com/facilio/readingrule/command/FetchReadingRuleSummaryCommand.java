package com.facilio.readingrule.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.context.RuleAlarmDetails;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FetchReadingRuleSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NewReadingRuleContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (NewReadingRuleContext rule : list) {
                NameSpaceContext namespaceContext = NamespaceAPI.getNameSpaceByRuleId(rule.getId(), NSType.READING_RULE);
                fetchAndUpdateAlarmDetails(rule);
                rule.setNs(namespaceContext);
                if(CollectionUtils.isNotEmpty(namespaceContext.getIncludedAssetIds())) {
                    rule.setAssets(namespaceContext.getIncludedAssetIds());
                }
                rule.setModuleName(FacilioConstants.ReadingRules.NEW_READING_RULE);
                setReadingfldAndModuleName(rule);
                NewReadingRuleAPI.setCategory(rule);
            }
        }
        return false;
    }

    public static void fetchAndUpdateAlarmDetails(NewReadingRuleContext readingRule) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(FieldFactory.getRuleAlarmDetailsFields())
                .table(ModuleFactory.getRuleAlarmDetailsModule().getTableName()) //Table:New_Reading_Rule_AlarmDetails
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(readingRule.getId()), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if (props != null) {
            RuleAlarmDetails alarmDetails = FieldUtil.getAsBeanFromMap(props, RuleAlarmDetails.class);
            alarmDetails.setSeverity(AlarmAPI.getAlarmSeverity(alarmDetails.getSeverityId()).getSeverity());
            readingRule.setAlarmDetails(alarmDetails);
        }
    }

    private void setReadingfldAndModuleName(NewReadingRuleContext rule) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(!Objects.isNull(rule.getReadingFieldId())){
            rule.setReadingFieldName(modBean.getField(rule.getReadingFieldId()).getDisplayName());
        }
        if(!Objects.isNull(rule.getReadingModuleId())){
            rule.setReadingModuleName(modBean.getModule(rule.getReadingModuleId()).getDisplayName());
        }
    }
}

