package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class PurchaseRequestPurchaseOrderLookUpsCommand implements Command  {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField >fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
		}
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		List<LookupFieldMeta> fetchLookup = Arrays.asList(new LookupFieldMeta((LookupField) fieldsAsMap.get("vendor")), 
				new LookupFieldMeta((LookupField) fieldsAsMap.get("storeRoom")),new LookupFieldMeta((LookupField) fieldsAsMap.get("shipToAddress")),new LookupFieldMeta((LookupField) fieldsAsMap.get("billToAddress")));
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);
		return false;
	}

}
