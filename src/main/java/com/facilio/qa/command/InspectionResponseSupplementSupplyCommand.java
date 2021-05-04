package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;

public class InspectionResponseSupplementSupplyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));

	        List<SupplementRecord> supplements = new ArrayList<>(); // Change size when new field is added
	        supplements.add((SupplementRecord) fieldMap.get("parent"));
	        supplements.add((SupplementRecord) fieldMap.get("resource"));
	        supplements.add((SupplementRecord) fieldMap.get("vendor"));
	        supplements.add((SupplementRecord) fieldMap.get("tenant"));
	        supplements.add((SupplementRecord) fieldMap.get("category"));
	        supplements.add((SupplementRecord) fieldMap.get("priority"));

	        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
	        
			return false;
	}

}
