package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.WORKORDER_FUNCTION)
public class FacilioWorkOrderFunctions {
	public Object getAvgResolutionTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects[0].toString()),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));

		return avgResolutionTimeByCategory;
	}

	public Object getWorkOrdersByCompletionTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects[0]),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));

		return siteOnCompletion;
	}

	public Object getTopNTechnicians(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getTopNTechnicians(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));

		return siteOnCompletion;
	}

	public Object getTopNBuildingsWithPlannedTypeCount(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> plannedMapList = WorkOrderAPI.getTopNBuildingsWithPlannedTypeCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));

		return plannedMapList;
	}

	public Object getTopNTeamsWithOpenCloseCount(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> teamOpenCloseCountList = WorkOrderAPI.getTopNTeamWithOpenCloseCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		return teamOpenCloseCountList;
	}

	public Object getTopNTechWithAvgResolutionTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> techResolutionTimeList = WorkOrderAPI.getAvgCompletionTimeByTechnician(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		return techResolutionTimeList;
	}

	public Object getTopNBuildingsWithUnPlannedTypeCount(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> unPlannedMapList = WorkOrderAPI.getTopNBuildingsWithUnPlannedTypeCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));

		return unPlannedMapList;
	}

	public Object getSiteName(Map<String, Object> globalParam, Object... objects) throws Exception {

		String siteName = WorkOrderAPI.getSiteName(Long.valueOf(objects[0].toString()));

		return siteName;
	}

	public Object getTopNTechWithOpenCloseCount(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> techOpenCloseCountList = WorkOrderAPI.getTopNTechniciansWithOpenCloseCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		return techOpenCloseCountList;
	}

	public Object getTopNBuildingsPlannedClosedTotalWoCount(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> closedVsTotalWoCountMap = WorkOrderAPI.getTopNBuildingsWithUnPlannedPlannedClosedCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		return closedVsTotalWoCountMap;
	}

	public Object getTopNBuildingsWithRecurringUnPlannedCountForResource(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> closedVsTotalWoCountMap = WorkOrderAPI.getTopNBuildingsWithRecurringUnPlannedCountForResource(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
		return closedVsTotalWoCountMap;
	}

	public Object getTopNBuildingsWithUnPlannedCountForCategories(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> assetAndSpaceCount = WorkOrderAPI.getTopNBuildingsWithUnPlannedCountForCategories(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		return assetAndSpaceCount;
	}

	/*public Object getTopNBuildingsWithRecurringUnPlannedCountForResource(Map<String, Object> globalParam, Object... objects) throws Exception {

		List<Map<String,Object>> assetAndSpaceCount = WorkOrderAPI.getTopNBuildingsWithRecurringUnPlannedCountForResource(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
		return assetAndSpaceCount;
	}*/

	public Object addTaskSection (Map < String, Object > globalParam, Object...objects) throws Exception {
		if (objects.length != 3) {
			throw new Exception("Arguments are not Valid");
		}
		String sectionName = String.valueOf(objects[0]);
		Long parentTicket = (Long) objects[1];
		Long sequenceNumber = (Long) objects[2];
		TaskSectionContext section = new TaskSectionContext();
		section.setParentTicketId(parentTicket);
		section.setName(sectionName);
		section.setSequenceNumber(sequenceNumber);
		section.setPreRequest(Boolean.FALSE);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTaskSectionModule().getTableName())
				.fields(FieldFactory.getTaskSectionFields());
		Map<String, Object> taskSection = FieldUtil.getAsProperties(section);
		long taskSectionId = insertBuilder.insert(taskSection);

		return taskSectionId;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
