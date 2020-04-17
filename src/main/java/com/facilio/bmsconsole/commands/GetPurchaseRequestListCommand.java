package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetPurchaseRequestListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		List<LookupField>fetchLookup = Arrays.asList((LookupField) fieldsAsMap.get("vendor"),(LookupField) fieldsAsMap.get("storeRoom"));
		for (FacilioField f : fields) {
			if (!f.isDefault() && f.getDataTypeEnum() == FieldType.LOOKUP) {
				fetchLookup.add((LookupField) f);
			}
		}
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);
		
		return false;
	}

}
