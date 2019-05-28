package com.facilio.bmsconsole.util;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.logging.Logger;

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
		// LOGGER.log(Level.SEVERE, "builder1 - "+builder);
		return workOrders;
	}

	public static List<WorkOrderContext> getOpenWorkOrderForUser(Long ouid) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioStatus status = TicketAPI.getStatus("Closed");
		
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
		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.select(selectFields)
				.module(module)
				;

		List<Map<String,Object>> asProps = builder.getAsProps();
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
			FacilioStatus statusMap = mp.getStatus();
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


public static List<Map<String,Object>> getWorkOrderStatusPercentageForWorkflow(String count,Long startTime,Long endTime) throws Exception {

	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
	ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
	
	FacilioField idCountField = new FacilioField();
	idCountField.setColumnName("count(*)");
	idCountField.setName("count");
	fields.add(idCountField);
	
	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);


	Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();

	SelectRecordsBuilder<WorkOrderContext> closedSelectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																	  .module(workOrderModule)
																	  .beanClass(WorkOrderContext.class)
																	  .select(fields)
																	  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																	  .andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
																	  .andCriteria(closedCriteria)
																	  .orderBy(idCountField.getColumnName()+" DESC")
																	  .groupBy(siteIdField.getCompleteColumnName())
			                                                          ;

    if(count != null)
    {
    	closedSelectRecordsBuilder.limit(Integer.parseInt(count));
		 
    }

	List<Map<String, Object>> closedCount = closedSelectRecordsBuilder.getAsProps();
	Map<Long,Object> closedMap = new HashMap<Long,Object>();
	Map<Long,Object> onTimeMap = new HashMap<Long,Object>();
	Map<Long,Object> openMap = new HashMap<Long,Object>();
	
	StringBuilder sb = new StringBuilder();
	for(int i=0;i<closedCount.size();i++)
	{   
		 
		Long siteId = (Long)closedCount.get(i).get("siteId");
		closedMap.put(siteId,closedCount.get(i).get("count"));
	    sb.append(siteId);
		if(i<closedCount.size()-1)
		{
			sb.append(",");
		}
		
		
	}

	
	SelectRecordsBuilder<WorkOrderContext> onTimeSelectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
															.module(workOrderModule)
															.beanClass(WorkOrderContext.class)
															.select(fields)
			  												.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
			  												.andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
			  												.andCondition(CriteriaAPI.getCondition(siteIdField,sb.toString(), NumberOperators.EQUALS))
			  												.andCriteria(closedCriteria)
			  												.andCustomWhere("ACTUAL_WORK_END <= DUE_DATE")
			  											    .groupBy(siteIdField.getCompleteColumnName())
	                                                        ;



    List<Map<String, Object>> onTimeCount = onTimeSelectRecordsBuilder.getAsProps();
    for(int i=0;i<onTimeCount.size();i++)
    {
    	Long siteId = (Long)onTimeCount.get(i).get("siteId");
		onTimeMap.put(siteId,onTimeCount.get(i).get("count"));
	    
    }
    
    FacilioStatus status = TicketAPI.getStatus("Closed");
	
	SelectRecordsBuilder<WorkOrderContext> openSelectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
													.module(workOrderModule)
													.beanClass(WorkOrderContext.class)
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
													.andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
													.andCondition(CriteriaAPI.getCondition("STATUS_ID", "status", status.getId()+"", NumberOperators.NOT_EQUALS))
													.andCondition(CriteriaAPI.getCondition(siteIdField,sb.toString(), NumberOperators.EQUALS))
													.groupBy(siteIdField.getCompleteColumnName())
	                                                ;



    List<Map<String, Object>> openCount = openSelectRecordsBuilder.getAsProps();
    for(int i=0;i<openCount.size();i++)
    {
    	Long siteId = (Long)openCount.get(i).get("siteId");
		openMap.put(siteId,openCount.get(i).get("count"));
	    
    }
    
    Map<Long, Object> siteNameArray = getLookupFieldPrimary("site");
    List<Map<String,Object>> resp = new ArrayList<Map<String,Object>>();
    
    for(int i=0;i<closedCount.size();i++)
     { 
       Long siteId =(Long) closedCount.get(i).get("siteId");
       Map<String,Object> siteInfo = new HashMap<String, Object>();
       Long closed = (Long)closedCount.get(i).get("count");
       Long onTime =onTimeMap.get(siteId)!=null?(Long) onTimeMap.get(siteId):0;
       Object openObj = openMap.remove(siteId);
       Long open = openObj!=null?(Long)openObj:0;
       Long overdue = closed - onTime;
       Long total = onTime+overdue+open;
       if(total>0) {
    	   Long onTimePercentage = (onTime*100) / total ;
    	   Long overDuePercentage = (overdue*100) / total ;
      	   siteInfo.put("onTime",onTime);
           siteInfo.put("overdue",overdue);
           siteInfo.put("open",open);
           siteInfo.put("total",total);
           siteInfo.put("onTimePercentage",onTimePercentage);
           siteInfo.put("overduePercentage",overDuePercentage);
           
  	      
       }
       else
       {
    	   siteInfo.put("onTime",0);
           siteInfo.put("overdue",0);
           siteInfo.put("open",0);
           siteInfo.put("total",total);
           siteInfo.put("onTimePercentage",0);
           siteInfo.put("overduePercentage",0);
         
           
          
       }
        
       siteInfo.put("siteId",siteId);
       siteInfo.put("siteName",siteNameArray.get(siteId));
       resp.add(siteInfo);
       
     }
    Iterator<Map.Entry<Long, Object>> openSitesitr = openMap.entrySet().iterator(); 
    
    while(openSitesitr.hasNext()) { 
         Map.Entry<Long, Object> entry = openSitesitr.next(); 
         Long siteId = entry.getKey();
         Long openCountVal = (Long)entry.getValue();
         Map<String,Object> siteInfo = new HashMap<String, Object>();
         siteInfo.put("onTime",0);
         siteInfo.put("overdue",0);
         siteInfo.put("open",openCountVal);
         siteInfo.put("total",openCountVal);
         siteInfo.put("onTimePercentage",0);
         siteInfo.put("overduePercentage",0);
         siteInfo.put("siteId",siteId);
         siteInfo.put("siteName",siteNameArray.get(siteId));
         resp.add(siteInfo);
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
public static List<Map<String,Object>> getAvgCompletionTimeByCategory(Long startTime,Long endTime,Long siteId) throws Exception {


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

		FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
		
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

       if(siteId != 0)
       {
    	   selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(siteIdField, ""+siteId, NumberOperators.EQUALS));
       }

       List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();


       return avgResolutionTime;
     

	}

public static List<Map<String,Object>> getTopNCategoryOnAvgCompletionTime(String count,Long startTime,Long endTime) throws Exception {


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


	FacilioField idCountField = new FacilioField();
	idCountField.setColumnName("count(*)");
	idCountField.setName("count");
	fields.add(idCountField);
	
	Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();

	SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilderClosed = new SelectRecordsBuilder<WorkOrderContext>()
																.module(workOrderModule)
																.beanClass(WorkOrderContext.class)
																.select(fields)
																.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																.andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
																.andCriteria(closedCriteria)
																.orderBy(idCountField.getColumnName()+" DESC")
																.groupBy(siteIdField.getCompleteColumnName())
																;
	List<Map<String,Object>> closedList = selectRecordsBuilderClosed.getAsProps();	
	
	Map<Long,Object> closedMap = new HashMap<Long, Object>();
	
    for(int i=0;i<closedList.size();i++)
    {
    	Long siteId = (Long)closedList.get(i).get("siteId");
    	closedMap.put(siteId,closedList.get(i).get("count"));
	    
    }


	SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilderTotal = new SelectRecordsBuilder<WorkOrderContext>()
																		.module(workOrderModule)
																		.beanClass(WorkOrderContext.class)
																		.select(fields)
																		.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																		.andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
																		.orderBy(idCountField.getColumnName()+" DESC")
																		.groupBy(siteIdField.getCompleteColumnName())
																		;
	
	
    List<Map<String,Object>> totalList = selectRecordsBuilderTotal.getAsProps();	
	
	Map<Long,Object> totalMap = new HashMap<Long, Object>();
	
    for(int i=0;i<totalList.size();i++)
    {
    	Long siteId = (Long)totalList.get(i).get("siteId");
    	totalMap.put(siteId,totalList.get(i).get("count"));
	    
    }

    StringJoiner sb = new StringJoiner(",");
	for(int i=0;i<totalList.size();i++) {
		Long siteId = (Long)totalList.get(i).get("siteId");
		Long totalCount = (Long)totalList.get(i).get("count");
		Long closedCount =closedMap.get(siteId)!=null? (Long)closedMap.get(siteId) : 0;
		Long closedOnTimePercentage = (closedCount*100)/totalCount;
		
		if(closedOnTimePercentage >= 70) {
			sb.add(String.valueOf(siteId));
		}
		
	}
	
	GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
			  													.table(workOrderModule.getTableName())
			  													.select(fields)
			  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
				  												.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
				  												.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
				  												.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
				  												.andCondition(CriteriaAPI.getCondition(siteIdField,sb.toString(), NumberOperators.EQUALS))
				  												.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																.groupBy(siteIdField.getCompleteColumnName())
																.orderBy(avgField.getCompleteColumnName()+" ASC")
																.limit(Integer.parseInt(count))
															  ;
	   
		selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		List<Map<String,Object>> list =selectRecordsBuilder.get();		
		
		Map<Long,Object> avgResolutionMap = new HashMap<Long, Object>();
		StringBuilder closeCountSb = new StringBuilder();
		for(int i=0;i<list.size();i++)
		{   
			 
			Long siteId = (Long)list.get(i).get("siteId");
			avgResolutionMap.put(siteId,list.get(i).get("avg_resolution_time"));
			closeCountSb.append(siteId);
			if(i<list.size()-1)
			{
				closeCountSb.append(",");
			}
			
			
		}
	  
		GenericSelectRecordBuilder selectRecordsBuilderLastMonth = new GenericSelectRecordBuilder()
																.table(workOrderModule.getTableName())
																.select(fields)
																.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
																.innerJoin(ticketStatusModule.getTableName()).on(statusField.getCompleteColumnName() +" = "+ticketStatusModule.getTableName()+".ID")
																.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "typeCode", ""+StatusType.CLOSED.getIntVal() , NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(siteIdField,closeCountSb.toString(), NumberOperators.EQUALS))
					  											.groupBy(siteIdField.getCompleteColumnName())
																.orderBy(avgField.getCompleteColumnName()+" ASC")
																.limit(Integer.parseInt(count))
																;

		Long lastMonthStartTime = DateTimeUtil.getMonthStartTime(-2,false);
		Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
		
		selectRecordsBuilderLastMonth.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
		
		List<Map<String,Object>> lastMonthList =selectRecordsBuilderLastMonth.get();		
		Map<Long,Object> avgResolutionTillLastMonthMap = new HashMap<Long, Object>();
		
	    for(int i=0;i<lastMonthList.size();i++)
	    {
	    	Long siteId = (Long)lastMonthList.get(i).get("siteId");
	    	avgResolutionTillLastMonthMap.put(siteId,lastMonthList.get(i).get("avg_resolution_time"));
		    
	    }
	    
	     
	    
	    Map<Long, Object> siteNameArray = getLookupFieldPrimary("site");
	    List<Map<String,Object>> resp = new ArrayList<Map<String,Object>>();
		 
	    for(int i=0;i<list.size();i++) 
	     { 
	       Long siteId = (Long)list.get(i).get("siteId");
	       Map<String,Object> siteInfo = new HashMap<String, Object>();
	         
	       siteInfo.put("siteId",siteId);
	       
	       Double avgResolutionTime = ((Number)avgResolutionMap.get(siteId)).doubleValue();
	       avgResolutionTime = Math.round(avgResolutionTime*100.0)/100.0;
		   siteInfo.put("avgResolutionTime",avgResolutionTime);
	    
		   Double avgResolutionTimeTillLastMonth = 0.0;
		   if(avgResolutionTillLastMonthMap.get(siteId)!=null)
		   {
			   avgResolutionTimeTillLastMonth = ((Number)avgResolutionTillLastMonthMap.get(siteId)).doubleValue();
		       avgResolutionTimeTillLastMonth = Math.round(avgResolutionTimeTillLastMonth*100.0)/100.0;
		   }
		   siteInfo.put("avgResolutionTimeTillLastMonth",avgResolutionTimeTillLastMonth);
		   siteInfo.put("siteName",siteNameArray.get(siteId));
	       resp.add(siteInfo);
	       
	     }
	   
	   return resp;


 

}



public static List<Map<String,Object>> getAvgCompletionTimeBySite(Long startTime,Long endTime,boolean isLastMonth) throws Exception {


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
																.orderBy(avgField.getCompleteColumnName()+" ASC")
															  ;


	 if(isLastMonth)
	   {
		    Long previousMonthTime = DateTimeUtil.addMonths(startTime, -1);
		 	Long lastMonthStartTime = DateTimeUtil.getMonthStartTimeOf(previousMonthTime, false);
		 	Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
			
			selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
					
	   }
	   else
	   {
		   selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
				
	   }

   List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.get();
   /*Map<Long,Object> countMap = new HashMap<Long, Object>();
	
	
	for(int i=0;i<avgResolutionTime.size();i++)
	{
		Map<String, Object> siteTechMap = avgResolutionTime.get(i);
		countMap.put((Long)siteTechMap.get("siteId"),siteTechMap.get("avg_resolution_time"));
		
	}
	*/
	return avgResolutionTime;



}


public static Map<Long,Object> getAvgResponseTimeBySite(Long startTime,Long endTime,boolean isLastMonth) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
	FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
	

	FacilioModule ticketStatusModule = modBean.getModule(FacilioConstants.ContextNames.TICKET_STATUS);

	List<FacilioField> ticketFields = modBean.getAllFields(workOrderModule.getName());


	Map<String, FacilioField> ticketFieldMap = FieldFactory.getAsMap(ticketFields);


	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField avgField = new FacilioField();
	avgField.setName("avg_response_time");
	avgField.setColumnName("avg((ACTUAL_WORK_START-CREATED_TIME)/(1000*60))");//to render client in mins
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

   if(isLastMonth)
   {
	    Long previousMonthTime = DateTimeUtil.addMonths(startTime, -1);
	    Long lastMonthStartTime = DateTimeUtil.getMonthStartTimeOf(previousMonthTime, false);
	 	Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
			
		selectRecordsBuilder.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
				
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




public static List<Map<String, Object>> getTotalWoCountBySite(Long startTime,Long endTime) throws Exception {


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
																  .groupBy(siteIdField.getCompleteColumnName())
																  .orderBy(idCountField.getColumnName()+" DESC")
					                                      		  ;
    List<Map<String, Object>> totalWoCountBySite = selectRecordsBuilder.getAsProps();
    
   
   return totalWoCountBySite;

}

public static List<Map<String,Object>> getTotalClosedWoCountBySite(Long startTime,Long endTime) throws Exception {


	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
	List<FacilioField> fields = new ArrayList<FacilioField>();

	FacilioField idCountField = new FacilioField();
	idCountField.setColumnName("count(*)");
	idCountField.setName("count");
	fields.add(idCountField);

	FacilioField siteIdField = FieldFactory.getSiteIdField(workOrderModule);
	fields.add(siteIdField);
	
	Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();

	SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																  .module(workOrderModule)
																  .beanClass(WorkOrderContext.class)
																  .select(fields)
																  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																  .andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
																  .andCriteria(closedCriteria)
																  .groupBy(siteIdField.getCompleteColumnName())
																  .orderBy(idCountField.getColumnName()+" DESC")
		                                                          ;
   List<Map<String, Object>> totalClosedWoCountBySite = selectRecordsBuilder.getAsProps();
 
  return totalClosedWoCountBySite;

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

  
  public static Map<Long, Object> getScreensCountBySite() throws Exception {

	  
	  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	  FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
	  FacilioModule spaceCategoryModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
	
	  List<FacilioField> spaceCategoryFields = new ArrayList<FacilioField>();
	  FacilioField idField = new FacilioField();
	  idField.setName("id");
	  idField.setColumnName("ID");
	  idField.setModule(spaceCategoryModule);
	  spaceCategoryFields.add(idField);
	  
	  List<FacilioField> fields = new ArrayList<FacilioField>();
		
	  FacilioField countField = new FacilioField();
	  countField.setName("count");
	  countField.setColumnName("count(*)");
	  fields.add(countField);
		
	  FacilioField siteIdField = FieldFactory.getSiteIdField(spaceModule);
	  fields.add(siteIdField);
 
	  GenericSelectRecordBuilder spaceCategorySelectBuilder = new GenericSelectRecordBuilder()
				.select(spaceCategoryFields).table(spaceCategoryModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(spaceCategoryModule.getTableName()+".NAME", "name" ,"Movie Screen", StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(spaceCategoryModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
			    ;
	 List<Map<String, Object>> spaceCategoryList = spaceCategorySelectBuilder.get();
	 if(spaceCategoryList.size()>0) {
	 Long screenCategoryId =(Long) spaceCategoryList.get(0).get("id");
	 
	 
	 SelectRecordsBuilder<SpaceContext> selectRecordsBuilder = new SelectRecordsBuilder<SpaceContext>()
														  .module(spaceModule)
														  .beanClass(SpaceContext.class)
					                                      .select(fields)
					                                      .andCondition(CriteriaAPI.getCondition(spaceModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
					                                      .andCondition(CriteriaAPI.getCondition(siteIdField, CommonOperators.IS_NOT_EMPTY))
					                                      .andCondition(CriteriaAPI.getCondition(spaceModule.getTableName()+".SPACE_CATEGORY_ID","spaceCategory",""+screenCategoryId ,NumberOperators.EQUALS))
		  											      .groupBy(siteIdField.getCompleteColumnName())
					                                      ;
	 
	 
	 List<Map<String, Object>> screenCountList = selectRecordsBuilder.getAsProps();
	 Map<Long,Object> countMap = new HashMap<Long, Object>();
		
		
		for(int i=0;i<screenCountList.size();i++)
		{
			Map<String, Object> siteMap = screenCountList.get(i);
			countMap.put((Long)siteMap.get("siteId"),siteMap.get("count"));
			
		}
		return countMap;
	 }
	 return null;
	 


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
			
		Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();
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
																	.orderBy(idCountField.getColumnName()+" DESC")
		                                                            .andCustomWhere("ACTUAL_WORK_END <= DUE_DATE");
							
			
				  													
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
	
	
    public static WorkorderItemContext getWorkOrderItem(long woItemId) throws Exception {
    	
    	  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	  FacilioModule woItemModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
    	
    	
    	 SelectRecordsBuilder<WorkorderItemContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkorderItemContext>()
				  .module(woItemModule)
				  .beanClass(WorkorderItemContext.class)
                 .select(modBean.getAllFields(woItemModule.getName()))
                 .andCondition(CriteriaAPI.getIdCondition(woItemId, woItemModule))
                ;
    	 
    	 List<WorkorderItemContext> list = selectRecordsBuilder.get();
    	 if(CollectionUtils.isNotEmpty(list)) {
    		 return list.get(0);
    	 }
    	 return null;

    }
	
    public static WorkorderToolsContext getWorkOrderTool(long woToolId) throws Exception {
    	
  	  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
  	  FacilioModule woToolModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);
  	
  	
  	 SelectRecordsBuilder<WorkorderToolsContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkorderToolsContext>()
				  .module(woToolModule)
				  .beanClass(WorkorderToolsContext.class)
               .select(modBean.getAllFields(woToolModule.getName()))
               .andCondition(CriteriaAPI.getIdCondition(woToolId, woToolModule))
              ;
  	 
  	 List<WorkorderToolsContext> list = selectRecordsBuilder.get();
  	 if(CollectionUtils.isNotEmpty(list)) {
  		 return list.get(0);
  	 }
  	 return null;

  }
	
	
	
 }
