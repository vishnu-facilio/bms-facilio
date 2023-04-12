package com.facilio.workflows.functions;

import java.util.Collections;

import com.facilio.bmsconsole.context.TaskSectionContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioWorkOrderFunctions implements FacilioWorkflowFunctionInterface {

	
	GET_AVG_RESOLUTION_TIME(1,"getAvgResolutionTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects[0].toString()),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));
			
            return avgResolutionTimeByCategory;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_WORK_ORDERS_ON_COMPLETION_TIME(2,"getWorkOrdersByCompletionTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects[0]),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));
			
            return siteOnCompletion;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_TECHNICIANS(3,"getTopNTechnicians") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getTopNTechnicians(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
			
            return siteOnCompletion;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_PLANNED_TYPE_COUNT(4,"getTopNBuildingsWithPlannedTypeCount") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> plannedMapList = WorkOrderAPI.getTopNBuildingsWithPlannedTypeCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
			
            return plannedMapList;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TEAM_CLOSED_OPEN_COUNT(5,"getTopNTeamsWithOpenCloseCount") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> teamOpenCloseCountList = WorkOrderAPI.getTopNTeamWithOpenCloseCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		    return teamOpenCloseCountList;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_TECH_AVG_RESOLUTION_TIME(6,"getTopNTechWithAvgResolutionTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> techResolutionTimeList = WorkOrderAPI.getAvgCompletionTimeByTechnician(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		    return techResolutionTimeList;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_UNPLANNED_TYPE_COUNT(7,"getTopNBuildingsWithUnPlannedTypeCount") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> unPlannedMapList = WorkOrderAPI.getTopNBuildingsWithUnPlannedTypeCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
			
            return unPlannedMapList;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_SITE_NAME(8,"getSiteName") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			String siteName = WorkOrderAPI.getSiteName(Long.valueOf(objects[0].toString()));
			
            return siteName;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TECHNICIAN_CLOSED_OPEN_COUNT(9,"getTopNTechWithOpenCloseCount") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> techOpenCloseCountList = WorkOrderAPI.getTopNTechniciansWithOpenCloseCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
		    return techOpenCloseCountList;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_PLANNED_UNPLANNED_CLOSED_VS_TOTAL_WO_COUNT(10,"getTopNBuildingsPlannedClosedTotalWoCount") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> closedVsTotalWoCountMap = WorkOrderAPI.getTopNBuildingsWithUnPlannedPlannedClosedCount(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
			return closedVsTotalWoCountMap;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_RECURRING_WO_FOR_RESOURCE_COUNT(11,"getTopNBuildingsWithRecurringUnPlannedCountForResource") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> closedVsTotalWoCountMap = WorkOrderAPI.getTopNBuildingsWithRecurringUnPlannedCountForResource(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
			return closedVsTotalWoCountMap;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_UNPLANNED_COUNT_CATEGORIES(12,"getTopNBuildingsWithUnPlannedCountForCategories") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> assetAndSpaceCount = WorkOrderAPI.getTopNBuildingsWithUnPlannedCountForCategories(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()), Long.valueOf(objects[3].toString()));
			return assetAndSpaceCount;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_BUILDING_ONTIME_CLOSED_COUNT(13,"getTopNBuildingsWithRecurringUnPlannedCountForResource") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			List<Map<String,Object>> assetAndSpaceCount = WorkOrderAPI.getTopNBuildingsWithRecurringUnPlannedCountForResource(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
			return assetAndSpaceCount;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},

	ADD_TASK_SECTION_SCRIPT(1,"addTaskSection") {
		@Override
		public Object execute (Map < String, Object > globalParam, Object...objects) throws Exception {
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
	}


	;
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "workorder";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.WORKORDER;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioWorkOrderFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioWorkOrderFunctions> getAllFunctions() {
		return WORKORDER_FUNCTIONS;
	}
	public static FacilioWorkOrderFunctions getFacilioWorkOrderFunction(String functionName) {
		return WORKORDER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioWorkOrderFunctions> WORKORDER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioWorkOrderFunctions> initTypeMap() {
		Map<String, FacilioWorkOrderFunctions> typeMap = new HashMap<>();
		for(FacilioWorkOrderFunctions type : FacilioWorkOrderFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
				
}
