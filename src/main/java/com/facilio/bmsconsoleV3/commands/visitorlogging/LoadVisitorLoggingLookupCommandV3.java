package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadVisitorLoggingLookupCommandV3 extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
		}
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField> additionaLookups = new ArrayList<LookupField>();
		LookupFieldMeta visitorField = new LookupFieldMeta((LookupField) fieldsAsMap.get("visitor"));
		LookupField visitorLastVisitedLocationField = (LookupField) modBean.getField("lastVisitedSpace",FacilioConstants.ContextNames.VISITOR);
		visitorField.addChildSupplement(visitorLastVisitedLocationField);
		LookupField hostField = (LookupField) fieldsAsMap.get("host");

		LookupField moduleStateField = (LookupField) fieldsAsMap.get("moduleState");
		LookupField visitorTypefield = (LookupField) fieldsAsMap.get("visitorType");
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
			LookupField tenant = (LookupField) fieldsAsMap.get("tenant");
			additionaLookups.add(tenant);
		}
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VENDOR)) {
			LookupField vendor = (LookupField) fieldsAsMap.get("vendor");
			additionaLookups.add(vendor);
		}
		LookupField requestedBy = (LookupField) fieldsAsMap.get("requestedBy");

		additionaLookups.add(visitorField);
		additionaLookups.add(hostField);
		additionaLookups.add(moduleStateField);
		additionaLookups.add(visitorTypefield);
		additionaLookups.add(requestedBy);

		LookupField visitedSpaceField = (LookupField) fieldsAsMap.get("visitedSpace");
		additionaLookups.add(visitedSpaceField);

		LookupField createdByField = (LookupField) fieldsAsMap.get("sysCreatedBy");
		additionaLookups.add(createdByField);

		LookupField modifiedByField = (LookupField) fieldsAsMap.get("sysModifiedBy");
		additionaLookups.add(modifiedByField);

		if (moduleName.equals(FacilioConstants.ContextNames.VISITOR_LOG)) {
			additionaLookups.add((LookupField) fieldsAsMap.get("invite"));
		}
		if (moduleName.equals(FacilioConstants.ContextNames.INVITE_VISITOR)){
			additionaLookups.add((LookupField) fieldsAsMap.get("groupId"));
		}
		context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);

		return false;
	}
}
