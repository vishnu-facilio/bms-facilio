package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetItemTypesDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		List<LookupField>lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) fieldsMap.get("status"));
		lookUpfields.add((LookupField) fieldsMap.get("category"));
		
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, lookUpfields);
		return false;
	}

}
