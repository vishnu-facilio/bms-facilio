package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupFieldMeta;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LoadVisitorLoggingLookUpCommand extends FacilioCommand {

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
		LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitor"));
		LookupField visitorLastVisitedLocationField = (LookupField) modBean.getField("lastVisitedSpace", FacilioConstants.ContextNames.VISITOR);
		visitorField.addChildSupplement(visitorLastVisitedLocationField);
		LookupField hostField = (LookupField) fieldsAsMap.get("host");
		LookupField visitedSpaceField = (LookupField) fieldsAsMap.get("visitedSpace");
		LookupField moduleStateField = (LookupField)fieldsAsMap.get("moduleState");
		LookupField visitorTypefield = (LookupField)fieldsAsMap.get("visitorType");
		if(AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
			LookupField tenant = (LookupField)fieldsAsMap.get("tenant");
			additionaLookups.add(tenant);
		}
		LookupField requestedBy = (LookupField)fieldsAsMap.get("requestedBy");
		
		additionaLookups.add(visitorField);
		additionaLookups.add(hostField);
		additionaLookups.add(visitedSpaceField);
		additionaLookups.add(moduleStateField);
		additionaLookups.add(visitorTypefield);
		
		additionaLookups.add(requestedBy);
		
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionaLookups);
	
		return false;
	}

}
