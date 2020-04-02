package com.facilio.bmsconsole.commands;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.OperationAlarmContext;
import com.facilio.bmsconsole.context.OperationAlarmEventContext;
import com.facilio.bmsconsole.context.OperationAlarmHistoricalLogsContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.OperationAlarmApi;
import com.facilio.bmsconsole.util.WorkflowRuleHistoricalLogsAPI;
import com.facilio.bmsconsole.util.WorkflowRuleResourceLoggerAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class HistoricalOperationAlarmCommand extends FacilioCommand{
	private static final Logger LOGGER = LogManager.getLogger(HistoricalOperationAlarmCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		DateRange range = (DateRange) context.get(FacilioConstants.ContextNames.DATE_RANGE);
		List<Long> resourceIds = (List<Long>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		long categoryId = (long) context.get(FacilioConstants.ContextNames.CATEGORY_ID);
		System.out.println(range);
		List<Long> finalResourceIds=new ArrayList<>();
		if(categoryId > -1)
		{
			List<Long> categoryIds=new ArrayList<>();
			categoryIds.add(categoryId);
			FacilioModule module = ModuleFactory.getAssetsModule();
	        List<FacilioField> fields = new ArrayList<>();
	        fields.add(FieldFactory.getField("resourceId", "ID", FieldType.NUMBER));
	        GenericSelectRecordBuilder fieldsBuilder = new GenericSelectRecordBuilder()
	                .select(fields)
	                .table(module.getTableName())
	                .andCondition(CriteriaAPI.getCondition(FieldFactory.getField("categoryid", "CATEGORY", FieldType.NUMBER),categoryIds, NumberOperators.EQUALS));
	        List<Long> resourceIdsList = fieldsBuilder.get().stream().map(prop -> (long) prop.get("resourceId")).collect(Collectors.toList()); 
			context.put(FacilioConstants.ContextNames.RESOURCE_LIST,resourceIdsList);
			finalResourceIds=resourceIdsList;

		}
		else if(resourceIds != null) {
			context.put(FacilioConstants.ContextNames.RESOURCE_LIST,resourceIds);
			finalResourceIds=resourceIds;
		}


		int minutesInterval = (7 * 24) * 60; 								//As of now, splitting up the rule_resource job each week
		List<DateRange> intervals = DateTimeUtil.getTimeIntervals(range.getStartTime(), range.getEndTime(), minutesInterval);
		DateRange firstInterval = intervals.get(0);
		DateRange lastInterval = intervals.get(intervals.size()-1);
		List<Long> resourceParentLoggerIds = new ArrayList<Long>();
		for(Long finalResourceId: finalResourceIds)
		{
			long lastParentId = getLastParentIdForResource(finalResourceId);

			if(intervals.size() == 1 && (firstInterval.getStartTime() == lastInterval.getStartTime() && firstInterval.getEndTime() == lastInterval.getEndTime())){
				OperationAlarmHistoricalLogsContext HistoricalLogsContext = setOperationAlarmHistoricalLogsContext(firstInterval, WorkflowRuleHistoricalLogsContext.LogState.FIRST_AS_WELL_AS_LAST.getIntVal());
				HistoricalLogsContext.setResourceId(finalResourceId);
				HistoricalLogsContext.setParentId(lastParentId+1);
				addOperationAlarmHistoricalLogsContext(HistoricalLogsContext);
			}
			else
			{
				for(DateRange interval:intervals)
				{
					OperationAlarmHistoricalLogsContext HistoricalLogsContext = new OperationAlarmHistoricalLogsContext();
					if(interval.getStartTime() == firstInterval.getStartTime() && interval.getEndTime() == firstInterval.getEndTime()) {
						HistoricalLogsContext =setOperationAlarmHistoricalLogsContext(interval,WorkflowRuleHistoricalLogsContext.LogState.IS_FIRST_JOB.getIntVal());
					}
					else if(interval.getStartTime() == lastInterval.getStartTime() && interval.getEndTime() == lastInterval.getEndTime()) {
						HistoricalLogsContext =setOperationAlarmHistoricalLogsContext(interval, WorkflowRuleHistoricalLogsContext.LogState.IS_LAST_JOB.getIntVal());
					}
					else {
						HistoricalLogsContext =setOperationAlarmHistoricalLogsContext(interval, null);
					}
					HistoricalLogsContext.setResourceId(finalResourceId);
					HistoricalLogsContext.setParentId(lastParentId+1);
					addOperationAlarmHistoricalLogsContext(HistoricalLogsContext);
				}
			}

			for(OperationAlarmHistoricalLogsContext listItem: getOperationAlarmHistoricalLogsByResourceId(finalResourceId,lastParentId+1))
			{
				FacilioTimer.scheduleOneTimeJobWithDelay(listItem.getId(), "HistoricalOperationAlarmEventJob", 30, "history");
			}
			System.out.println(finalResourceId);
		}	
		
		// FacilioTimer.scheduleOneTimeJobWithDelay(1, "HistoricalOperationAlarmOccurrenceDeletionJob", 5, "history");
		return false;
	}
	public static OperationAlarmHistoricalLogsContext setOperationAlarmHistoricalLogsContext(DateRange splitRange, Integer logState)
	{
		OperationAlarmHistoricalLogsContext HistoricalLogsContext = new OperationAlarmHistoricalLogsContext();
		HistoricalLogsContext.setStatus(WorkflowRuleHistoricalLogsContext.Status.IN_PROGRESS.getIntVal());
		HistoricalLogsContext.setSplitStartTime(splitRange.getStartTime());
		HistoricalLogsContext.setSplitEndTime(splitRange.getEndTime());
		if(logState != null)
		{
			HistoricalLogsContext.setLogState(logState);
		}
		return HistoricalLogsContext;	
	}
	public static void addOperationAlarmHistoricalLogsContext(OperationAlarmHistoricalLogsContext HistoricalLogsContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
				.fields(FieldFactory.getOperationAlarmHistoricalLogFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(HistoricalLogsContext);
		insertBuilder.addRecord(props);
	
		insertBuilder.save();
		
		HistoricalLogsContext.setId((Long) props.get("id"));
	
	}
	public static List<OperationAlarmHistoricalLogsContext> getOperationAlarmHistoricalLogsByResourceId(long resourceId,long parentid) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
				.select(FieldFactory.getOperationAlarmHistoricalLogFields())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getField("resourceid", "RESOURCE_ID", FieldType.NUMBER), "" +resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getField("parentid", "PARENT_ID", FieldType.NUMBER), "" +parentid, NumberOperators.EQUALS));

		
		List<Map<String, Object>> props = selectBuilder.get();
		List<OperationAlarmHistoricalLogsContext> HistoricalLogsContextList = new ArrayList<OperationAlarmHistoricalLogsContext>();
		if (props != null && !props.isEmpty()) {		
			HistoricalLogsContextList  = FieldUtil.getAsBeanListFromMapList(props, OperationAlarmHistoricalLogsContext.class);		
		}	
		return HistoricalLogsContextList;
	}
	public static long getLastParentIdForResource(long resourceid) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOperationAlarmHistoricalLogsModule().getTableName())
				.select(FieldFactory.getOperationAlarmHistoricalLogFields())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getField("resourceid", "RESOURCE_ID", FieldType.NUMBER), "" +resourceid, NumberOperators.EQUALS))
				.orderBy("PARENT_ID DESC LIMIT 1");

		
		List<Map<String, Object>> props = selectBuilder.get();
		System.out.println(props);
		long lastParentId = -1;
		if(props.size() > 0)
		{
			lastParentId=(long) props.get(0).get("parentId");
		}
		return lastParentId;
	}

}
	