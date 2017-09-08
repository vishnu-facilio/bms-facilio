package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWorkOrderListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
														.connection(conn)
														.table(dataTableName)
														.moduleName(moduleName)
														.beanClass(WorkOrderContext.class)
														.select(fields)
														.orderBy("ID");

		if(view != null) {
			Criteria criteria = view.getCriteria();
			builder.andCriteria(criteria);
		}
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		if(filters != null && !filters.isEmpty())
		{	
			List<String> appliedFilters = new ArrayList<>();
			List<Condition> conditionList = filterToCondition(filters, appliedFilters);
			for(Condition condition : conditionList)
			{
				builder.andCondition(condition);
			}
		}
		
		List<WorkOrderContext> workOrders = builder.get();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Condition> filterToCondition(JSONObject filters, List<String> appliedFilters) throws Exception
	{
		List<Condition> conditionList = new ArrayList<>();
		Iterator<String> filterIterator = filters.keySet().iterator();
		while(filterIterator.hasNext())
		{
			String fieldName = filterIterator.next();
			JSONObject fieldJson = (JSONObject) filters.get(fieldName);
			JSONArray value = (JSONArray) fieldJson.get("value");
			if(value.size() > 0 || (fieldJson.get("operator") != null && !((String) fieldJson.get("operator")).equals("is")))
			{
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				String[] module = ((String) fieldJson.get("module")).split("\\.");
				if(module.length > 1)
				{
					FacilioField field = modBean.getField(module[1], module[0]);
					Condition condition = new Condition();
					condition.setField(field);
					condition.setOperator(LookupOperator.LOOKUP);
					
					FacilioField childField = modBean.getField(fieldName, module[1]);
					Condition childCondition = new Condition();
					childCondition.setField(childField);
					childCondition.setOperator(childField.getDataType().getOperator((String) fieldJson.get("operator")));
					
					if(value.size() > 0)
					{
						StringBuilder values = new StringBuilder();
						boolean isFirst = true;
						Iterator<String> iterator = value.iterator();
						while(iterator.hasNext())
						{
							String obj = iterator.next();
							if(!isFirst)
							{
								values.append(",");
							}
							else
							{
								isFirst = false;
							}
							values.append(obj.substring(0, obj.indexOf("_")));
						}
						childCondition.setValue(values.toString());
					}
					
					Criteria criteria = new Criteria();
					criteria.addAndCondition(childCondition);
					
					condition.setCriteriaValue(criteria);
					conditionList.add(condition);
				}
				else
				{
					FacilioField field = modBean.getField(fieldName, module[0]);
					Condition condition = new Condition();
					condition.setField(field);
					condition.setOperator(field.getDataType().getOperator((String) fieldJson.get("operator")));
					
					StringBuilder values = new StringBuilder();
					boolean isFirst = true;
					Iterator<String> iterator = value.iterator();
					while(iterator.hasNext())
					{
						String obj = iterator.next();
						if(!isFirst)
						{
							values.append(",");
						}
						else
						{
							isFirst = false;
						}
						values.append(obj.substring(0, obj.indexOf("_")));
					}
					condition.setValue(values.toString());
					conditionList.add(condition);
				}
			}
		}
		return conditionList;
	}
}
