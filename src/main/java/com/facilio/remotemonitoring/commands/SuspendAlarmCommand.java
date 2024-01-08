package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


public class SuspendAlarmCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long flaggedEventId = (Long) context.get(FacilioConstants.ContextNames.ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if(flaggedEvent != null) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(RawAlarmModule.FLAGGED_EVENT_FIELD_NAME,RawAlarmModule.MODULE_NAME),String.valueOf(flaggedEvent.getId()), NumberOperators.EQUALS));
            List<RawAlarmContext> alarmEvents = V3RecordAPI.getRecordsListWithSupplements(RawAlarmModule.MODULE_NAME, null, RawAlarmContext.class, criteria, null);
            List<Long> suspendAlarmMappingIds = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(alarmEvents)) {
                for(RawAlarmContext alarmEvent : alarmEvents) {
                    if(alarmEvent.getAlarmDefinition() != null && flaggedEvent.getAsset() != null) {
                        AlarmAssetTaggingContext alarmAssetMap = alarmBean.getAlarmAssetMapping(flaggedEvent.getClient().getId(),alarmEvent.getAlarmDefinition().getId(),flaggedEvent.getController().getId(),flaggedEvent.getAsset().getId());
                        if(alarmAssetMap != null) {
                            suspendAlarmMappingIds.add(alarmAssetMap.getId());
                        }
                    }
                }
            }
            FlaggedEventUtil.changeFlaggedEventStatus(flaggedEventId, FlaggedEventContext.FlaggedEventStatus.SUSPENDED);
            if(CollectionUtils.isNotEmpty(suspendAlarmMappingIds)) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put(AlarmAssetTaggingModule.SUSPEND_ALARM, true);
                V3Util.updateBulkRecords(AlarmAssetTaggingModule.MODULE_NAME, updateMap, suspendAlarmMappingIds, false);
            }
         }
        return false;
    }
}
