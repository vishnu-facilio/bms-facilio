package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketStatusContext.StatusType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class WorkOrderAPI {
	
	private static final Logger LOGGER = Logger.getLogger(WorkOrderAPI.class.getName());
	
	public static List<Map<String, Object>> getTasks(Long workorderID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.ID, workorderID);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
		context.put("isAsMap", true);
		chain.execute(context);
		
		return (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
	}
	public static WorkOrderContext getWorkOrder(long ticketId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getIdCondition(ticketId, module))
														;
		
		List<WorkOrderContext> workOrders = builder.get();
		if(workOrders != null && !workOrders.isEmpty()) {
			return workOrders.get(0);
		}
		return null;
	}
	public static List<WorkOrderContext> getWorkOrderFromPMId(long pmid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("PM_ID", "pmId", pmid+"", NumberOperators.EQUALS))
														;
		
		return builder.get();
	}
	
	public static List<WorkOrderContext> getWorkOrders(long categoryId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", categoryId+"", NumberOperators.EQUALS))
														;
		
		List<WorkOrderContext> workOrders = builder.get();
		return workOrders;
	}
	
	public static List<WorkOrderContext> getWorkOrders(long categoryId,Long startTime,Long endTime,Long spaceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", categoryId+"", NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
														;
		if(spaceId != null && spaceId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resource", spaceId+"", BuildingOperator.BUILDING_IS));
		}
		List<WorkOrderContext> workOrders = builder.get();
		LOGGER.log(Level.SEVERE, "builder1 - "+builder);
		return workOrders;
	}

	public static List<WorkOrderContext> getOpenWorkOrderForUser(Long ouid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		TicketStatusContext status = TicketAPI.getStatus("Closed");
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.table("WorkOrders")
														.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getCondition("STATUS_ID", "status", status.getId()+"", NumberOperators.NOT_EQUALS))
														.andCondition(CriteriaAPI.getCondition("ASSIGNED_TO_ID", "assignedTo", ouid+"", NumberOperators.EQUALS));
		
		List<WorkOrderContext> workOrders = builder.get();

		return workOrders;
	}
	
	public static Map<Long, Object> getLookupFieldPrimary(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioField mainField = modBean.getPrimaryField(moduleName);

		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(mainField);
		selectFields.add(FieldFactory.getIdField(module));
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		FacilioModule prevModule = module;
		while (prevModule.getExtendModule() != null) {
			builder.innerJoin(prevModule.getExtendModule().getTableName())
				.on(prevModule.getTableName()+".ID = " + prevModule.getExtendModule().getTableName()+ ".ID");
			prevModule = prevModule.getExtendModule();
		}

		List<Map<String,Object>> asProps = builder.get();
		Map lookupMap = new HashMap<>();
		for (Map<String, Object> map : asProps) {
			lookupMap.put((Long) map.get("id"), map.get(mainField.getName()));
		}
		return lookupMap;
	}


	public static List<WorkOrderContext> getOverdueWorkOrders(List<WorkOrderContext> workOrders) throws Exception {
		List<WorkOrderContext> overdueWorkorders =null;
		
		for(WorkOrderContext workOrder:workOrders) {
			
			long estimatedEnd = workOrder.getEstimatedEnd();
			if(estimatedEnd != -1) {
				if(estimatedEnd - DateTimeUtil.getCurrenTime() < 0) {
					
					overdueWorkorders = overdueWorkorders == null ? new ArrayList<>() : overdueWorkorders;
					
					overdueWorkorders.add(workOrder);
				}
			}
		}
		
		return overdueWorkorders;
	}
	
	
	public static List<WorkOrderContext> getDueTodayWorkOrders(List<WorkOrderContext> workOrders) throws Exception {
		
		List<WorkOrderContext> dueTodayWorkorders =null;
		
		for(WorkOrderContext workOrder:workOrders) {
			
			long estimatedEnd = workOrder.getEstimatedEnd();
			if(estimatedEnd != -1) {
				
				DateRange dateRange = DateOperators.TODAY.getRange(null);
				
				if (dateRange.getStartTime() < estimatedEnd && dateRange.getEndTime() > estimatedEnd) {
					
					dueTodayWorkorders = dueTodayWorkorders == null ? new ArrayList<>() : dueTodayWorkorders;
					
					dueTodayWorkorders.add(workOrder);
				}
			}
		}
		
		return dueTodayWorkorders;
	}
	
	
public static Map<Long, Map<String, Object>> getWorkOrderStatusPercentage(Long startTime,Long endTime) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		  .module(workOrderModule)
																		  .beanClass(WorkOrderContext.class)
																		  .select(workorderFields)
																		  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																		  .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																		  
																		  ;
  


		List<WorkOrderContext> workOrderStatusCount = selectRecordsBuilder.get();
        TicketAPI.loadTicketLookups(workOrderStatusCount);
		
	  	Map<Long, Object> siteArray = WorkOrderAPI.getLookupFieldPrimary("site");

		
		Map<Long, Map<String, Object>> resp = new HashMap<Long, Map<String, Object>>();
		for (int i = 0; i < workOrderStatusCount.size(); i++) {
			WorkOrderContext mp = workOrderStatusCount.get(i);
			String siteName = (String) siteArray.get(mp.getSiteId());
			TicketStatusContext statusMap = mp.getStatus();
			Long siteId = mp.getSiteId();
			Long dueDate = mp.getDueDate();
			Long actualWorkEnd = mp.getActualWorkEnd();
			
			
			Map<String,Object> siteInfo = null;
			
			boolean overDue = false;
			boolean onTime = false;
			boolean open = false;
		    if(statusMap.getType() == StatusType.CLOSED)
            {
            	if(dueDate != null && actualWorkEnd != null) {
            	if(actualWorkEnd <= dueDate)
            	{
            		onTime = true;
            	}
            	else
            	{
            		overDue = true;
            	}
            	}
            }
            else
            {
            	open = true;
            }
		
			if (resp.containsKey(siteId)) {
                siteInfo = resp.get(siteId);
               if(overDue)
               {
               Integer overDueCount = (Integer)siteInfo.get("overDue");
               overDueCount = overDueCount + 1;
               siteInfo.put("overDue",overDueCount);
               }
               else if(onTime)
               {
            	   Integer onTimeCount = (Integer)siteInfo.get("onTime");
                   onTimeCount = onTimeCount + 1;
                   siteInfo.put("onTime",onTimeCount);
                 
               }
               else if(open)
               {
            	   Integer openCount = (Integer)siteInfo.get("open");
            	   openCount = openCount + 1;
                   siteInfo.put("open",openCount);
                 
               }
               Integer totalCount = (Integer)siteInfo.get("totalCount");
          	   siteInfo.put("totalCount",totalCount+1);  
     		}

			else {
				
				siteInfo = new HashMap<String, Object>();
				siteInfo.put("open",open?1:0);
				siteInfo.put("name",siteName);
				siteInfo.put("overDue",overDue?1:0);
				siteInfo.put("onTime",onTime?1:0);
				siteInfo.put("siteId",siteId);
				siteInfo.put("totalCount",1);
				
			}
			  resp.put(siteId, siteInfo);
			
	      	
		
		}
		


       return resp;
	}


public static Map<Long,Object> getTechnicianCountBySite() throws Exception{
	
	
	FacilioModule groupsModule = AccountConstants.getGroupModule();
	FacilioModule groupMembersModule = AccountConstants.getGroupMemberModule();
	FacilioModule rolesModule = AccountConstants.getRoleModule();
	FacilioModule orgUserModule = AccountConstants.getOrgUserModule();
	
	List<FacilioField> fields = new ArrayList<FacilioField>();
	
	FacilioField countField = new FacilioField();
	countField.setName("count");
	countField.setColumnName("count(DISTINCT "+groupMembersModule.getTableName()+".ORG_USERID)");
	fields.add(countField);
	
	FacilioField siteIdField = FieldFactory.getSiteIdField(groupsModule);
	fields.add(siteIdField);

	List<FacilioField> roleFields = AccountConstants.getRoleFields();
	
	GenericSelectRecordBuilder roleSelectBuilder = new GenericSelectRecordBuilder()
			.select(roleFields).table(rolesModule.getTableName())
			.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".NAME", "name" ,"Technician", StringOperators.STARTS_WITH))
			.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
		    ;
	List<Map<String, Object>> roleList = roleSelectBuilder.get();
	Long roleId = (Long)roleList.get(0).get("roleId");
	
	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(fields).table(groupsModule.getTableName())
			.innerJoin(groupMembersModule.getTableName()).on(groupsModule.getTableName()+".GROUPID = "+groupMembersModule.getTableName()+".GROUPID")
			.innerJoin(orgUserModule.getTableName()).on(groupMembersModule.getTableName()+".ORG_USERID = "+orgUserModule.getTableName()+".ORG_USERID")
			.andCondition(CriteriaAPI.getCondition(orgUserModule.getTableName()+".ROLE_ID", "roleId" ,""+roleId, NumberOperators.EQUALS))
			.andCondition(CriteriaAPI.getCondition(groupsModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
			.groupBy(siteIdField.getCompleteColumnName())
		    ;
	
	
	List<Map<String, Object>> list = selectBuilder.get();
	
	Map<Long,Object> countMap = new HashMap<Long, Object>();

	for(int i=0;i<list.size();i++)
	{
		Map<String, Object> siteTechMap = list.get(i);
		countMap.put((Long)siteTechMap.get("siteId"), siteTechMap.get("count"));
	}
	
	return countMap;

}
public static List<Map<String,Object>> getAvgCompletionTimeByCategory(Long startTime,Long endTime) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
		

		FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		List<FacilioField> ticketFields = modBean.getAllFields(workOrderModule.getName());


		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);
		Map<String, FacilioField> ticketFieldMap = FieldFactory.getAsMap(ticketFields);


		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField avgField = new FacilioField();
		avgField.setName("avg_resolution_time");
		avgField.setColumnName("avg(ACTUAL_WORK_DURATION/(60))");//to render client in mins
		fields.add(avgField);

		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("count(*)");
		fields.add(countField);

		FacilioField categoryField = workorderFieldMap.get("category");
		fields.add(categoryField);

		FacilioField statusField = ticketFieldMap.get("status");
		//fields.add(statusField);


		//fetching the workorders with closed status



		GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
				  													.table(workOrderModule.getTableName())
				  													.select(fields)
				  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
					  												.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
					  												.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
					  											    .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
					  											    .andCondition(CriteriaAPI.getCondition(categoryField, CommonOperators.IS_NOT_EMPTY))
																    .andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																	.groupBy(categoryField.getCompleteColumnName())
																  ;



       List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();


       return avgResolutionTime;

	}


public static Map<Long,Object> getAvgCompletionTimeBySite(Long startTime,Long endTime,boolean isTillLastMonth) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
	FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
	

	FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

	List<FacilioField> ticketFields = modBean.getAllFields(workOrderModule.getName());


	Map<String, FacilioField> ticketFieldMap = FieldFactory.getAsMap(ticketFields);


	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField avgField = new FacilioField();
	avgField.setName("avg_resolution_time");
	avgField.setColumnName("avg(ACTUAL_WORK_DURATION/(60))");//to render client in mins
	fields.add(avgField);

	FacilioField countField = new FacilioField();
	countField.setName("count");
	countField.setColumnName("count(*)");
	fields.add(countField);

	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);

	
	FacilioField statusField = ticketFieldMap.get("status");
	//fields.add(statusField);


	//fetching the workorders with closed status



	GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
			  													.table(workOrderModule.getTableName())
			  													.select(fields)
			  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
				  												.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
				  												.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
				  												.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																.groupBy(siteIdField.getCompleteColumnName())
															  ;


	 if(isTillLastMonth)
	   {
		 //endTime=DateTimeUtil.minusDays(startTime, 1);
		 selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", ""+startTime, DateOperators.IS_BEFORE));
				
	   }
	   else
	   {
		   selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
				
	   }

   List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();
   Map<Long,Object> countMap = new HashMap<Long, Object>();
	
	
	for(int i=0;i<avgResolutionTime.size();i++)
	{
		Map<String, Object> siteTechMap = avgResolutionTime.get(i);
		countMap.put((Long)siteTechMap.get("siteId"),siteTechMap.get("avg_resolution_time"));
		
	}
	return countMap;



}


public static Map<Long,Object> getAvgResponseTimeBySite(Long startTime,Long endTime,boolean isTillLastMonth) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
	FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
	

	FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

	List<FacilioField> ticketFields = modBean.getAllFields(workOrderModule.getName());


	Map<String, FacilioField> ticketFieldMap = FieldFactory.getAsMap(ticketFields);


	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField avgField = new FacilioField();
	avgField.setName("avg_response_time");
	avgField.setColumnName("avg((ACTUAL_WORK_START-CREATED_TIME)/(60))");//to render client in mins
	fields.add(avgField);

	FacilioField countField = new FacilioField();
	countField.setName("count");
	countField.setColumnName("count(*)");
	fields.add(countField);

	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);

	
	FacilioField statusField = ticketFieldMap.get("status");
	//fields.add(statusField);


	//fetching the workorders with closed status



	GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
			  													.table(workOrderModule.getTableName())
			  													.select(fields)
			  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
				  												.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
				  												.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
				  												.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																.groupBy(siteIdField.getCompleteColumnName())
															  ;

   if(isTillLastMonth)
   {
	  // endTime=DateTimeUtil.minusDays(startTime, 1);
	   selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", ""+startTime, DateOperators.IS_BEFORE));
			
   }
   else
   {
	   selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
			
   }

   List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();
   Map<Long,Object> countMap = new HashMap<Long, Object>();
	
	
	for(int i=0;i<avgResolutionTime.size();i++)
	{
		Map<String, Object> siteTechMap = avgResolutionTime.get(i);
		countMap.put((Long)siteTechMap.get("siteId"),siteTechMap.get("avg_response_time"));
		
	}
	return countMap;



}




public static Map<Long, Object> getTotalWoCountBySite(Long startTime,Long endTime) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField idCountField = new FacilioField();
	idCountField.setColumnName("count(*)");
	idCountField.setName("count");
	fields.add(idCountField);

	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);
	
	

	SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																  .module(workOrderModule)
																  .beanClass(WorkOrderContext.class)
																  .select(fields)
																  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																  .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																  .groupBy(siteIdField.getCompleteColumnName());
			  													  ;
    List<Map<String, Object>> totalWoCountBySite = selectRecordsBuilder.getAsProps();
    
    Map<Long, Object> sites = new HashMap<Long, Object>();
    for(int i=0;i<totalWoCountBySite.size();i++)
    {
    	 
       	sites.put((Long)totalWoCountBySite.get(i).get("siteId"),totalWoCountBySite.get(i).get("count"));
       
    }

   return sites;

}

public static Map<Long,Object> getTotalClosedWoCountBySite(Long startTime,Long endTime) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField idCountField = new FacilioField();
	idCountField.setColumnName("count(*)");
	idCountField.setName("count");
	fields.add(idCountField);

	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);
	
	Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria(workOrderModule);

	SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																  .module(workOrderModule)
																  .beanClass(WorkOrderContext.class)
																  .select(fields)
																  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																  .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																  .andCriteria(closedCriteria)
																  .groupBy(siteIdField.getCompleteColumnName());
			  													  ;
   List<Map<String, Object>> totalClosedWoCountBySite = selectRecordsBuilder.getAsProps();
   Map<Long,Object> sites = new HashMap<Long,Object>();
   for(int i=0;i<totalClosedWoCountBySite.size();i++)
   {
	sites.put((Long)totalClosedWoCountBySite.get(i).get("siteId"),totalClosedWoCountBySite.get(i).get("count"));
    
   }

  return sites;

}

  public static Map<Long, Object> getTeamsCountBySite() throws Exception {

  
	    FacilioModule groupsModule = AccountConstants.getGroupModule();
		FacilioModule groupMembersModule = AccountConstants.getGroupMemberModule();
		FacilioModule rolesModule = AccountConstants.getRoleModule();
		FacilioModule orgUserModule = AccountConstants.getOrgUserModule();
		
		List<FacilioField> orgUserFields = AccountConstants.getOrgUserFields();
		Map<String,FacilioField> orgUserFieldsMap = FieldFactory.getAsMap(orgUserFields);
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("count(DISTINCT "+groupMembersModule.getTableName()+".ORG_USERID)");
		fields.add(countField);
		
		FacilioField siteIdField = FieldFactory.getSiteIdField(groupsModule);
		fields.add(siteIdField);
	
		FacilioField roleIdField = orgUserFieldsMap.get("roleId");
		fields.add(roleIdField);
	
		
		List<FacilioField> roleFields = AccountConstants.getRoleFields();
		
		GenericSelectRecordBuilder roleSelectBuilder = new GenericSelectRecordBuilder()
				.select(roleFields).table(rolesModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".NAME", "name" ,"Technician,Executive,TL", StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(rolesModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
			    ;
		List<Map<String, Object>> roleList = roleSelectBuilder.get();
		StringBuilder sb = new StringBuilder();
		
		Map<Long,Object> roleNameMap = new HashMap<Long,Object>();
		
		
		for(int i=0;i<roleList.size();i++)
		{   
			
		    roleNameMap.put((Long)roleList.get(i).get("roleId"),roleList.get(i).get("name"));
		    Long roleId = (Long)roleList.get(i).get("roleId");
			sb.append(roleId);
			if(i<roleList.size()-1)
			{
				sb.append(",");
			}
			
			
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields).table(groupsModule.getTableName())
				.innerJoin(groupMembersModule.getTableName()).on(groupsModule.getTableName()+".GROUPID = "+groupMembersModule.getTableName()+".GROUPID")
				.innerJoin(orgUserModule.getTableName()).on(groupMembersModule.getTableName()+".ORG_USERID = "+orgUserModule.getTableName()+".ORG_USERID")
				.andCondition(CriteriaAPI.getCondition(orgUserModule.getTableName()+".ROLE_ID", "roleId" ,""+sb.toString(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(groupsModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
				.groupBy(siteIdField.getCompleteColumnName()+","+roleIdField.getCompleteColumnName())
			    
			    ;
		
		List<Map<String, Object>> list = selectBuilder.get();
		
		Map<Long,Object> countMap = new HashMap<Long, Object>();
		
	
		for(int i=0;i<list.size();i++)
		{
			Map<String, Object> siteTechMap = list.get(i);
			Map<String,Object> site = null;
		    if(countMap.containsKey(siteTechMap.get("siteId"))) {
		    	site = (Map<String, Object>) countMap.get(siteTechMap.get("siteId"));
		    }
		    else
		    {
		    	site = new HashMap<String, Object>();
		    }
		 	site.put((String)roleNameMap.get(siteTechMap.get("roleId")),siteTechMap.get("count"));
	    
			countMap.put((Long)siteTechMap.get("siteId"),site);
			
		}
		return countMap;

  }
  public static List<Map<String, Object>> getTopNTechnicians(String count,long startTime,long endTime) throws Exception{
	  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
		FacilioModule orgUserModule = AccountConstants.getOrgUserModule();
		FacilioModule userModule = AccountConstants.getUserModule();
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		


		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		List<FacilioField> resourceFields = modBean.getAllFields(resourceModule.getName());
		

		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);
		Map<String, FacilioField> resourceFieldMap = FieldFactory.getAsMap(resourceFields);
			
		Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria(workOrderModule);
		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField idCountField = new FacilioField();
		idCountField.setColumnName("count(*)");
		idCountField.setName("count");
		fields.add(idCountField);

		
		FacilioField assignedToField = workorderFieldMap.get("assignedTo");
		
		FacilioField userIdField = AccountConstants.getUserIdField(orgUserModule);
		
		List<FacilioField> userFields = AccountConstants.getUserFields();
		Map<String,FacilioField> userFieldMap = FieldFactory.getAsMap(userFields);
	
		FacilioField userNameField = userFieldMap.get("name");
		FacilioField userField = userNameField.clone();
		userField.setName("user_name");
		fields.add(userField);
	
		
		FacilioField resourceNameField = resourceFieldMap.get("name");
		FacilioField resourceField = resourceNameField.clone();
		resourceField.setName("site_name");
		fields.add(resourceField);
		
     	

		GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
				  													.table(workOrderModule.getTableName())
				  													.select(fields)
				  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
				  													.innerJoin(orgUserModule.getTableName()).on(orgUserModule.getTableName() +".ORG_USERID = "+assignedToField.getCompleteColumnName())
				  													.innerJoin(userModule.getTableName()).on(userIdField.getCompleteColumnName()+" = "+userModule.getTableName()+".USERID")
				  													.innerJoin(resourceModule.getTableName()).on(workOrderModule.getTableName() +".SITE_ID = "+resourceModule.getTableName()+".ID")
				  													.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				  													.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
				  													.andCondition(CriteriaAPI.getCondition(assignedToField, CommonOperators.IS_NOT_EMPTY))
				  													.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																	.andCriteria(closedCriteria)
				  													.limit(Integer.parseInt(count))
				  													.groupBy(assignedToField.getCompleteColumnName()+","+userIdField.getCompleteColumnName()+","+userField.getCompleteColumnName()+","+resourceField.getCompleteColumnName())
																	.orderBy(idCountField.getColumnName()+" DESC");
		                                                            
				  													
	 List<Map<String, Object>> topNTechnicians = selectRecordsBuilder.get();
	 return topNTechnicians;

    }



	public static long getSiteIdForWO(long woId) throws Exception {
		WorkOrderContext wo = getWorkOrder(woId);
		if (wo == null) {
			return -1;
		}
		return wo.getSiteId();
	}
	
	
	
	
	
	
 }
