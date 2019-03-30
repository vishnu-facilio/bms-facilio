package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.FormulaContext.CommonAggregateOperator;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetPlannedFutureWOCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO)) {
			return false;
		}

		FacilioField dateField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		if (dateField.getName().equals("createdTime") && endTime < System.currentTimeMillis()) {
			return false;
		}
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (!view.getName().equals("myopen") && !view.getName().equals("all")) {
			throw new IllegalArgumentException("For now only myopen/ all view is supported");
		}
		
		
//		Criteria transformedCriteria = transformCritera(view.getCriteria());
		
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
		FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
		
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		fields.addAll(FieldFactory.getWorkOrderTemplateFields());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField transformedDateField = getDateField(dateField.getName(), fieldMap);
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.table(pmJobsModule.getTableName())
																.innerJoin(pmModule.getTableName())
																.on(pmJobsModule.getTableName()+".PM_ID = "+pmModule.getTableName()+".ID")
																.innerJoin(woTemplateModule.getTableName())
																.on(pmModule.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID")
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmModule))
																.andCondition(CriteriaAPI.getCondition(transformedDateField, String.valueOf(startTime+","+endTime), DateOperators.BETWEEN))
																;
		
		if (view.getName().equals("myopen")) {
			selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assignedToId"), String.valueOf("${LOGGED_USER}"), PickListOperators.IS));
		}
		
		boolean isCount = (boolean) context.get(FacilioConstants.ContextNames.COUNT);
		if (isCount) {
			List<FacilioField> selecFields = new ArrayList<>();
			FacilioField countField = CommonAggregateOperator.COUNT.getSelectField(FieldFactory.getIdField(woTemplateModule));
			countField.setName("count");
			selecFields.add(countField);
			
			DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.AGGR_KEY);
			selecFields.add(aggr.getTimestampField(transformedDateField));
			selectRecordBuilder.groupBy(aggr.getSelectField(transformedDateField).getCompleteColumnName())
								.select(selecFields);
			
			List<Map<String, Object>> props = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT);
			props.addAll(selectRecordBuilder.get());
		}
		else {
			selectRecordBuilder.select(fields);
			List<Map<String, Object>> props = selectRecordBuilder.get();
			List<WorkOrderContext> existingWOs = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
			for (Map<String, Object> prop : props) {
				WorkorderTemplate template = FieldUtil.getAsBeanFromMap(prop, WorkorderTemplate.class);
				WorkOrderContext wo = template.getWorkorder();
				wo.setCreatedTime((Long) prop.get("createdTime"));
				
				Long dueDate = (Long) prop.get("dueDate");
				if (dueDate != null) {
					wo.setDueDate(dueDate);
				}
				existingWOs.add(wo);
			}
		}
		return false;
	}
	
	private FacilioField getDateField(String fieldName, Map<String, FacilioField> fieldMap) {
		FacilioField createdTimeField = fieldMap.remove("nextExecutionTime");
		FacilioField dueDateField = fieldMap.remove("duration");
		dueDateField.setColumnName("(("+createdTimeField.getColumnName()+"+"+dueDateField.getColumnName()+") * 1000)");
		dueDateField.setName("dueDate");
		dueDateField.setModule(null);
		fieldMap.put(dueDateField.getName(), dueDateField);
		
		createdTimeField.setColumnName("("+createdTimeField.getColumnName()+" * 1000)");
		createdTimeField.setName("createdTime");
		createdTimeField.setModule(null);
		fieldMap.put(createdTimeField.getName(), createdTimeField);
		
		if (fieldName.equals("createdTime")) {
			return createdTimeField;
		}
		else {
			return dueDateField;
		}
	}
	
	private Criteria transformCritera (Criteria criteria) {
		if (criteria == null || criteria.isEmpty()) {
			return criteria;
		}
		
		FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
		Criteria transformedCriteria = new Criteria();
		transformedCriteria.andCriteria(criteria);
		for (Condition condition : transformedCriteria.getConditions().values()) {
			condition.setComputedWhereClause(null);
			switch (condition.getFieldName()) {
				case "subject":
					condition.setColumnName(woTemplateModule.getTableName()+".SUBJECT");
					break;
				case "description":
					condition.setColumnName(woTemplateModule.getTableName()+".DESCRIPTION");
					break;
				case "status":
				case "workorder.status":
					condition.setColumnName(woTemplateModule.getTableName()+".STATUS_ID");
					break;
				case "priority":
				case "workorder.priority":
					condition.setColumnName(woTemplateModule.getTableName()+".PRIORITY_ID");
					break;
				case "category":
				case "workorder.category":
					condition.setColumnName(woTemplateModule.getTableName()+".CATEGORY_ID");
					break;
				case "type":
				case "workorder.type":
					condition.setColumnName(woTemplateModule.getTableName()+".TYPE_ID");
					break;
				case "assignmentGroup":
				case "workorder.assignmentGroup":
					condition.setColumnName(woTemplateModule.getTableName()+".ASSIGNMENT_GROUP_ID");
					break;
				case "assignedTo":
				case "workorder.assignedTo":
					condition.setColumnName(woTemplateModule.getTableName()+".ASSIGNED_TO_ID");
					break;
				case "resourceId":
				case "workorder.resourceId":
					condition.setColumnName(woTemplateModule.getTableName()+".RESOURCE_ID");
					break;
				case "siteId":
					condition.setColumnName(woTemplateModule.getTableName()+".SITE_ID");
					break;
				default:
					throw new IllegalArgumentException("This view cannot be used");
			}
		}
		return transformedCriteria;
	}

}
