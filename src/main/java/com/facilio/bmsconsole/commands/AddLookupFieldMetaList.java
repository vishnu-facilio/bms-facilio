package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;

public class AddLookupFieldMetaList extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Boolean shouldFetchLookup = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_LOOKUPS, false);
		Boolean customLookupsOnly = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_CUSTOM_LOOKUPS, false);
		shouldFetchLookup = shouldFetchLookup == null || shouldFetchLookup || customLookupsOnly;

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<LookupField> fetchLookup = (List<LookupField>) context.get(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST);
		// return if existing lookup_field_meta_list is found
		if (CollectionUtils.isNotEmpty(fetchLookup)) {
			shouldFetchLookup = false;
		}

		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		if (fields == null) {
			fields = modBean.getAllFields(moduleName);
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
		}
		
		fetchLookup = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fields)) {
			List<SupplementRecord> supplements = new ArrayList<>(); // TODO remove LOOKUP_FIELD_META_LIST if no dependency
			for (FacilioField f : fields) {
				if ((f instanceof LookupField || f instanceof MultiLookupField)) {
					if (shouldFetchLookup && (!customLookupsOnly || !f.isDefault())) {
						if (f instanceof MultiLookupField) {
							supplements.add((MultiLookupField) f);
						}
						else {
							fetchLookup.add((LookupField) f);
						}
					}
				}
				else if (f instanceof MultiEnumField) {
					supplements.add((MultiEnumField) f);
				}
			}
			if (CollectionUtils.isNotEmpty(fetchLookup)) {
				context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, fetchLookup);
			}
			 if (!supplements.isEmpty()) {
	            List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
	            if (supplementFields == null) {
	                supplementFields = new ArrayList<>();
	                context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);
	            }
	            supplementFields.addAll(supplements);
	        }
		}
		
		return false;
	}

}
