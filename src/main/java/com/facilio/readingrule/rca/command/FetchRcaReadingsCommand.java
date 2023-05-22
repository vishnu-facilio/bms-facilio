package com.facilio.readingrule.rca.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.RCAScoreReadingContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FetchRcaReadingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long alarmId = (Long) context.get(FacilioConstants.ContextNames.ID);
        Integer page = (Integer) context.get(FacilioConstants.ContextNames.PAGE);
        Integer perPage = (Integer) context.get(FacilioConstants.ContextNames.PER_PAGE);
        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        DateRange dateRange = ReadingRuleRcaAPI.getDateRange(context);

        List<RCAGroupContext> rcaGroups = ReadingRuleRcaAPI.getRcaGroupsForAlarm(alarmId);
        List<RCAScoreReadingContext> readings;
        if (CollectionUtils.isNotEmpty(rcaGroups)) {
            if (perPage != -1) {
                Integer offset = ((page - 1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }
                readings = ReadingRuleRcaAPI.getRcaReadingsForAlarm(alarmId, dateRange, offset, perPage, filterCriteria);
            } else {
                readings = ReadingRuleRcaAPI.getRcaReadingsForAlarm(alarmId, dateRange);
            }

            if (CollectionUtils.isNotEmpty(readings)) {
                for (RCAScoreReadingContext reading : readings) {
                    setParentFault(reading);
                    setRcaRule(reading);
                    setRcaFault(reading);
                }

                Long count = ReadingRuleRcaAPI.getRcaReadingsCount(alarmId, filterCriteria, dateRange);
                context.put(FacilioConstants.ContextNames.COUNT, count);
            }
        } else {  // if there are no groups configured, then the mapped ruleIds are fetched and populated(without score)
            readings = setReadingContextWithRuleIds(alarmId, ReadingRuleRcaAPI.getRcaMappingForAlarm(alarmId));
            for (RCAScoreReadingContext reading : readings) {
                setRcaRule(reading);
            }
        }
        context.put(FacilioConstants.ReadingRules.RCA.RCA_SCORE_READINGS, readings);
        return false;
    }

    private void setParentFault(RCAScoreReadingContext reading) throws Exception {
        ReadingAlarm parentAlarm = (ReadingAlarm) NewAlarmAPI.getAlarm(reading.getParentId());
        reading.setParentAlarm(parentAlarm);
    }

    private void setRcaFault(RCAScoreReadingContext reading) throws Exception {
        ReadingAlarm rcaFault = (ReadingAlarm) NewAlarmAPI.getAlarm(reading.getRcaFaultId());
        setAssetCategory(rcaFault);
        reading.setRcaFault(rcaFault);
    }

    private void setAssetCategory(ReadingAlarm alarm) throws Exception {
        Long assetCategoryId = alarm.getReadingAlarmAssetCategory().getId();
        AssetCategoryContext assetCategoryContext = AssetsAPI.getCategoryForAsset(assetCategoryId);
        alarm.setReadingAlarmAssetCategory(assetCategoryContext);
    }

    private void setRcaRule(RCAScoreReadingContext reading) throws Exception {
        Long ruleId = reading.getRcaRuleId();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE);

        SelectRecordsBuilder<NewReadingRuleContext> selectRecordsBuilder = new SelectRecordsBuilder<NewReadingRuleContext>()
                .module(module)
                .beanClass(NewReadingRuleContext.class)
                .select(modBean.getAllFields(FacilioConstants.ReadingRules.NEW_READING_RULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), Collections.singleton(ruleId), NumberOperators.EQUALS));
        List<NewReadingRuleContext> rules = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(rules)) {
            reading.setRcaRule(rules.get(0));
        }
    }

    /**
     * @return a readingContext only for those rules (in the map) that have an associated fault
     */
    private List<RCAScoreReadingContext> setReadingContextWithRuleIds(Long parentAlarmId, List<Long> rcaRuleIds) throws Exception {
        List<RCAScoreReadingContext> rcaScoreReadingContexts = new ArrayList<>();
        ReadingAlarm parentAlarm = (ReadingAlarm) NewAlarmAPI.getAlarm(parentAlarmId);

        if (CollectionUtils.isNotEmpty(rcaRuleIds)) {
            List<ReadingAlarm> rcaFaults = ReadingRuleRcaAPI.getRCAReadingAlarms(rcaRuleIds, parentAlarm.getResource().getId(), null);
            if (CollectionUtils.isNotEmpty(rcaFaults)) {
                for(Long rcaRuleId: rcaRuleIds){
                    List<ReadingAlarm> rcaFaultsFiltered = rcaFaults.stream().filter(fault -> fault.getRule() != null && fault.getRule().getId() == rcaRuleId).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(rcaFaultsFiltered)){
                        ReadingAlarm rcaFault = rcaFaultsFiltered.get(0);
                        RCAScoreReadingContext readingContext = new RCAScoreReadingContext();
                        NewReadingRuleContext rule = new NewReadingRuleContext();
                        rule.setId(rcaRuleId);
                        readingContext.setRcaRule(rule);
                        readingContext.setParentAlarm(parentAlarm);
                        setAssetCategory(rcaFault);
                        readingContext.setRcaFault(rcaFault);
                        rcaScoreReadingContexts.add(readingContext);
                    }
                }
            }
            return rcaScoreReadingContexts;
        }
        return new ArrayList<>();
    }


}
