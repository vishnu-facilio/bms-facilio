package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.fields.FacilioField;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import com.facilio.fw.BeanFactory;

public class LoadServiceRequestLookupCommandV3 extends FacilioCommand {

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
		LookupField priorityField = (LookupField) fieldsAsMap.get("urgency");
		LookupField requesterField = (LookupField) fieldsAsMap.get("requester");
		LookupField resourceField = (LookupField) fieldsAsMap.get("resource");
		LookupField moduleStateField = (LookupField) fieldsAsMap.get("moduleState");
		additionaLookups.add(priorityField);
		additionaLookups.add(requesterField);
		additionaLookups.add(resourceField);
		additionaLookups.add((LookupField) fieldsAsMap.get("assignmentGroup"));
		additionaLookups.add((LookupField) fieldsAsMap.get("assignedTo"));
		additionaLookups.add(moduleStateField);
		LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
		additionaLookups.add(sysCreatedBy);

		context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);
		return false;
	}

}
