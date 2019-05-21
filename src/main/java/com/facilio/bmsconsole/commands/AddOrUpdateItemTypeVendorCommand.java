package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemTypesVendorsContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrUpdateItemTypeVendorCommand implements Command{

	@Override
	public boolean execute(org.apache.commons.chain.Context context) throws Exception {
		List<ItemTypesVendorsContext> itemTypeVendorsList = (List<ItemTypesVendorsContext>) context.get(FacilioConstants.ContextNames.ITEM_VENDORS_LIST);
		if(itemTypeVendorsList!=null){
			long vendorId = (long) context.get(FacilioConstants.ContextNames.VENDOR_ID);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule itemVendorModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_VENDORS);
			List<FacilioField> itemVendorFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_VENDORS);
			Map<String, FacilioField> itemVendorFieldMap = FieldFactory.getAsMap(itemVendorFields);
			List<Long> itemTypesId = new ArrayList<>();
			Map<Long, Long> itemTypeVsVendor = new HashMap<>();
			
			SelectRecordsBuilder<ItemTypesVendorsContext> itemVendorselectBuilder = new SelectRecordsBuilder<ItemTypesVendorsContext>().select(itemVendorFields)
					.table(itemVendorModule.getTableName()).moduleName(itemVendorModule.getName()).beanClass(ItemTypesVendorsContext.class)
					.andCondition(CriteriaAPI.getCondition(itemVendorFieldMap.get("vendor"), String.valueOf(vendorId),
							NumberOperators.EQUALS));
			
			List<ItemTypesVendorsContext> itemVendorsList = itemVendorselectBuilder.get();
			if (itemVendorsList != null && !itemVendorsList.isEmpty()) {
				for (ItemTypesVendorsContext itemVendor : itemVendorsList) {
					itemTypesId.add(itemVendor.getItemType().getId());
					itemTypeVsVendor.put(itemVendor.getItemType().getId(), itemVendor.getId());
				}
			}
			
			List<ItemTypesVendorsContext> itemVendorsToBeAdded = new ArrayList<>();
			for (ItemTypesVendorsContext itemVendors : itemTypeVendorsList) {
				if (!itemTypesId.contains(itemVendors.getItemType().getId())) {
					itemVendorsToBeAdded.add(itemVendors);
				} else {
					itemVendors.setId(itemTypeVsVendor.get(itemVendors.getItemType().getId()));
					updateItemVendor(itemVendorModule, itemVendorFields, itemVendors);
				}
			}
			
			if (itemVendorsToBeAdded != null && !itemVendorsToBeAdded.isEmpty()) {
				addItem(itemVendorModule, itemVendorFields, itemVendorsToBeAdded);
			}
		}
		return false;
	}

	private void addItem(FacilioModule module, List<FacilioField> fields, List<ItemTypesVendorsContext> parts) throws Exception {
		InsertRecordBuilder<ItemTypesVendorsContext> readingBuilder = new InsertRecordBuilder<ItemTypesVendorsContext>().module(module)
				.fields(fields).addRecords(parts);
		readingBuilder.save();
	}
	
	private void updateItemVendor(FacilioModule module, List<FacilioField> fields, ItemTypesVendorsContext parts) throws Exception {
		UpdateRecordBuilder<ItemTypesVendorsContext> builder = new UpdateRecordBuilder<ItemTypesVendorsContext>().module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(parts.getId(), module));
		builder.update(parts);
	}

}
