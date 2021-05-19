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

public class AddCommonSupplementsForQAndACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
    	
    	 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.Inspection.INSPECTION_TEMPLATE));

         List<SupplementRecord> supplements = new ArrayList<>(2); // Change size when new field is added
         supplements.add((SupplementRecord) fieldMap.get("sysCreatedBy"));
         supplements.add((SupplementRecord) fieldMap.get("sysModifiedBy"));

         context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);
    	
		return false;
	}

}
