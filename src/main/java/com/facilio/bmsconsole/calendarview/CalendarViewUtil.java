package com.facilio.bmsconsole.calendarview;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
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

    public static Criteria calendarTimeCriteria(FacilioField startTimeField, FacilioField endTimeField, CalendarViewRequestContext calendarViewRequest) {
        Criteria mainCriteria = new Criteria();

        // Records with StartTimeField between Request (StartTime and EndTime) or EndTimeField between Request (StartTime and EndTime)
        Criteria timeCriteria = new Criteria();
        timeCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, calendarViewRequest.getDateValue(), DateOperators.BETWEEN));
        if (endTimeField != null) {
            timeCriteria.addOrCondition(CriteriaAPI.getCondition(endTimeField, calendarViewRequest.getDateValue(), DateOperators.BETWEEN));
        }

        // Records that start before our timeFrame and ends after our timeFrame
        if (endTimeField != null) {
            Criteria rollOverCriteria = new Criteria();
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(startTimeField, String.valueOf(calendarViewRequest.getStartTime()), NumberOperators.LESS_THAN));
            rollOverCriteria.addAndCondition(CriteriaAPI.getCondition(endTimeField, String.valueOf(calendarViewRequest.getEndTime()), NumberOperators.GREATER_THAN));
            timeCriteria.orCriteria(rollOverCriteria);
        }

        mainCriteria.andCriteria(timeCriteria);

        return mainCriteria;
    }

    public static SelectRecordsBuilder<ModuleBaseWithCustomFields> getSelectRecordsBuilder(Context context, ModuleBean moduleBean, boolean fetchSupplements) throws Exception{
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        FacilioModule module = ChainUtil.getModule(moduleName);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.module(module).beanClass(beanClass);

        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.SELECTABLE_FIELDS);
        if(CollectionUtils.isEmpty(fields)){
            fields = moduleBean.getAllFields(module.getName());
        }
        selectRecordsBuilder.select(fields);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (fetchSupplements && CollectionUtils.isNotEmpty(supplementFields)) {
            selectRecordsBuilder.fetchSupplements(supplementFields);
        }

        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        if (filterCriteria != null) {
            selectRecordsBuilder.andCriteria(filterCriteria);
        }

        Criteria beforeFetchCriteria = (Criteria) context.get(Constants.BEFORE_FETCH_CRITERIA);
        if (beforeFetchCriteria != null) {
            selectRecordsBuilder.andCriteria(beforeFetchCriteria);
        }

        Object serverCriteria = context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        if (serverCriteria != null) {
            if (serverCriteria instanceof Criteria) {
                if (!((Criteria) serverCriteria).isEmpty()) {
                    selectRecordsBuilder.andCriteria((Criteria) serverCriteria);
                }
            } else {
                selectRecordsBuilder.andCondition((Condition) serverCriteria);
            }
        }

        Criteria clientCriteria= (Criteria) context.get(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA);
        if(clientCriteria!=null && !clientCriteria.isEmpty()){
            selectRecordsBuilder.andCriteria(clientCriteria);
        }

        FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        boolean excludeParentCriteria = (boolean) context.getOrDefault(Constants.EXCLUDE_PARENT_CRITERIA, false);
        if (!excludeParentCriteria && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
            selectRecordsBuilder.andCriteria(view.getCriteria());
        }

        boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
        if (skipModuleCriteria || view.isExcludeModuleCriteria()) {
            selectRecordsBuilder.skipModuleCriteria();
        }

        return selectRecordsBuilder;
    }

}
