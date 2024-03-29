package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadServiceRequestLookupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
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
		additionaLookups.add(priorityField);
		additionaLookups.add(requesterField);
		additionaLookups.add(resourceField);
		additionaLookups.add((LookupField) fieldsAsMap.get("assignmentGroup"));
		additionaLookups.add((LookupField) fieldsAsMap.get("assignedTo"));
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, additionaLookups);
		return false;
	}

}
