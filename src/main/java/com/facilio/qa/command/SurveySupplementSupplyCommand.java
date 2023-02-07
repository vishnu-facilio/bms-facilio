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

public class SurveySupplementSupplyCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(Constants.getModuleName(context)));

	        List<SupplementRecord> supplements = new ArrayList<>(); // Change size when new field is added
	        
//	        supplements.add((SupplementRecord) fieldMap.get("sysCreatedBy"));
//	        supplements.add((SupplementRecord) fieldMap.get("sysModifiedBy"));
	        
	        if( Constants.getModuleName(context).equals(FacilioConstants.Survey.SURVEY_RESPONSE)) {
	        	supplements.add((SupplementRecord) fieldMap.get("parent"));
	        }
	        if( Constants.getModuleName(context).equals(FacilioConstants.Survey.SURVEY_TEMPLATE)) {
	        	supplements.add((SupplementRecord) fieldMap.get("assetCategory"));
	        	supplements.add((SupplementRecord) fieldMap.get("spaceCategory"));
	        	supplements.add((SupplementRecord) fieldMap.get("sites"));
	        	supplements.add((SupplementRecord) fieldMap.get("buildings"));
	        }
	        if(fieldMap.containsKey("resource")) {
	        	supplements.add((SupplementRecord) fieldMap.get("resource"));
	        }
	        if(fieldMap.containsKey("assignedTo")) {
	        	supplements.add((SupplementRecord) fieldMap.get("assignedTo"));
	        }
			if(fieldMap.containsKey("parentId")) {
				supplements.add((SupplementRecord) fieldMap.get("parentId"));
			}

	        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
	        
			return false;
	}

}
