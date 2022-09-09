package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

public class LoadMultiResourcesLookUpFieldsCommandV3 extends FacilioCommand{
	 @Override
	    public boolean executeCommand(Context context) throws Exception {
		 
		 	String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
	        ModuleBean modBean = Constants.getModBean();

	        FacilioModule module = modBean.getModule(moduleName);

	        Objects.requireNonNull(module,"Module "+moduleName+" doesn't exists.");

	        List<FacilioField> fields = modBean.getAllFields(moduleName);
	        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

	        List<LookupField> fetchLookup = new ArrayList<LookupField>();
	        
	        fetchLookup.add((LookupField) fieldsAsMap.get("space"));
	        fetchLookup.add((LookupField) fieldsAsMap.get("asset"));

	        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,fetchLookup);

	        return false;
	 }
}
