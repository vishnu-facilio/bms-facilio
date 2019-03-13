package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
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

public class GetItemTypesDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) fieldsMap.get("status")));
		lookUpfields.add(new LookupFieldMeta((LookupField) fieldsMap.get("category")));
		
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, lookUpfields);
		return false;
	}

}
