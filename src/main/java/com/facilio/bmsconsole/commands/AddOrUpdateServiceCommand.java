package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateServiceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ServiceContext serviceContext = (ServiceContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (serviceContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			FacilioModule serviceVendorModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
			
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			if (serviceContext.getId() > 0) {
				updateRecord(serviceContext, module, fields);
				DeleteRecordBuilder<ServiceVendorContext> deleteBuilder = new DeleteRecordBuilder<ServiceVendorContext>()
						.module(serviceVendorModule)
						.andCondition(CriteriaAPI.getCondition("SERVICE_ID", "service", String.valueOf(serviceContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				serviceContext.setStatus(ServiceContext.ServiceStatus.ACTIVE);
				addRecord(true, Collections.singletonList(serviceContext), module, fields);
			}
			if(CollectionUtils.isNotEmpty(serviceContext.getServiceVendors())) {
				updateServiceVendors(serviceContext);
				addRecord(false, serviceContext.getServiceVendors(), serviceVendorModule, modBean.getAllFields(serviceVendorModule.getName()));
			}
		
			context.put(FacilioConstants.ContextNames.RECORD, serviceContext);
		}
		return false;
	}
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	
	private void updateServiceVendors(ServiceContext serviceContext) {
		for (ServiceVendorContext serviceVendorContext : serviceContext.getServiceVendors()) {
			serviceVendorContext.setServiceId(serviceContext.getId());
		}
	}
	
	

}
