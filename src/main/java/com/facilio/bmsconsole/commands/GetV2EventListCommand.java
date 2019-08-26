package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.events.util.EventAPI;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetV2EventListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        AlarmOccurrenceContext alarmOccurrence = null;
		if (recordId > 0) {
		    alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(recordId);
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

        if (fieldMap.containsKey("severity")) {
            builder.fetchLookup((LookupField) fieldMap.get("severity"));
        }
        List<BaseEventContext> list = builder.get();

        context.put(FacilioConstants.ContextNames.RECORD_LIST, list);

		return false;
	}

}
