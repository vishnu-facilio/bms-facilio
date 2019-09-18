package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class NewEventAPI {

	public static Class getEventClass(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return ReadingEventContext.class;

			case ML_ANOMALY_ALARM:
				return MLAnomalyEvent.class;

			case RCA_ALARM:
				return RCAEvent.class;

			case READING_RCA_ALARM:
				return ReadingRCAEvent.class;

			case BMS_ALARM:
				return BMSEventContext.class;

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static String getEventModuleName(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Invalid alarm type");
		}

		switch (type) {
			case READING_ALARM:
				return "readingevent";

			case ML_ANOMALY_ALARM:
				return "mlanomalyevent";

			case RCA_ALARM:
				return "RcaEvent";

			case READING_RCA_ALARM:
				return "readingrcaevent";

			case BMS_ALARM:
				return "bmsevent";

			default:
				throw new IllegalArgumentException("Invalid alarm type");
		}
	}

	public static BMSEventContext transformEvent(BMSEventContext event, JSONTemplate template, Map<String, Object> placeHolders) throws Exception {
		Map<String, Object> eventProp = FieldUtil.getAsProperties(event);
		JSONObject content = template.getTemplate(placeHolders);
		if (content != null && !content.isEmpty()) {
			content.put("severityString", content.remove("severity"));
		}
		eventProp.putAll(FieldUtil.getAsProperties(content));
		event = FieldUtil.getAsBeanFromMap(eventProp, BMSEventContext.class);
		event.setMessageKey(null);
		eventProp.put("messageKey", event.getMessageKey()); //Setting the new key in case if it's updated
		CommonCommandUtil.appendModuleNameInKey(null, "event", eventProp, placeHolders);//Updating the placeholders with the new event props
		return event;
	}

	public static BaseEventContext getEvent(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		BaseEventContext baseEventContext = builder.fetchFirst();
		if (baseEventContext != null) {
			List<BaseEventContext> list = getExtendedEvent(Collections.singletonList(baseEventContext));
			if (CollectionUtils.isNotEmpty(list)) {
				return list.get(0);
			}
		}
		return null;
	}

	public static List<BaseEventContext> getExtendedEvent(List<BaseEventContext> baseEventContexts) throws Exception {
		if (CollectionUtils.isEmpty(baseEventContexts)) {
			return new ArrayList<>();
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		Map<Type, List<Long>> map = new HashMap<>();
		for (BaseEventContext event : baseEventContexts) {
			List<Long> list = map.get(event.getEventTypeEnum());
			if (list == null) {
				list = new ArrayList<>();
				map.put(event.getEventTypeEnum(), list);
			}
			list.add(event.getId());
		}

		List<BaseEventContext> newList = new ArrayList<>();
		for (Type type : map.keySet()) {
			if (CollectionUtils.isNotEmpty(map.get(type))) {
				continue;
			}
			String moduleName = NewEventAPI.getEventModuleName(type);
			FacilioModule module = modBean.getModule(moduleName);

			SelectRecordsBuilder<BaseEventContext> builder = new SelectRecordsBuilder<BaseEventContext>()
					.module(module)
					.select(modBean.getAllFields(module.getName()))
					.beanClass(NewEventAPI.getEventClass(type))
					.andCondition(CriteriaAPI.getIdCondition(map.get(type), module));
			newList.addAll(builder.get());
		}
		return newList;
	}
}
