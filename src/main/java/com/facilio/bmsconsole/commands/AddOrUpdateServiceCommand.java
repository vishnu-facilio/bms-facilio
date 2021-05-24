package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.modules.fields.SupplementRecord;
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

public class AddOrUpdateServiceCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ServiceContext serviceContext = (ServiceContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (serviceContext != null) {
			serviceContext.setSellingPrice((double)Math.round(serviceContext.getSellingPrice()*100)/100);
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

		//custom fields multi lookup handling
		List<SupplementRecord> supplements = new ArrayList<>();
		CommonCommandUtil.handleFormDataAndSupplement(fields, list.get(0).getData(), supplements);

		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}

		if(!supplements.isEmpty()) {
			insertRecordBuilder.insertSupplements(supplements);
		}
		insertRecordBuilder.addRecord(list.get(0));
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {

		//custom fields multi lookup handling
		List<SupplementRecord> supplements = new ArrayList<>();
		CommonCommandUtil.handleFormDataAndSupplement(fields, data.getData(), supplements);

		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));

		if(!supplements.isEmpty()) {
			updateRecordBuilder.updateSupplements(supplements);
		}

		updateRecordBuilder.update(data);
	}
	
	private void updateServiceVendors(ServiceContext serviceContext) {
		for (ServiceVendorContext serviceVendorContext : serviceContext.getServiceVendors()) {
			serviceVendorContext.setServiceId(serviceContext.getId());
		}
	}
	
	

}
