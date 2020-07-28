package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetContractListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		List<LookupField>fetchLookup = new ArrayList<LookupField>();
		fetchLookup.add((LookupField) fieldsAsMap.get("vendor"));
		for (FacilioField f : fields) {
			if (!f.isDefault() && f.getDataTypeEnum() == FieldType.LOOKUP) {
				fetchLookup.add((LookupField) f);
			}
		}
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST,fetchLookup);

		JSONObject filters = (JSONObject)context.get(FacilioConstants.ContextNames.FILTERS);
		JSONObject status = new JSONObject();
		JSONArray array = new JSONArray();
		array.add(String.valueOf(ContractsContext.Status.REVISED.getValue()));

		status.put("operatorId", (long) NumberOperators.NOT_EQUALS.getOperatorId());
		status.put("value", array);

		if(filters == null){
			filters = new JSONObject();
		}
		filters.put("status", status);

		context.put(FacilioConstants.ContextNames.FILTERS, filters);

		return false;
	}
	
	
}
