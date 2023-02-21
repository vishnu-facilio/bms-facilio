package com.facilio.bmsconsole.calendarview;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarViewUtil {
    public static CalendarViewContext getCalendarView(long viewId) throws Exception {
        Map<Long, CalendarViewContext> calendarViewContextMap = getAllCalendarViews(Collections.singletonList(viewId));
        return calendarViewContextMap.get(viewId);
    }

    public static Map<Long, CalendarViewContext> getAllCalendarViews(List<Long> viewIds) throws Exception {
        Map<Long, CalendarViewContext> calendarViewContextMap = new HashMap<>();

        FacilioModule calendarModule = ModuleFactory.getCalendarViewModule();
        List<FacilioField> calendarModuleFields = FieldFactory.getCalendarViewFields(calendarModule);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(calendarModule.getTableName())
                .select(calendarModuleFields)
                .andCondition(CriteriaAPI.getIdCondition(viewIds, calendarModule));

        List<Map<String, Object>> calendarViewProps = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(calendarViewProps)) {
            List<CalendarViewContext> calendarViewsList = FieldUtil.getAsBeanListFromMapList(calendarViewProps, CalendarViewContext.class);
            calendarViewContextMap = calendarViewsList.stream()
                    .collect(Collectors.toMap(CalendarViewContext::getId, Function.identity()));
            return calendarViewContextMap;
        }

        return calendarViewContextMap;
    }
}
