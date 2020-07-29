package com.facilio.bmsconsoleV3.commands.purchaserequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LoadPurchaseRequestListLookupCommandV3 extends FacilioCommand {

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
		LookupField moduleStateField = (LookupField) fieldsAsMap.get("vendor");
		LookupField addressField = (LookupField) fieldsAsMap.get("storeRoom");
		additionaLookups.add(addressField);
		additionaLookups.add(moduleStateField);
		
		for (FacilioField field : fields) {
			if (!field.isDefault() && field.getDataTypeEnum() == FieldType.LOOKUP) {
				additionaLookups.add((LookupField) field);
			}
		}
		
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
		return false;
	}
}