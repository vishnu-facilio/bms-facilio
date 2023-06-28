package com.facilio.ims.handler;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import lombok.extern.log4j.Log4j;

import java.util.*;

@Log4j
public class AlarmDeleteHandler extends ImsHandler {

    public static final String KEY = "alarm-delete";

    @Override
    public void processMessage(Message message) {
        try {
           Map<String,Object> messageContent = (Map<String, Object>) message.getContent();
           Long alarmId = Long.parseLong(messageContent.get("alarmId").toString());
           AccountUtil.setCurrentAccount(message.getOrgId());
           Map<String, List<Long>> idsToBeDeleted = getEventAndOccurrenceIds(alarmId);
           List<Long> eventIds =  idsToBeDeleted.get("eventIds");
           List<Long> occurrenceIds = idsToBeDeleted.get("occurrenceIds");
           ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
           int deletedEventCount = deleteRecords(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT), eventIds);
           int deletedOccurrenceCount = deleteRecords(modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE), occurrenceIds);
           int deletedAlarmCount = deleteRecords(modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM), Arrays.asList(alarmId));
           LOGGER.info(deletedEventCount+" BaseEvents deleted\n"+deletedOccurrenceCount+" AlarmOccurrence deleted\n"+deletedAlarmCount+" BaseAlarm deleted");
       }
       catch (Exception e){
           LOGGER.error("Exception while deleting alarm",e);
       }
    }

    public Map<String, List<Long>> getEventAndOccurrenceIds(Long alarmId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<BaseEventContext> selectBuilder = new SelectRecordsBuilder<BaseEventContext>()
                .beanClass(BaseEventContext.class)
                .moduleName(FacilioConstants.ContextNames.BASE_EVENT)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.BASE_EVENT))
                .andCondition(CriteriaAPI.getCondition("ALARM_ID", "baseAlarm", String.valueOf(alarmId),
                        NumberOperators.EQUALS));
        List<BaseEventContext> baseEvents = selectBuilder.get();
        Set<Long> eventIds = new HashSet<>();
        Set<Long> occurrenceIds = new HashSet<>();
        baseEvents.forEach(event -> {
            eventIds.add(event.getId());
            occurrenceIds.add(event.getAlarmOccurrence().getId());
        });
        Map<String, List<Long>> idsToBeDeleted = new HashMap<>();
        idsToBeDeleted.put("eventIds", new ArrayList<>(eventIds));
        idsToBeDeleted.put("occurrenceIds", new ArrayList<>(occurrenceIds));
        return idsToBeDeleted;
    }
    public int deleteRecords(FacilioModule module, List<Long> ids) throws Exception {
        DeleteRecordBuilder builder = new DeleteRecordBuilder();
        int deletedCount = builder.module(module).batchDeleteById(ids);
        return deletedCount;
    }
}