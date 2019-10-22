package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.LookupFieldMeta;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LoadVisitorLoggingLookUpCommand extends FacilioCommand{

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
		LookupField eventField = (LookupField) fieldsAsMap.get("invite");
		LookupField visitorField = (LookupField) fieldsAsMap.get("visitor");
		LookupField hostField = (LookupField) fieldsAsMap.get("host");
		LookupField visitedSpaceField = (LookupField) fieldsAsMap.get("visitedSpace");
		//LookupField moduleStateField = (LookupField)fieldsAsMap.get("moduleState");
		additionaLookups.add(eventField);
		additionaLookups.add(visitorField);
		additionaLookups.add(hostField);
		additionaLookups.add(visitedSpaceField);
		//additionaLookups.add(moduleStateField);
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,additionaLookups);
	
		return false;
	}

}
