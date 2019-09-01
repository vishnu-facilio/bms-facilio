package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.chargebee.internal.StringJoiner;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderContext.AllowNegativePreRequisite;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

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
	
	public static void setPrerequisiteApprover(List<WorkOrderContext> wos) throws Exception {
		for (WorkOrderContext wo : wos) {
			if (wo.getId() > 0) {
				SharingContext<SingleSharingContext> sharing = SharingAPI.getSharing(wo.getId(),ModuleFactory.getPrerequisiteApproversModule(), SingleSharingContext.class);
				if(sharing !=null){
					wo.setPrerequisiteApprover(sharing.isAllowed());
				}
			}
		}
	}
	public static WorkOrderContext getWorkOrder(long ticketId) throws Exception {
		List<WorkOrderContext> workorders = getWorkOrders(Collections.singletonList(ticketId));
		if (CollectionUtils.isNotEmpty(workorders)) {
			return workorders.get(0);
		}
		return null;
	}
	
	public static List<WorkOrderContext> getWorkOrders(List<Long> ticketIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.module(module)
														.beanClass(WorkOrderContext.class)
														.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
														.andCondition(CriteriaAPI.getIdCondition(ticketIds, module))
														;
		
		List<WorkOrderContext> workOrders = builder.get();
		return workOrders;
	}
	
	public static Map<Long, WorkOrderContext> getWorkOrdersAsMap(List<Long> workorderIds) throws Exception {
		List<WorkOrderContext> workorders = getWorkOrders(workorderIds);
		if (CollectionUtils.isNotEmpty(workorders)) {
			return workorders.stream().collect(Collectors.toMap(WorkOrderContext::getId, Function.identity()));
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
	FacilioModule orgUserModule = AccountConstants.getAppOrgUserModule();
	
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

	public static void updatePreRequisiteEnabled(Long woId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String workorderModuleName = "workorder";
		FacilioModule workorderModule = modBean.getModule(workorderModuleName);
		List<FacilioField> allFields = modBean.getAllFields(workorderModuleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		Map<String, Object> updateMap = new HashMap<>();
		int preRequisiteCount=getPreRequisiteCount(woId);
		updateMap.put("prerequisiteEnabled", preRequisiteCount != 0);
		Condition idFieldCondition = CriteriaAPI.getCondition(FieldFactory.getIdField(workorderModule),String.valueOf(woId), NumberOperators.EQUALS);
		GenericUpdateRecordBuilder recordBuilder = new GenericUpdateRecordBuilder()
				.table(workorderModule.getTableName()).fields(Arrays.asList(fieldMap.get("prerequisiteEnabled")))
				.andCondition(idFieldCondition);
	    recordBuilder.update(updateMap);
	}
	public static PreRequisiteStatus updatePreRequisiteStatus(Long woId) throws Exception {
		PreRequisiteStatus preRequisiteStatus;
		WorkOrderContext workorder = getWorkOrder(woId);
		if (workorder.getPrerequisiteEnabled()) {
			preRequisiteStatus = getPreRequestStatus(woId);
			 if (AllowNegativePreRequisite.YES_WITH_APPROVAL.equals(workorder.getAllowNegativePreRequisiteEnum())) {
				if (preRequisiteStatus.equals(PreRequisiteStatus.COMPLETED_WITH_NEGATIVE) && (workorder.getPreRequisiteApproved() != null && workorder.getPreRequisiteApproved())) {
					preRequisiteStatus = PreRequisiteStatus.COMPLETED;
				}
			}
		} else {
			preRequisiteStatus = PreRequisiteStatus.COMPLETED;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String workorderModuleName = "workorder";
		FacilioModule workorderModule = modBean.getModule(workorderModuleName);
		List<FacilioField> allFields = modBean.getAllFields(workorderModuleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("preRequestStatus", preRequisiteStatus.getValue());
		Condition idFieldCondition = CriteriaAPI.getCondition(FieldFactory.getIdField(workorderModule),String.valueOf(woId), NumberOperators.EQUALS);
		GenericUpdateRecordBuilder recordBuilder = new GenericUpdateRecordBuilder()
				.table(workorderModule.getTableName()).fields(Arrays.asList(fieldMap.get("preRequestStatus")))
				.andCondition(idFieldCondition);
		recordBuilder.update(updateMap);
		return preRequisiteStatus;
	}
  public static Map<Long, Object> getTeamsCountBySite() throws Exception {

  
	    FacilioModule groupsModule = AccountConstants.getGroupModule();
		FacilioModule groupMembersModule = AccountConstants.getGroupMemberModule();
		FacilioModule rolesModule = AccountConstants.getRoleModule();
		FacilioModule orgUserModule = AccountConstants.getAppOrgUserModule();
		
		List<FacilioField> orgUserFields = AccountConstants.getAppOrgUserFields();
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

  
  public static List<Map<String, Object>> getTopNBuildingsWithPlannedTypeCount(String count,long startTime,long endTime, long siteId) throws Exception{
	  List<BuildingContext> buildings = siteId > 0 ? SpaceAPI.getSiteBuildings(siteId) : SpaceAPI.getAllBuildings();
	  
	  StringJoiner buildingIdString = new StringJoiner(",");
	  for(BuildingContext building : buildings) {
		  buildingIdString.add(String.valueOf(building.getId()));
	  }
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
	
				List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());

		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);

		List<FacilioField> fields = new ArrayList<FacilioField>();
		FacilioField plannedType = workorderFieldMap.get("type");
		
		FacilioField idCountField = new FacilioField();
		idCountField.setColumnName("count(*)");
		idCountField.setName("count");
		fields.add(idCountField);
				List<TicketTypeContext> types = TicketAPI.getPlannedTypes(AccountUtil.getCurrentOrg().getId());
		StringJoiner idString = new StringJoiner(",");
		for(TicketTypeContext type : types) {
			idString.add(String.valueOf(type.getId()));
		}
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		
		FacilioField buildingId = new FacilioField();
		buildingId.setName("buildingId");
		buildingId.setColumnName("BUILDING_ID");
		buildingId.setModule(baseSpaceModule);
		buildingId.setDataType(FieldType.NUMBER);

		fields.add(buildingId);

		Condition spaceCond = new Condition();
		spaceCond.setField(buildingId);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(buildingIdString.toString());

		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.module(workOrderModule)
				.beanClass(WorkOrderContext.class)
				.select(fields)
				.innerJoin(resourceModule.getTableName())
				.on(resourceModule.getTableName()+".ID = "+ resourceIdFld.getCompleteColumnName())
				.innerJoin(baseSpaceModule.getTableName())
				.on(baseSpaceModule.getTableName()+".ID = "+ resourceModule.getTableName()+".SPACE_ID")
				.andCondition(CriteriaAPI.getCondition(plannedType, idString.toString() , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(spaceCond)
				.groupBy(buildingId.getCompleteColumnName())
				.orderBy(idCountField.getColumnName()+" DESC")
                .limit(Integer.parseInt(count))
				;


	 List<Map<String, Object>> plannedWorkOrders = builder.getAsProps();
	 Map<Long,Map<String, Object>> plannedMap = new HashMap<Long, Map<String,Object>>();
	 StringJoiner resourceidString = new StringJoiner(",");
	 for(int i =0;i<plannedWorkOrders.size();i++) {
		 Map<String, Object> map = plannedWorkOrders.get(i);
		 resourceidString.add(String.valueOf(map.get("buildingId")));
		 plannedMap.put((Long)map.get("buildingId"),map);
	 }
			
	 Condition spaceCond2 = new Condition();
		spaceCond2.setField(buildingId);
		spaceCond2.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond2.setValue(resourceidString.toString());
		
	 
	 SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilderLastMonth = new SelectRecordsBuilder<WorkOrderContext>()
											.module(workOrderModule)
											.beanClass(WorkOrderContext.class)
											.select(fields)
											.innerJoin(resourceModule.getTableName())
											.on(resourceModule.getTableName()+".ID = "+ resourceIdFld.getCompleteColumnName())
											.innerJoin(baseSpaceModule.getTableName())
											.on(baseSpaceModule.getTableName()+".ID = "+ resourceModule.getTableName()+".SPACE_ID")
											.andCondition(CriteriaAPI.getCondition(plannedType, idString.toString() , NumberOperators.EQUALS))
											.andCondition(spaceCond2)
											.groupBy(buildingId.getCompleteColumnName())
											.orderBy(idCountField.getColumnName()+" DESC")
											;

	Long lastMonthStartTime = DateTimeUtil.getMonthStartTime(-2,false);
	Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
	
	selectRecordsBuilderLastMonth.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
	
	List<Map<String,Object>> lastMonthList =selectRecordsBuilderLastMonth.getAsProps();	
	
	 Map<Long,Map<String, Object>> plannedLastMonthMap = new HashMap<Long, Map<String,Object>>();
	 for(int i =0;i<lastMonthList.size();i++) {
		 Map<String, Object> map = lastMonthList.get(i);
		 plannedLastMonthMap.put((Long)map.get("buildingId"),map);
	 }
		
	List<Map<String, Object>> finalResult = new ArrayList<Map<String,Object>>();
	 Map<Long, Object> resourceArray = WorkOrderAPI.getLookupFieldPrimary("resource");

	 for(int i =0;i<plannedWorkOrders.size();i++) {
		 Map<String,Object> map = plannedWorkOrders.get(i);
		 Map<String,Object> planned = plannedMap.get(map.get("buildingId"));
		 Map<String,Object> plannedLastMonth = plannedLastMonthMap.get(map.get("buildingId"));
		 long diff = 0;
		 Map<String, Object> resMap = new HashMap<String, Object>();
			
		 if(plannedLastMonth != null) {
			 diff = (((long)planned.get("count") - (long)plannedLastMonth.get("count")) * 100 / (long)plannedLastMonth.get("count") ) ;
			 resMap.put("difference",diff > 0 ? 1 : diff == 0 ? 2 : 3 );//1-increase,2-same,3-decrease,4-no data
				
		 }
		 else {
			 resMap.put("difference",4);//1-increase,2-same,3-decrease,4-no data
		 }
		
		 resMap.put("plannedCount",planned.get("count"));
		 resMap.put("percentage",Math.abs(diff));
		 resMap.put("resourceId",map.get("buildingId"));
		 resMap.put("resourceName",resourceArray.get((Long)map.get("buildingId")));
		 finalResult.add(resMap);
	 }
	
	 return finalResult;
    }


  public static List<Map<String, Object>> getTopNBuildingsWithUnPlannedTypeCount(String count,long startTime,long endTime, long siteId) throws Exception{
	  List<BuildingContext> buildings = siteId > 0 ? SpaceAPI.getSiteBuildings(siteId) : SpaceAPI.getAllBuildings();
	  StringJoiner buildingIdString = new StringJoiner(",");
	  for(BuildingContext building : buildings) {
		  buildingIdString.add(String.valueOf(building.getId()));
	  }
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
				List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
				
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);

		List<FacilioField> fields = new ArrayList<FacilioField>();
		FacilioField plannedType = workorderFieldMap.get("type");
		
		FacilioField idCountField = new FacilioField();
		idCountField.setColumnName("count(*)");
		idCountField.setName("count");
		fields.add(idCountField);
		List<TicketTypeContext> types = TicketAPI.getPlannedTypes(AccountUtil.getCurrentOrg().getId());
		StringJoiner idString = new StringJoiner(",");
		for(TicketTypeContext type : types) {
			idString.add(String.valueOf(type.getId()));
		}
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		FacilioField buildingId = new FacilioField();
		buildingId.setName("buildingId");
		buildingId.setColumnName("BUILDING_ID");
		buildingId.setModule(baseSpaceModule);
		buildingId.setDataType(FieldType.NUMBER);

		fields.add(buildingId);
	
		Condition spaceCond = new Condition();
		spaceCond.setField(buildingId);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(buildingIdString.toString());

		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.module(workOrderModule)
				.beanClass(WorkOrderContext.class)
				.select(fields)
				.innerJoin(resourceModule.getTableName())
				.on(resourceModule.getTableName()+".ID = "+ resourceIdFld.getCompleteColumnName())
				.innerJoin(baseSpaceModule.getTableName())
				.on(baseSpaceModule.getTableName()+".ID = "+ resourceModule.getTableName()+".SPACE_ID")
				.andCondition(CriteriaAPI.getCondition(plannedType, idString.toString() , NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(spaceCond)
				.groupBy(buildingId.getCompleteColumnName())
				.orderBy(idCountField.getColumnName()+" DESC")
                .limit(Integer.parseInt(count))
				;


	 List<Map<String, Object>> unPlannedWorkOrders = builder.getAsProps();
	 
	 Map<Long,Map<String, Object>> unplannedMap = new HashMap<Long, Map<String,Object>>();
	 StringJoiner resourceidString = new StringJoiner(",");
	 for(int i =0;i<unPlannedWorkOrders.size();i++) {
		 Map<String, Object> map = unPlannedWorkOrders.get(i);
		 resourceidString.add(String.valueOf(map.get("buildingId")));
		 unplannedMap.put((Long)map.get("buildingId"),map);
	 }
	 Condition spaceCond2 = new Condition();
		spaceCond2.setField(buildingId);
		spaceCond2.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond2.setValue(resourceidString.toString());
	 
	 SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilderLastMonth = new SelectRecordsBuilder<WorkOrderContext>()
											.module(workOrderModule)
											.beanClass(WorkOrderContext.class)
											.select(fields)
											.innerJoin(resourceModule.getTableName())
											.on(resourceModule.getTableName()+".ID = "+ resourceIdFld.getCompleteColumnName())
											.innerJoin(baseSpaceModule.getTableName())
											.on(baseSpaceModule.getTableName()+".ID = "+ resourceModule.getTableName()+".SPACE_ID")
											.andCondition(CriteriaAPI.getCondition(plannedType, idString.toString() , NumberOperators.NOT_EQUALS))
											.andCondition(spaceCond2)
											.groupBy(buildingId.getCompleteColumnName())
											.orderBy(idCountField.getColumnName()+" DESC")
											;

	Long lastMonthStartTime = DateTimeUtil.getMonthStartTime(-2,false);
	Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
	
	selectRecordsBuilderLastMonth.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
	
	List<Map<String,Object>> lastMonthList =selectRecordsBuilderLastMonth.getAsProps();	
	
	 Map<Long,Map<String, Object>> unplannedLastMonthMap = new HashMap<Long, Map<String,Object>>();
	 for(int i =0;i<lastMonthList.size();i++) {
		 Map<String, Object> map = lastMonthList.get(i);
		 unplannedLastMonthMap.put((Long)map.get("buildingId"),map);
	 }
	
	List<Map<String, Object>> finalResult = new ArrayList<Map<String,Object>>();
	 Map<Long, Object> resourceArray = WorkOrderAPI.getLookupFieldPrimary("resource");

	 for(int i =0;i<unPlannedWorkOrders.size();i++) {
		 Map<String,Object> map = unPlannedWorkOrders.get(i);
		 Map<String,Object> unplanned = unplannedMap.get(map.get("buildingId"));
		 Map<String,Object> unplannedLastMonth = unplannedLastMonthMap.get(map.get("buildingId"));
			
		 long diff = 0;
		 Map<String, Object> resMap = new HashMap<String, Object>();
		 resMap.put("unplannedCount",unplanned.get("count"));
		 if(unplannedLastMonth != null) {
			 diff = (((long)unplanned.get("count") - (long)unplannedLastMonth.get("count")) * 100 / (long)unplannedLastMonth.get("count") )  ;
			 resMap.put("difference",diff > 0 ? 1 : diff == 0 ? 2 : 3 );//1-increase,2-same,3-decrease,4-no data
				
		 }
		 else {
			 resMap.put("difference",4);//1-increase,2-same,3-decrease,4-no data
		 }
		 resMap.put("percentage",Math.abs(diff));
		 resMap.put("resourceId",map.get("buildingId"));
		 resMap.put("resourceName",resourceArray.get((Long)map.get("buildingId")));
		 finalResult.add(resMap);
	 }
	
	
	 return finalResult;
    }



	
	public static List<Map<String, Object>> getTopNTechnicians(String count,long startTime,long endTime) throws Exception{
		  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
			FacilioModule orgUserModule = AccountConstants.getAppOrgUserModule();
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

			FacilioField resourceNameField = resourceFieldMap.get("name");
			FacilioField resourceField = resourceNameField.clone();
			resourceField.setName("site_name");
			fields.add(resourceField);
			fields.add(userIdField);

			GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
					  													.table(workOrderModule.getTableName())
					  													.select(fields)
					  													.innerJoin(ticketModule.getTableName()).on(workOrderModule.getTableName() +".ID = "+ticketModule.getTableName()+".ID")
					  													.innerJoin(orgUserModule.getTableName()).on(orgUserModule.getTableName() +".ORG_USERID = "+assignedToField.getCompleteColumnName())
					  													.innerJoin(resourceModule.getTableName()).on(workOrderModule.getTableName() +".SITE_ID = "+resourceModule.getTableName()+".ID")
					  													.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
					  													.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(workOrderModule), CommonOperators.IS_NOT_EMPTY))
					  													.andCondition(CriteriaAPI.getCondition(assignedToField, CommonOperators.IS_NOT_EMPTY))
					  													.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
																		.andCriteria(closedCriteria)
																		.limit(Integer.parseInt(count))
					  													.groupBy(assignedToField.getCompleteColumnName()+","+userIdField.getCompleteColumnName()+","+resourceField.getCompleteColumnName())
																		.orderBy(idCountField.getColumnName()+" DESC")
			                                                            .andCustomWhere("ACTUAL_WORK_END <= DUE_DATE");



		 List<Map<String, Object>> topNTechnicians = selectRecordsBuilder.get();
			
		 if(CollectionUtils.isNotEmpty(topNTechnicians)) {
			 Map<Long, User> orgUsers = AccountUtil.getOrgBean().getOrgUsersAsMap(AccountUtil.getCurrentOrg().getOrgId(), null);
			 for(Map<String, Object> map : topNTechnicians) {
				 long userId = (long)map.get("uid");
				 User techDetails = orgUsers.get(userId);
				 map.put("user_name", techDetails.getName());
			 }
		 }
		 return topNTechnicians;

	    }


	public static List<Map<String, Object>> getTopNTeamWithOpenCloseCount(String count,long startTime, long endTime, long siteId) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);




		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField assignedToTeamField = workorderFieldMap.get("assignmentGroup");
		FacilioField teamField = assignedToTeamField.clone();
		teamField.setName("team_id");
		fields.add(teamField);

		FacilioField idCountField = new FacilioField();
		idCountField.setColumnName("count(*)");
		idCountField.setName("count");
		fields.add(idCountField);


		Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();
        Condition openCondition = ViewFactory.getOpenStatusCondition();
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																	  .module(workOrderModule)
																	  .beanClass(WorkOrderContext.class)
																	  .select(fields)
																	  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																	  .andCriteria(closedCriteria)
																	  .andCondition(CriteriaAPI.getCondition(teamField,"",CommonOperators.IS_NOT_EMPTY))
																	  .groupBy(teamField.getCompleteColumnName())
																	  .orderBy(idCountField.getColumnName()+" DESC")
																	  .limit(Integer.parseInt(count))

			                                                          ;
		if(siteId > 0) {
			 selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}
	   List<Map<String, Object>> totalClosedWoCountByTeam = selectRecordsBuilder.getAsProps();
	   Map<Long,Map<String, Object>> result = new HashMap<Long, Map<String,Object>>();
	   StringJoiner teamIdString = new StringJoiner(",");
		for(int i=0;i<totalClosedWoCountByTeam.size();i++)
		{
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> teamMap = (Map<String, Object>) totalClosedWoCountByTeam.get(i).get("team_id");
		  Group group = AccountUtil.getGroupBean().getGroup((Long)teamMap.get("id"));
		  map.put("teamName", group.getName());
		  map.put("closed", totalClosedWoCountByTeam.get(i).get("count"));
		  result.put((Long)teamMap.get("id"), map);
		  teamIdString.add(String.valueOf(teamMap.get("id")));
		}

		SelectRecordsBuilder<WorkOrderContext> openBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				  .module(workOrderModule)
				  .beanClass(WorkOrderContext.class)
				  .select(fields)
				  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				  .andCondition(CriteriaAPI.getCondition(teamField, String.valueOf(teamIdString), NumberOperators.EQUALS))
				  .andCondition(openCondition)
				  .groupBy(teamField.getCompleteColumnName())
				  .orderBy(idCountField.getColumnName()+" DESC")
				
                ;
		if(siteId > 0) {
			 openBuilder.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> openCountByTeam = openBuilder.getAsProps();
		List<Map<String,Object>> finalResult = new ArrayList<Map<String,Object>>();
		Map<Long, Map<String, Object>> openMap = new HashMap<Long, Map<String,Object>>();
		for(int i=0;i<openCountByTeam.size();i++)
		{
		  Map<String,Object> map = openCountByTeam.get(i);
		  Map teamMap = (Map) map.get("team_id");
		  openMap.put((Long)teamMap.get("id"), map);
		}
		for(int i=0;i<totalClosedWoCountByTeam.size();i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> teamMap = (Map<String, Object>) totalClosedWoCountByTeam.get(i).get("team_id");
			long open = openMap.get(teamMap.get("id")) != null ? (long) openMap.get(teamMap.get("id")).get("count") : 0;
			result.get(teamMap.get("id")).put("open", open);
			finalResult.add(result.get(teamMap.get("id")));
		}
		return finalResult;

	}


	public static List<Map<String,Object>> getAvgCompletionTimeByTechnician(String count, Long startTime,Long endTime, Long siteId) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");


		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		
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


		FacilioField assignedTo = workorderFieldMap.get("assignedTo");
		FacilioField technicianField = assignedTo.clone();
		technicianField.setName("technician");
		fields.add(technicianField);


		FacilioField statusField = ticketFieldMap.get("status");
		//fields.add(statusField);


		//fetching the workorders with closed status

		Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				  .module(workOrderModule)
				  .beanClass(WorkOrderContext.class)
				  .select(fields)
				  .andCriteria(closedCriteria)
				  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				  .andCondition(CriteriaAPI.getCondition(technicianField, CommonOperators.IS_NOT_EMPTY))
				  .groupBy(technicianField.getCompleteColumnName())
				  .orderBy(countField.getColumnName()+" DESC")
				  .limit(Integer.parseInt(count))

                ;

		if(siteId > 0) {
			 selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}

       List<Map<String,Object>> avgResolutionTime = selectRecordsBuilder.getAsProps();
       List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		 StringJoiner techIdString = new StringJoiner(",");
		 Map<Long, Map<String,Object>> techMap = new HashMap<Long, Map<String,Object>>();
		 for(int i =0;i<avgResolutionTime.size();i++) {
			 Map<String, Object> map = avgResolutionTime.get(i);
			 Map technicianMap = (Map) map.get("technician");
			 techIdString.add(String.valueOf(technicianMap.get("id")));
			 techMap.put((Long)technicianMap.get("id"),map);
		 }
		 
			SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilderLastMonth = new SelectRecordsBuilder<WorkOrderContext>()
					  .module(workOrderModule)
					  .beanClass(WorkOrderContext.class)
					  .select(fields)
					  .andCriteria(closedCriteria)
					  .andCondition(CriteriaAPI.getCondition(technicianField.getCompleteColumnName(), "assignedTo", techIdString.toString(),NumberOperators.EQUALS ))
					  .groupBy(technicianField.getCompleteColumnName())
					
	                ;
			if(siteId > 0) {
				 selectRecordsBuilderLastMonth.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
			}

			Long lastMonthStartTime = DateTimeUtil.getMonthStartTime(-2,false);
			Long lastMonthEndTime = DateTimeUtil.getMonthEndTimeOf(lastMonthStartTime,false);
			
			selectRecordsBuilderLastMonth.andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", lastMonthStartTime+","+lastMonthEndTime, DateOperators.BETWEEN));
			
			List<Map<String, Object>> lastMonthList = selectRecordsBuilderLastMonth.getAsProps();
			
			 Map<Long,Map<String, Object>> lastMonthMap = new HashMap<Long, Map<String,Object>>();
			 for(int i =0;i<lastMonthList.size();i++) {
				 Map<String, Object> map = lastMonthList.get(i);
				 Map technicianMap = (Map) map.get("technician");
				 lastMonthMap.put((Long)technicianMap.get("id"),map);
			 }
			
			List<Map<String, Object>> finalResult = new ArrayList<Map<String,Object>>();
			 for(int i =0;i<avgResolutionTime.size();i++) {
				 Map<String, Object> resMap = new HashMap<String, Object>();
					
				 Map<String,Object> map = avgResolutionTime.get(i);
				 Map technicianMap = (Map) map.get("technician");
				 Map<String,Object> thsMonth = techMap.get(technicianMap.get("id"));
				 Map<String,Object> lastMonth = lastMonthMap.get(technicianMap.get("id"));
				
				 if(lastMonth != null ) {
					 Double avgTime = 0.0;
					 if(lastMonth.get("avg_resolution_time") != null) {
					   avgTime = ((Number)lastMonth.get("avg_resolution_time")).doubleValue();
					 }
					 avgTime = Math.round(avgTime*100.0)/100.0;
					 
					 resMap.put("avg_resolution_time_last_month",avgTime );
					
				 }
				 else {
					 resMap.put("avg_resolution_time_last_month",0);
						
				 }
				 Double avgTime = ((Number)thsMonth.get("avg_resolution_time")).doubleValue();
				 avgTime = Math.round(avgTime*100.0)/100.0;
				   
				 resMap.put("avgResolutionTime",avgTime);
				 User user = AccountUtil.getUserBean().getUser((Long)technicianMap.get("id"), true);
				 resMap.put("technicianName",user.getName());
				 finalResult.add(resMap);
			 }
			

       return finalResult;


	}



	public static long getSiteIdForWO(long woId) throws Exception {
		WorkOrderContext wo = getWorkOrder(woId);
		if (wo == null) {
			return -1;
		}
		return wo.getSiteId();
	}

	public static int getPreRequisiteCount(Long woId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = "task";
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("COUNT(*)");
		countField.setDataType(FieldType.NUMBER);
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(woId),NumberOperators.EQUALS);
		Condition preRequestCondition = CriteriaAPI.getCondition(fieldMap.get("preRequest"), "1",NumberOperators.EQUALS);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().table(module.getTableName()).select(Collections.singletonList(countField))
				.groupBy(fieldMap.get("parentTicketId").getCompleteColumnName()).andCondition(condition)
				.andCondition(preRequestCondition);
		Map<String, Object> props = select.fetchFirst();
		int count = 0;
		if(props != null){
			count = ((Number) props.get("count")).intValue();
		}
		return count;
	}
	public static PreRequisiteStatus getPreRequestStatus(Long woId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = "task";
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> allFields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		FacilioField countField = new FacilioField();
		countField.setName("count");
		countField.setColumnName("COUNT(*)");
		countField.setDataType(FieldType.NUMBER);
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), Collections.singletonList(woId),NumberOperators.EQUALS);
		Condition preRequestCondition = CriteriaAPI.getCondition(fieldMap.get("preRequest"), "1",NumberOperators.EQUALS);
		Condition inputValueCondition = CriteriaAPI.getCondition(fieldMap.get("inputValue"), "NULL",CommonOperators.IS_EMPTY);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(Collections.singletonList(countField))
				.groupBy(fieldMap.get("parentTicketId").getCompleteColumnName()).andCondition(condition)
				.andCondition(preRequestCondition).andCondition(inputValueCondition);
		Map<String, Object> props = select.fetchFirst();
		int nullcount = 0;
		if (props != null) {
			nullcount = ((Number) props.get("count")).intValue();
		}
		 inputValueCondition = CriteriaAPI.getCondition(fieldMap.get("inputValue"), "0",StringOperators.IS);
		 select = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(Collections.singletonList(countField))
				.groupBy(fieldMap.get("parentTicketId").getCompleteColumnName()).andCondition(condition)
				.andCondition(preRequestCondition).andCondition(inputValueCondition);
		 props = select.fetchFirst();
		int negativecount = 0;
		if (props != null) {
			negativecount = ((Number) props.get("count")).intValue();
		}
		 inputValueCondition = CriteriaAPI.getCondition(fieldMap.get("inputValue"), "1",StringOperators.IS);
		 select = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(Collections.singletonList(countField))
				.groupBy(fieldMap.get("parentTicketId").getCompleteColumnName()).andCondition(condition)
				.andCondition(preRequestCondition).andCondition(inputValueCondition);
		 props = select.fetchFirst();
		int positivecount = 0;
		if (props != null) {
			positivecount = ((Number) props.get("count")).intValue();
		}
		PreRequisiteStatus preRequestStatus = PreRequisiteStatus.NOT_STARTED;
	    if(negativecount > 0 || positivecount > 0){
	    	 preRequestStatus = PreRequisiteStatus.PENDING;
	    }
		boolean photoValidation = isPhotosAddedIfMandatory(woId);
		if(nullcount == 0 && negativecount == 0 && photoValidation){
			preRequestStatus = PreRequisiteStatus.COMPLETED;
		}else if(nullcount == 0 && negativecount > 0 && photoValidation){
			preRequestStatus = PreRequisiteStatus.COMPLETED_WITH_NEGATIVE;
		}
		return preRequestStatus;
	}
	public static Boolean isPhotosAddedIfMandatory(Long id)throws Exception{
		WorkOrderContext workorder=WorkOrderAPI.getWorkOrder(id);
		boolean isPhtMandatory = workorder.getPhotoMandatory() == null ? Boolean.FALSE : workorder.getPhotoMandatory();
		if(!isPhtMandatory){
			return true;
		}else{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String moduleName=FacilioConstants.ContextNames.PREREQUISITE_PHOTOS;
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			Condition idCondition = CriteriaAPI.getCondition(modBean.getField("parentId", moduleName), String.valueOf(id),NumberOperators.EQUALS);
			SelectRecordsBuilder<PhotosContext> selectBuilder = new SelectRecordsBuilder<PhotosContext>()
																		.moduleName(moduleName)
																		.beanClass(PhotosContext.class)
																		.select(fields)
																		.table(module.getTableName())
																		.andCondition(idCondition);
	        return !selectBuilder.get().isEmpty();
		}
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

    public static String getSiteName(long siteId) throws Exception {
    	
    	return SpaceAPI.getSiteSpace(siteId).getName();
    }

    public static List<Map<String, Object>> getTopNTechniciansWithOpenCloseCount(String count,long startTime, long endTime, long siteId) throws Exception {


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> workorderFields = modBean.getAllFields(workOrderModule.getName());
		Map<String, FacilioField> workorderFieldMap = FieldFactory.getAsMap(workorderFields);




		List<FacilioField> fields = new ArrayList<FacilioField>();

		FacilioField assignedToTechField = workorderFieldMap.get("assignedTo");
		FacilioField techField = assignedToTechField.clone();
		techField.setName("tech_id");
		fields.add(techField);

		FacilioField idCountField = new FacilioField();
		idCountField.setColumnName("count(*)");
		idCountField.setName("count");
		fields.add(idCountField);


		Criteria closedCriteria = ViewFactory.getClosedTicketsCriteria();
        Condition openCondition = ViewFactory.getOpenStatusCondition();
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																	  .module(workOrderModule)
																	  .beanClass(WorkOrderContext.class)
																	  .select(fields)
																	  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
																	  .andCriteria(closedCriteria)
																	  .andCondition(CriteriaAPI.getCondition(techField,"",CommonOperators.IS_NOT_EMPTY))
																	  .groupBy(techField.getCompleteColumnName())
																	  .orderBy(idCountField.getColumnName()+" DESC")
																	  .limit(Integer.parseInt(count))

			                                                          ;
		if(siteId > 0) {
			 selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}
	   List<Map<String, Object>> totalClosedWoCountByTech = selectRecordsBuilder.getAsProps();
	   Map<Long,Map<String, Object>> result = new HashMap<Long, Map<String,Object>>();
	   StringJoiner techIdString = new StringJoiner(",");
		for(int i=0;i<totalClosedWoCountByTech.size();i++)
		{
		  Map<String, Object> map = new HashMap<String, Object>();
		  Map<String, Object> techMap = (Map<String, Object>) totalClosedWoCountByTech.get(i).get("tech_id");
		  User user = AccountUtil.getUserBean().getUser((Long)techMap.get("id"), true);
		  map.put("technicianName", user.getName());
		  map.put("closed", totalClosedWoCountByTech.get(i).get("count"));
		  result.put((Long)techMap.get("id"), map);
		  techIdString.add(String.valueOf(techMap.get("id")));
		}

		SelectRecordsBuilder<WorkOrderContext> openBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				  .module(workOrderModule)
				  .beanClass(WorkOrderContext.class)
				  .select(fields)
				  .andCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN))
				  .andCondition(CriteriaAPI.getCondition(techField, String.valueOf(techIdString), NumberOperators.EQUALS))
				  .andCondition(openCondition)
				  .groupBy(techField.getCompleteColumnName())
				  .orderBy(idCountField.getColumnName()+" DESC")
				
                ;
		if(siteId > 0) {
			 openBuilder.andCondition(CriteriaAPI.getCondition(workOrderModule.getTableName()+".SITE_ID", "siteId", String.valueOf(siteId), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> openCountByTech = openBuilder.getAsProps();
		List<Map<String,Object>> finalResult = new ArrayList<Map<String,Object>>();
		Map<Long, Map<String, Object>> openMap = new HashMap<Long, Map<String,Object>>();
		for(int i=0;i<openCountByTech.size();i++)
		{
		  Map<String,Object> map = openCountByTech.get(i);
		  Map techMap = (Map) map.get("tech_id");
		  openMap.put((Long)techMap.get("id"), map);
		}
		for(int i=0;i<totalClosedWoCountByTech.size();i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> teamMap = (Map<String, Object>) totalClosedWoCountByTech.get(i).get("tech_id");
			long open = openMap.get(teamMap.get("id")) != null ? (long) openMap.get(teamMap.get("id")).get("count") : 0;
			result.get(teamMap.get("id")).put("open", open);
			finalResult.add(result.get(teamMap.get("id")));
		}
		return finalResult;

	}
 }
