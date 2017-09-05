package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetWorkOrderListCommand implements Command {

	@SuppressWarnings("unchecked")
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
			Iterator<String> filterIterator = filters.keySet().iterator();
			while(filterIterator.hasNext())
			{
				String fieldName = filterIterator.next();
				JSONObject fieldJson = (JSONObject) filters.get(fieldName);
				String module = (String) fieldJson.get("module");
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField field = modBean.getField(fieldName, module);
				
				JSONArray value = (JSONArray) fieldJson.get("value");
				if(value.size() > 0)
				{
					Iterator<Object> arrayIterator = value.iterator();
					while(arrayIterator.hasNext())
					{
						Object obj = arrayIterator.next();
						if(obj instanceof JSONObject)
						{
							JSONObject childFilter = (JSONObject) obj;
							
							Iterator<String> childFilterIterator = childFilter.keySet().iterator();
							while(childFilterIterator.hasNext())
							{
								String childFieldName = childFilterIterator.next();
								JSONObject childFieldJson = (JSONObject) childFilter.get(childFieldName);
								String childModule = (String) childFieldJson.get("module");
								FacilioField childField = modBean.getField(childFieldName, childModule);
								
								JSONArray childValue = (JSONArray) childFieldJson.get("value");
								if(childValue.size() > 0)
								{
									Iterator<Object> arrayIterator2 = childValue.iterator();
									StringBuilder values = new StringBuilder();
									boolean isFirst = true;
									while(arrayIterator2.hasNext())
									{
										Object obj2 = arrayIterator2.next();
										if(!isFirst)
										{
											values.append(",");
										}
										values.append((String) obj2);
									}
									Condition condition = new Condition();
									condition.setField(field);
									condition.setOperator(LookupOperator.LOOKUP);
									
									Condition childCondition = new Condition();
									childCondition.setField(childField);
									childCondition.setOperator(childField.getDataType().getOperator((String) childFieldJson.get("operator")));
									childCondition.setValue(values.toString());
									
									Criteria criteria = new Criteria();
									criteria.addAndCondition(childCondition);
									
									condition.setCriteriaValue(criteria);
									builder.andCondition(condition);
								}
								else if(childField.getDataType() == FieldType.DATE_TIME && childFieldJson.get("operator") != null)
								{
									Condition condition = new Condition();
									condition.setField(field);
									condition.setOperator(LookupOperator.LOOKUP);
									
									Condition childCondition = new Condition();
									childCondition.setField(childField);
									childCondition.setOperator(childField.getDataType().getOperator((String) childFieldJson.get("operator")));
									
									Criteria criteria = new Criteria();
									criteria.addAndCondition(childCondition);
									
									condition.setCriteriaValue(criteria);
									builder.andCondition(condition);
								}
							}
						}
						else
						{
							Iterator<Object> arrayIterator2 = value.iterator();
							StringBuilder values = new StringBuilder();
							boolean isFirst = true;
							while(arrayIterator2.hasNext())
							{
								Object obj2 = arrayIterator2.next();
								if(!isFirst)
								{
									values.append(",");
								}
								values.append((String) obj2);
							}
							Condition condition = new Condition();
							condition.setField(field);
							condition.setOperator(field.getDataType().getOperator((String) fieldJson.get("operator")));
							condition.setValue(values.toString());
							
							builder.andCondition(condition);
						}
					}
				}
			}
		}
		
		List<WorkOrderContext> workOrders = builder.get();
		context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, workOrders);
		
		return false;
	}

}
