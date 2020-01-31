package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LoadWorkPermitLookUpsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField>fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
		}
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		LookupField vendorField = (LookupField) fieldsAsMap.get("vendor");
		LookupField vendorContactField = (LookupField) fieldsAsMap.get("vendorContact");
		LookupField ticketField = (LookupField) fieldsAsMap.get("ticket");
		LookupField moduleStateField = (LookupField)fieldsAsMap.get("moduleState");
		if(AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
			LookupField tenantField = (LookupField)fieldsAsMap.get("tenant");
			additionaLookups.add(tenantField);
		}
		LookupField requestedByField = (LookupField)fieldsAsMap.get("requestedBy");
		LookupField issuedToUserField = (LookupField)fieldsAsMap.get("issuedToUser");
		
		additionaLookups.add(vendorField);
		additionaLookups.add(ticketField);
		additionaLookups.add(vendorContactField);
		additionaLookups.add(moduleStateField);
		additionaLookups.add(requestedByField);
		additionaLookups.add(issuedToUserField);
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionaLookups);
		return false;
	}

}
