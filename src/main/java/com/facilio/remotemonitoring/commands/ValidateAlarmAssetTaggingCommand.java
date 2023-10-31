package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.remotemonitoring.context.AlarmAssetTaggingContext;
import com.facilio.remotemonitoring.context.AlarmTypeContext;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.signup.AlarmAssetTaggingModule;
import com.facilio.remotemonitoring.signup.AlarmTypeModule;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateAlarmAssetTaggingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmAssetTaggingContext> alarmAssetTaggings = (List<AlarmAssetTaggingContext>) recordMap.get(AlarmAssetTaggingModule.MODULE_NAME);
        if (CollectionUtils.isNotEmpty(alarmAssetTaggings)) {
            for(AlarmAssetTaggingContext alarmAssetTagging : alarmAssetTaggings) {
                if(alarmAssetTagging == null) {
                    FacilioUtil.throwIllegalArgumentException(true,"Alarm Asset Mapping is required");
                }
                if(alarmAssetTagging.getClient() == null || alarmAssetTagging.getClient().getId() < 0) {
                    FacilioUtil.throwIllegalArgumentException(true,"Client is required");
                }
                if(alarmAssetTagging.getAlarmDefinition() == null || alarmAssetTagging.getAlarmDefinition().getId() < 0) {
                    FacilioUtil.throwIllegalArgumentException(true,"Alarm Definition is required");
                }
                if(alarmAssetTagging.getController() == null || alarmAssetTagging.getController().getId() < 0) {
                    FacilioUtil.throwIllegalArgumentException(true,"Controller is required");
                }
                if(alarmAssetTagging.getAsset() == null || alarmAssetTagging.getAsset().getId() < 0) {
                    FacilioUtil.throwIllegalArgumentException(true,"Asset is required");
                }
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID","clientId",String.valueOf(alarmAssetTagging.getClient().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_ID","controllerId",String.valueOf(alarmAssetTagging.getController().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION_ID","alarmDefinitionId",String.valueOf(alarmAssetTagging.getAlarmDefinition().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("ASSET_ID","assetId",String.valueOf(alarmAssetTagging.getAsset().getId()), NumberOperators.EQUALS));
                List<AlarmAssetTaggingContext> fetchedAlarmTaggings = V3RecordAPI.getRecordsListWithSupplements(AlarmAssetTaggingModule.MODULE_NAME,null,AlarmAssetTaggingContext.class,criteria,null);
                if(CollectionUtils.isNotEmpty(fetchedAlarmTaggings)) {
                    for(AlarmAssetTaggingContext fetchedAlarmTagging : fetchedAlarmTaggings) {
                        if(fetchedAlarmTagging.getId() != alarmAssetTagging.getId()) {
                            FacilioUtil.throwIllegalArgumentException(true,"Alarm Asset Mapping already exists");
                        }
                    }
                }
            }
        }
        return false;
    }
}