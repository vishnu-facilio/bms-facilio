package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;

public class GetWorkOrderCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetWorkOrderCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long workOrderId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(workOrderId > 0) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
																.table(dataTableName)
																.moduleName(moduleName)
																.beanClass(WorkOrderContext.class)
																.select(fields)
																.andCustomWhere(module.getTableName()+".ID = ?", workOrderId)
																.orderBy("ID")
																.skipModuleCriteria();
			
			boolean fetchTriggers = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_TRIGGERS, false);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			if (fetchTriggers) {
				builder.fetchSupplement((LookupField) fieldMap.get("trigger"));
			}

			//fetch vendor details
			builder.fetchSupplement((LookupField) fieldMap.get("vendor"));
			//fetch safetyPlan details
			if(AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
				builder.fetchSupplement((LookupField) fieldMap.get("safetyPlan"));
			}
			
			// temp fix
			List<SupplementRecord> customLookupFields = new ArrayList<>();
			for (FacilioField field : fields) {
				if (!field.isDefault()) {
					if ((field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP)) {
						if (((LookupField) field).getSpecialType() == null) {
							customLookupFields.add((SupplementRecord) field);
						}
					}
					else if (field.getDataTypeEnum() == FieldType.MULTI_ENUM) {
						customLookupFields.add((SupplementRecord) field);
					}
					
				}
			}
			builder.fetchSupplements(customLookupFields);
			builder.fetchSupplement((LookupField) fieldMap.get("client"));
			builder.fetchSupplement((LookupField) fieldMap.get("type"));
			
			List<WorkOrderContext> workOrders = builder.get();
			if(workOrders.size() > 0) {
				WorkOrderContext workOrder = workOrders.get(0);
				
				context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
				context.put(FacilioConstants.ContextNames.RECORD, workOrder);
				
				TicketAPI.loadWorkOrderLookups(Collections.singleton(workOrder));
//				if ((AccountUtil.getCurrentOrg().getId() == 146 || AccountUtil.getCurrentOrg().getId() == 155) && workOrder != null) {
					LOGGER.debug("Workorder subject : "+ workOrder.getSubject()+"\n Description : "+workOrder.getDescription());
//				}
			}
		}
		else {
			throw new IllegalArgumentException("Invalid Work Order ID : "+workOrderId);
		}
		
		return false;
	}

}
