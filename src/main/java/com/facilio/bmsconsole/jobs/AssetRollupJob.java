package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.RuleRollupCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

public class AssetRollupJob extends AlarmRollupJob {

    private final long HOUR_IN_MILLIS = 60 * 60 * 1000;
    private final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    @Override
    public void execute(JobContext jc) throws Exception {

        long totalExecutionTime = 0;

        Long lastOccurredTime = null;

        long lastRolledUpDate = -1;
        long nextRolledUpDate = -1;
        Map map = null;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        map = getLastRolledUpDate(ModuleFactory.getAssetRollupSummaryModule(), FieldFactory.getAssetRollupSummaryFields());
        if (MapUtils.isNotEmpty(map)) {
            if (map.containsKey("lastRolledUpDate")) {
                lastRolledUpDate = (long) map.get("lastRolledUpDate");
            }
        }
        if (lastRolledUpDate <= 0) {
            lastRolledUpDate = getLastRolledUpDateFromOccurrence();
        }

        if (lastRolledUpDate == -1) {
            // No occurrence found
            return;
        }

        while (totalExecutionTime <= TRANSACTION_TIME) {

            long startTime = System.currentTimeMillis();

            nextRolledUpDate = getNextRolledUpDate(lastRolledUpDate);

            FacilioModule alarmModule = modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(alarmModule.getName()));
            SelectRecordsBuilder<ReadingAlarm> assetBuilder = new SelectRecordsBuilder<ReadingAlarm>()
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.DISTINCT, fieldMap.get("resource"))
                    .module(alarmModule);
            if (lastRolledUpDate > 0) {
                assetBuilder.andCondition(CriteriaAPI.getCondition("LAST_OCCURRED_TIME", "lastOccurredTime",
                        String.valueOf(lastRolledUpDate), NumberOperators.GREATER_THAN));
            }
            List<Map<String, Object>> props = assetBuilder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    Long resourceId = (Long) prop.get("resource");

                    FacilioChain assetRollupChain = TransactionChainFactory.getRuleRollupChain();
                    Context assetRollupCommand = assetRollupChain.getContext();
                    assetRollupCommand.put(FacilioConstants.ContextNames.ID, resourceId);
                    assetRollupCommand.put(FacilioConstants.ContextNames.ROLL_UP_TYPE, RuleRollupCommand.RollupType.ASSET);
                    assetRollupCommand.put(FacilioConstants.ContextNames.START_TIME, lastRolledUpDate);
                    assetRollupCommand.put(FacilioConstants.ContextNames.END_TIME, nextRolledUpDate);
                    assetRollupChain.execute();
                }
            }

            totalExecutionTime += System.currentTimeMillis() - startTime;
            if (nextRolledUpDate < getPreviousDayEndTime()) {
                // get only last occurred time while running historical
                lastOccurredTime = NewAlarmAPI.getReadingAlarmLastOccurredTime();
            }
            lastRolledUpDate = updateOneTimeJob(nextRolledUpDate, lastOccurredTime);

            if (lastRolledUpDate >= getPreviousDayEndTime()) {
                break;
            }
        }

        scheduleNextJob(lastRolledUpDate, "AssetRollupJob-OneTime", jc);
        updateSummaryTable(ModuleFactory.getAssetRollupSummaryModule(), FieldFactory.getAssetRollupSummaryFields(), map, lastRolledUpDate);
    }
}
