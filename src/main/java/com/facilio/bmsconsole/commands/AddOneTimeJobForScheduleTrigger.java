package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.AddOneTimeJobForScheduleTriggerHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.ScheduleTriggerRecordRelationContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class AddOneTimeJobForScheduleTrigger extends FacilioCommand {

    private ScheduleTriggerRecordRelationContext.EventType eventType;

    public AddOneTimeJobForScheduleTrigger(ScheduleTriggerRecordRelationContext.EventType eventType){
        this.eventType = eventType;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {

        try {

            if (context == null){
                return false;
            }

            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            if (moduleName == null) {
                LOGGER.debug("Module Name is empty for addOneTimeJobForScheduledRuleOnUpdate");
                return false;
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if (module == null) {
                LOGGER.debug("Module not found for addOneTimeJobForScheduledRuleOnUpdate");
                return false;
            }

            List<? extends ModuleBaseWithCustomFields> records = Constants.getRecordList((FacilioContext) context);

            if(CollectionUtils.isEmpty(records)){
                return false;
            }

            if (eventType == ScheduleTriggerRecordRelationContext.EventType.DELETE) {
                List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
                deleteScheduledJobsForTrigger(module.getModuleId(), recordIds);
                return false;
            }

            List<TriggerFieldRelContext> triggers = TriggerUtil.getTriggersForEventTypeAndModule(module.getModuleId(), EventType.SCHEDULED, TriggerFieldRelContext.class);


            if (CollectionUtils.isEmpty(triggers)) {
                return false;
            }

            for (ModuleBaseWithCustomFields record : records) {
                List<Long> filteredTriggerIds = new ArrayList<>();

                Map<String, Object> finalRecordObject = new HashMap<>();
                finalRecordObject.put("id", record.getId());

                for (TriggerFieldRelContext trigger : triggers) {
                    long dateFieldId = trigger.getFieldId();
                    FacilioField dateField = modBean.getField(dateFieldId);
                    Long dateFieldValue = (Long) FieldUtil.getValue(record, dateField);
                    filteredTriggerIds.add(trigger.getId());
                    finalRecordObject.put(dateField.getName(), dateFieldValue);
                }

                if (CollectionUtils.isNotEmpty(filteredTriggerIds)) {
                    JSONObject content = new JSONObject();
                    content.put("moduleName", module.getName());
                    content.put("record", finalRecordObject);
                    content.put("triggerIds", filteredTriggerIds);
                    content.put("eventType", eventType.getName());

                    Messenger.getMessenger().sendMessage(new Message()
                            .setKey(AddOneTimeJobForScheduleTriggerHandler.KEY+"/"+ module.getModuleId() + "/" + record.getId())
                            .setContent(content));
                }
            }
        }catch (Exception e){
            LOGGER.error("Exception at AddOneTimeJobForScheduleTrigger : ",e);
        }
        return false;
    }

    public static void deleteScheduledJobsForTrigger(Long moduleId, List<Long> recordIds) throws Exception {

        if(recordIds == null){
            return;
        }

        List<ScheduleTriggerRecordRelationContext> triggerRecordRelList = TriggerUtil.getScheduleTriggerRecordRel(recordIds,moduleId);

        if (triggerRecordRelList == null || triggerRecordRelList.isEmpty()) {
            return;
        }

        int rowsDeleted = TriggerUtil.deleteTriggerRecordRelationshipTable(triggerRecordRelList);
        LOGGER.info("Deleted job count" + rowsDeleted);
    }
}
