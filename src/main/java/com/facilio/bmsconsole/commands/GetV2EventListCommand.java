package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetV2EventListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long recordId = (long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, null);
		
		List<AlarmOccurrenceContext> alarmOccurrenceList = (List<AlarmOccurrenceContext>) context.getOrDefault(FacilioConstants.ContextNames.RECORD_LIST, null);
        AlarmOccurrenceContext alarmOccurrence = null;
        List<Long> alarmOccurrenceIds = null;
		if (recordId > 0) {
		    alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(recordId);
        }
		if (alarmOccurrenceList!=null && alarmOccurrenceList.size() > 0) {
			alarmOccurrenceIds = alarmOccurrenceList.stream().map(prop -> (long) prop.getId()).collect(Collectors.toList());
		}
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
                .module(module)
                .select(allFields)
                .beanClass(BaseEventContext.class);

        if (alarmOccurrence != null) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarmOccurrence"),
                    String.valueOf(recordId), NumberOperators.EQUALS));
        }
        
        if (alarmOccurrenceIds != null && alarmOccurrenceIds.size() > 0) {
        	builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("alarmOccurrence"), 
        			StringUtils.join(alarmOccurrenceIds, ","), NumberOperators.EQUALS));
        }

        if (fieldMap.containsKey("severity")) {
            builder.fetchSupplement((LookupField) fieldMap.get("severity"));
        }
        List<BaseEventContext> list = builder.get();

        context.put(FacilioConstants.ContextNames.RECORD_LIST, list);

		return false;
	}

}
