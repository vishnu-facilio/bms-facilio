package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.remotemonitoring.context.AlarmDefinitionTaggingContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateAlarmDefinitionTaggingModule extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmDefinitionTaggingContext> alarmDefinitionTaggings = (List<AlarmDefinitionTaggingContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmDefinitionTaggings)) {
            for(AlarmDefinitionTaggingContext alarmDefinitionTagging : alarmDefinitionTaggings) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("ALARM_DEFINITION","alarmDefinition",String.valueOf(alarmDefinitionTagging.getAlarmDefinition().getId()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("CONTROLLER_TYPE","controllerType",String.valueOf(alarmDefinitionTagging.getControllerType()), NumberOperators.EQUALS));
                criteria.addAndCondition(CriteriaAPI.getCondition("CLIENT_ID","client",String.valueOf(alarmDefinitionTagging.getClient().getId()), NumberOperators.EQUALS));
                List<AlarmDefinitionTaggingContext> existingRecords = V3RecordAPI.getRecordsListWithSupplements(moduleName,null,AlarmDefinitionTaggingContext.class,criteria,null);
                if(CollectionUtils.isNotEmpty(existingRecords)) {
                    if(alarmDefinitionTagging.getId() > 0) {
                        List<AlarmDefinitionTaggingContext> filteredTaggings = existingRecords.stream().filter(a -> a.getId() != alarmDefinitionTagging.getId()).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(filteredTaggings)) {
                            throw new IllegalArgumentException("Alarm definition tagging already exists");
                        }
                    } else {
                        throw new IllegalArgumentException("Alarm definition tagging already exists");
                    }
                }
            }
        }
        return false;
    }
}
