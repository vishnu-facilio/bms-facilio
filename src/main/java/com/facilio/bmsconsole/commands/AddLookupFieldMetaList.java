package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddLookupFieldMetaList implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		List<LookupField>fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
		// return if existing lookup_field_meta_list is found
		if (CollectionUtils.isNotEmpty(fetchLookup)) {
			return false;
		}

		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
		}
		
		fetchLookup = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fields)) {
			for (FacilioField f : fields) {
				if (f instanceof LookupField) {
					fetchLookup.add((LookupField) f);
				}
			}
			if (CollectionUtils.isNotEmpty(fetchLookup)) {
				context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, fetchLookup);
			}
		}
		
		return false;
	}

}
