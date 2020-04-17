package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurchaseRequestPurchaseOrderLookUpsCommand extends FacilioCommand  {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField>fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
		}
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		LookupFieldMeta vendorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("vendor"));
		LookupField vendorLocationField = (LookupField) modBean.getField("address", FacilioConstants.ContextNames.VENDORS);
		vendorField.addChildLookupField(vendorLocationField);
		
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		additionaLookups.add(vendorField);
		additionaLookups.add((LookupField) fieldsAsMap.get("storeRoom"));
		additionaLookups.add((LookupField) fieldsAsMap.get("shipToAddress"));
		additionaLookups.add((LookupField) fieldsAsMap.get("billToAddress"));
		additionaLookups.add((LookupField) fieldsAsMap.get("requestedBy"));
		if(moduleName.contentEquals("purchaseorder")) {
			additionaLookups.add((LookupField) fieldsAsMap.get("contract"));
		}
		for (FacilioField f : fields) {
			if (!f.isDefault() && f.getDataTypeEnum() == FieldType.LOOKUP) {
				additionaLookups.add((LookupField) f);
			}
		}
		
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionaLookups);
		return false;
	}

}
