package com.facilio.readingrule.command;

import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
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
                Map<String, Object> resourcesWithCount = NewReadingRuleAPI.getMatchedResourcesWithCount(rule);
                rule.setAssets((List<Long>) resourcesWithCount.get("resourceIds"));
                if(CollectionUtils.isNotEmpty(namespaceContext.getIncludedAssetIds())) {
                    rule.setAssets(namespaceContext.getIncludedAssetIds());
                }
                rule.setModuleName(FacilioConstants.ReadingRules.NEW_READING_RULE);
                rule.setAlarmRCARules(NewReadingRuleAPI.getRCARulesForReadingRule(rule.getId()));
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
}

