package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;

public class InspectionSupplementSupplyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(Constants.getModuleName(context)));

	        List<SupplementRecord> supplements = new ArrayList<>(); // Change size when new field is added
	        
//	        supplements.add((SupplementRecord) fieldMap.get("sysCreatedBy"));
//	        supplements.add((SupplementRecord) fieldMap.get("sysModifiedBy"));
	        
	        if( Constants.getModuleName(context).equals(FacilioConstants.Inspection.INSPECTION_RESPONSE)) {
	        	supplements.add((SupplementRecord) fieldMap.get("parent"));
	        }
	        if( Constants.getModuleName(context).equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)) {
	        	supplements.add((SupplementRecord) fieldMap.get("assetCategory"));
	        	supplements.add((SupplementRecord) fieldMap.get("spaceCategory"));
	        	supplements.add((SupplementRecord) fieldMap.get("sites"));
	        	supplements.add((SupplementRecord) fieldMap.get("buildings"));
	        }
	        supplements.add((SupplementRecord) fieldMap.get("resource"));
	        supplements.add((SupplementRecord) fieldMap.get("vendor"));
	        supplements.add((SupplementRecord) fieldMap.get("tenant"));
	        supplements.add((SupplementRecord) fieldMap.get("category"));
	        supplements.add((SupplementRecord) fieldMap.get("priority"));
	        supplements.add((SupplementRecord) fieldMap.get("assignmentGroup"));
	        supplements.add((SupplementRecord) fieldMap.get("assignedTo"));

	        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
	        
			return false;
	}

}
