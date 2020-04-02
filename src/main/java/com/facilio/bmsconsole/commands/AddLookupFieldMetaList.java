package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class AddLookupFieldMetaList extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Boolean shouldFetchLookup = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_LOOKUPS, false);

		if (shouldFetchLookup != null && !shouldFetchLookup) {
			return false;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<LookupField> fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
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
