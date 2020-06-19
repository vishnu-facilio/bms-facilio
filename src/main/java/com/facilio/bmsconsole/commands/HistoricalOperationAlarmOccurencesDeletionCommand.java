package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.OperationAlarmContext;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext.Type;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;

public class HistoricalOperationAlarmOccurencesDeletionCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		long startTime = range.getStartTime();
		long endTime= range.getEndTime();
		// int[] coverageType = {1, 2};

		// for (Integer type: coverageType) {
			List<AlarmOccurrenceContext> alarmOccurrenceList = getAllAlarmOccurrences(startTime, endTime, resourceIds, AlarmOccurrenceContext.Type.OPERATION_OCCURRENCE , null);
			if (alarmOccurrenceList != null && !alarmOccurrenceList.isEmpty())
			{
				AlarmOccurrenceContext lastAlarmOccurrence = alarmOccurrenceList.get(alarmOccurrenceList.size() - 1);
				BaseAlarmContext baseAlarm =  lastAlarmOccurrence.getAlarm();
				BaseAlarmContext baseAlarmContext = NewAlarmAPI.getBaseAlarmById(baseAlarm.getId());

				if (alarmOccurrenceList.get(0).getCreatedTime() < startTime)
				{
					AlarmOccurrenceContext initialEdgeAlarmOccurrence = alarmOccurrenceList.get(0);
					clearAlarmOccurrenceIdForEdgeEvents(initialEdgeAlarmOccurrence, initialEdgeAlarmOccurrence.getCreatedTime(), startTime-1000);
					startTime = initialEdgeAlarmOccurrence.getCreatedTime();
				}
				if (lastAlarmOccurrence.getClearedTime() == -1 || lastAlarmOccurrence.getClearedTime() > endTime)
				{
					BaseEventContext finalEvent = getFinalEventForAlarmOccurrence(lastAlarmOccurrence);
					if(finalEvent != null && finalEvent.getCreatedTime() > endTime) {
						clearAlarmOccurrenceIdForEdgeEvents(lastAlarmOccurrence, endTime+1000, finalEvent.getCreatedTime()); //avoid event deletion
						endTime = finalEvent.getCreatedTime();
					}
				}

				deleteAllAlarmOccurences(baseAlarmContext, startTime, endTime);
				NewAlarmAPI.updateBaseAlarmWithNoOfOccurences(baseAlarm);
				NewAlarmAPI.changeLatestAlarmOccurrence(baseAlarmContext);
			}

		//}

		context.put(FacilioConstants.ContextNames.DATE_RANGE,new DateRange(startTime, endTime));
		return false;	
	}	
	
	public static void clearAlarmOccurrenceIdForEdgeEvents(AlarmOccurrenceContext edgeAlarmOccurrence, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		List<FacilioField> allEventFields = modBean.getAllFields(eventModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allEventFields);
		
		List<FacilioField> eventFields = new ArrayList<FacilioField>();
		eventFields.add(fieldMap.get("baseAlarm"));	
		eventFields.add(fieldMap.get("alarmOccurrence"));
		
		UpdateRecordBuilder<BaseEventContext> updateBuilder = new UpdateRecordBuilder<BaseEventContext>()
				.module(eventModule)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +edgeAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.fields(eventFields);
		
		BaseAlarmContext baseAlarmNull = new BaseAlarmContext();
		baseAlarmNull.setId(-99L);
		
		AlarmOccurrenceContext alarmOccurrenceContextNull = new AlarmOccurrenceContext();
		alarmOccurrenceContextNull.setId(-99L);
		
		Map<String, Object> map = new HashMap<>();
		map.put("baseAlarm", FieldUtil.getAsProperties(baseAlarmNull));
		map.put("alarmOccurrence", FieldUtil.getAsProperties(alarmOccurrenceContextNull));
		updateBuilder.updateViaMap(map);
	}

	public static BaseEventContext getFinalEventForAlarmOccurrence(AlarmOccurrenceContext finalEdgeCaseAlarmOccurrence) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule eventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
		
		SelectRecordsBuilder<BaseEventContext> selectEventbuilder = new SelectRecordsBuilder<BaseEventContext>()
				.select(modBean.getAllFields(eventModule.getName()))
				.module(modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT))
				.beanClass(BaseEventContext.class)
				.andCondition(CriteriaAPI.getCondition("ALARM_OCCURRENCE_ID", "alarmOccurrence","" +finalEdgeCaseAlarmOccurrence.getId(), NumberOperators.EQUALS))
				.orderBy("CREATED_TIME DESC").limit(1);
		
		return selectEventbuilder.fetchFirst();
	}
	
	public static void deleteAllAlarmOccurences(BaseAlarmContext baseAlarmContext, long startTime, long endTime) throws Exception {
	
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
	
		DeleteRecordBuilder<AlarmOccurrenceContext> deleteBuilder = new DeleteRecordBuilder<AlarmOccurrenceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition("ALARM_ID", "alarm", "" +baseAlarmContext.getId(), NumberOperators.EQUALS));
	
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		
		deleteBuilder.andCriteria(criteria);
		deleteBuilder.delete();			
	}
	private List<AlarmOccurrenceContext> getAllAlarmOccurrences(long startTime,long endTime,List<Long> resourceIds,Type type, Integer coverageTye) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(NewAlarmAPI.getOccurrenceModuleName(type));
		List<FacilioField> allFields = modBean.getAllFields(module.getName());

		SelectRecordsBuilder<AlarmOccurrenceContext> selectbuilder = new SelectRecordsBuilder<AlarmOccurrenceContext>()
				.select(allFields)
				.beanClass(NewAlarmAPI.getOccurrenceClass(type)).moduleName(NewAlarmAPI.getOccurrenceModuleName(type));
				if (resourceIds != null ) {
					selectbuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getField("resourceid", "RESOURCE_ID", FieldType.NUMBER), resourceIds, NumberOperators.EQUALS))
					;
				}
				if (coverageTye != null) {
					selectbuilder.andCondition(CriteriaAPI.getCondition("COVERAGE_TYPE", "coverageType", "" +coverageTye, NumberOperators.EQUALS));
				}

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "" + endTime, NumberOperators.LESS_THAN_EQUAL));

		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "" + startTime, NumberOperators.GREATER_THAN_EQUAL));
		subCriteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", "", CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);

		selectbuilder.andCriteria(criteria);
		selectbuilder.orderBy("CREATED_TIME");

		List<AlarmOccurrenceContext> alarmOccurrenceList = selectbuilder.get();
		return alarmOccurrenceList;
	}
	}

