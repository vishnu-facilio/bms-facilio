package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadPurchaseRequestSummaryLookupCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
			
		List<FacilioField>fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
		}
		
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		
		LookupFieldMeta vendorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("vendor"));
		LookupField vendorLocationField = (LookupField) modBean.getField("address", FacilioConstants.ContextNames.VENDORS);
		vendorField.addChildSupplement(vendorLocationField);
		
		additionaLookups.add(vendorField);
		additionaLookups.add((LookupField) fieldsAsMap.get("storeRoom"));
		additionaLookups.add((LookupField) fieldsAsMap.get("shipToAddress"));
		additionaLookups.add((LookupField) fieldsAsMap.get("billToAddress"));
		additionaLookups.add((LookupField) fieldsAsMap.get("requestedBy"));
		if(moduleName.contentEquals("purchaseorder")) {
			additionaLookups.add((LookupField) fieldsAsMap.get("contract"));
		} else if (moduleName.contentEquals("purchaserequest")) {
			additionaLookups.add((LookupField) fieldsAsMap.get("purchaseOrder"));
		}
		
		for (FacilioField field : fields) {
			if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
				additionaLookups.add((LookupField) field);
			}
		}
		
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
		return false;
	}

}
